/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.mobilepayments.services

import com.google.inject.{Inject, Singleton}
import openbanking.cor.model._
import openbanking.cor.model.response.{CreateSessionDataResponse, InitiatePaymentResponse}
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}
import uk.gov.hmrc.mobilepayments.connectors.OpenBankingConnector
import uk.gov.hmrc.mobilepayments.controllers.errors.MalformedRequestException
import uk.gov.hmrc.mobilepayments.domain.dto.request.{CreateSessionRequest, SelfAssessmentOriginSpecificData, SimpleAssessmentOriginSpecificData, TaxTypeEnum}
import uk.gov.hmrc.mobilepayments.domain.dto.response._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.domain.{Bank, BankGroupData}

import java.time.LocalDate
import javax.inject.Named
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenBankingService @Inject() (
  connector:                                                         OpenBankingConnector,
  @Named("openBankingPaymentReturnUrl") openBankingPaymentReturnUrl: String) {

  def getBanks(
    journeyId:                 JourneyId
  )(implicit executionContext: ExecutionContext,
    headerCarrier:             HeaderCarrier
  ): Future[BanksResponse] =
    for {
      rawBanks     <- connector.getBanks(journeyId)
      groupedBanks <- groupBanks(rawBanks)
    } yield {
      BanksResponse(groupedBanks)
    }

  def createSession(
    request:                CreateSessionRequest,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[CreateSessionDataResponse] =
    if (request.taxType.isDefined) {
      request.taxType match {
        case Some(TaxTypeEnum.appSelfAssessment) =>
          (request.amountInPence, request.reference) match {
            case (Some(amountInPence), Some(reference)) =>
              connector.createSession(amountInPence, SelfAssessmentOriginSpecificData(SaUtr(reference)), journeyId)
            case _ =>
              throw new MalformedRequestException("Malformed Json")
          }
        case Some(TaxTypeEnum.appSimpleAssessment) =>
          (request.amountInPence, request.reference) match {
            case (Some(amountInPence), Some(reference)) =>
              connector.createSession(amountInPence, SimpleAssessmentOriginSpecificData(reference), journeyId)
            case _ =>
              throw new MalformedRequestException("Malformed Json")
          }
        case _ =>
          throw new BadRequestException("Incorrect Tax Type returned")

      }
    } else {
      (request.amount, request.saUtr) match {
        case (Some(amount), Some(saUtr)) =>
          connector
            .createSession(BigDecimal((amount * 100).longValue), SelfAssessmentOriginSpecificData(saUtr), journeyId)
        case _ => throw new MalformedRequestException("Malformed Json")
      }
    }

  def getSession(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[SessionDataResponse] =
    connector
      .getSession(sessionDataId, journeyId)
      .map { data: SessionData[OriginSpecificSessionData] =>
        val ptaSa = data.originSpecificData.asInstanceOf[AppSaSessionData]

        val bankId: Option[String] = data.sessionState match {
          case SessionInitiated => None
          case t: BankSelected     => Some(t.bankId.value)
          case t: PaymentInitiated => Some(t.bankId.value)
          case t: PaymentFinished  => Some(t.bankId.value)
          case t: PaymentFinalised => Some(t.bankId.value)
        }

        val paymentDate: Option[LocalDate] = data.sessionState match {
          case SessionInitiated => None
          case _: BankSelected     => None
          case _: PaymentInitiated => None
          case _: PaymentFinished  => Some(LocalDate.now())
          case t: PaymentFinalised => Some(t.dateFinalised)
        }

        val state: String = data.sessionState match {
          case SessionInitiated => "SessionInitiated"
          case _: BankSelected     => "BankSelected"
          case _: PaymentInitiated => "PaymentInitiated"
          case _: PaymentFinished  => "PaymentFinished"
          case _: PaymentFinalised => "PaymentFinalised"
        }

        val email: Option[String] = data.sessionState match {
          case SessionInitiated => None
          case _: BankSelected     => None
          case t: PaymentInitiated => t.email.map(_.value)
          case t: PaymentFinished  => t.email.map(_.value)
          case t: PaymentFinalised => t.email.map(_.value)
        }

        val emailSent: Option[Boolean] = data.sessionState match {
          case SessionInitiated => None
          case _: BankSelected     => None
          case _: PaymentInitiated => None
          case t: PaymentFinished  => t.emailSent
          case t: PaymentFinalised => t.emailSent
        }

        SessionDataResponse(
          sessionDataId = data._id.value,
          amount        = data.amount.inPounds,
          bankId        = bankId,
          state         = state,
          createdOn     = data.createdOn,
          paymentDate   = paymentDate,
          saUtr         = SaUtr(ptaSa.saUtr.value),
          email         = email,
          emailSent     = emailSent
        )
      }

  def selectBank(
    sessionDataId:          String,
    bankId:                 String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[Unit] = connector.selectBank(sessionDataId, bankId, journeyId).map(_ => ())

  def initiatePayment(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[InitiatePaymentResponse] =
    connector.initiatePayment(sessionDataId, openBankingPaymentReturnUrl, journeyId)

  def updatePayment(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[InitiatePaymentResponse] =
    connector
      .clearPayment(sessionDataId, journeyId)
      .flatMap(_ => connector.initiatePayment(sessionDataId, openBankingPaymentReturnUrl, journeyId))

  def urlConsumed(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[UrlConsumedResponse] =
    connector
      .urlConsumed(sessionDataId, journeyId)
      .map(t => UrlConsumedResponse(consumed = t))

  def getPaymentStatus(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[PaymentStatusResponse] =
    connector
      .getPaymentStatus(sessionDataId, journeyId)
      .map { t =>
        PaymentStatusResponse(t.ecospendPaymentStatus)
      }

  def setEmail(
    sessionDataId:          String,
    email:                  String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[Unit] = connector.setEmail(sessionDataId, email, journeyId)

  private def groupBanks(banks: List[Bank])(implicit hc: HeaderCarrier): Future[List[BankGroupData]] =
    Future successful banks
      .groupBy(_.group)
      .values
      .toList
      .map(BankGroupData.buildBankGroupData)
      .sortWith((bankGroupData, nextBankGroupData) => bankGroupData.bankGroupName < nextBankGroupData.bankGroupName)

  def sendEmail(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[Unit] =
    for {
      _ <- connector.sendEmail(sessionDataId, journeyId)
      _ <- connector.setEmailSentFlag(sessionDataId, journeyId)
    } yield ()

  def clearEmail(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[Unit] = connector.clearEmail(sessionDataId, journeyId)
}
