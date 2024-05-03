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

package uk.gov.hmrc.mobilepayments.connectors

import com.google.inject.Inject
import com.google.inject.name.Named
import openbanking.cor.model.request.InitiateEmailSendRequest
import openbanking.cor.model.response.{CreateSessionDataResponse, InitiatePaymentResponse}
import openbanking.cor.model.{OriginSpecificSessionData, SessionData}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http._
import uk.gov.hmrc.mobilepayments.domain.dto.request._
import uk.gov.hmrc.mobilepayments.domain.dto.response._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.domain.Bank

import java.time.LocalDate
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenBankingConnector @Inject() (
  http:                              HttpClient,
  @Named("open-banking") serviceUrl: String
)(implicit ex:                       ExecutionContext) {

  def getBanks(journeyId: JourneyId)(implicit headerCarrier: HeaderCarrier): Future[List[Bank]] =
    http.GET[List[Bank]](
      url = s"$serviceUrl/open-banking/banks?journeyId=${journeyId.value}"
    )

  def createSession(
    amount:                 BigDecimal,
    originSpecificData: OriginSpecificData,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[CreateSessionDataResponse] = {
    http.POST[CreateSessionDataRequest, CreateSessionDataResponse](
      url = s"$serviceUrl/open-banking/session?journeyId=${journeyId.value}",
      CreateSessionDataRequest(amount, originSpecificData),
      Seq(("X-Session-ID", journeyId.value))
    )
  }

  def getSession(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[SessionData[OriginSpecificSessionData]] = {
    val journey = journeyId.value
    http.GET[SessionData[OriginSpecificSessionData]](
      url = s"$serviceUrl/open-banking/session/$sessionDataId?journeyId=$journey"
    )
  }

  def selectBank(
    sessionDataId:          String,
    bankId:                 String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[HttpResponse] =
    http.POST[SelectBankRequest, HttpResponse](
      url = s"$serviceUrl/open-banking/session/$sessionDataId/select-bank?journeyId=${journeyId.value}",
      SelectBankRequest(bankId)
    )

  def initiatePayment(
    sessionDataId:          String,
    returnUrl:              String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[InitiatePaymentResponse] =
    http.POST[InitiatePaymentRequest, InitiatePaymentResponse](
      url = s"$serviceUrl/open-banking/session/$sessionDataId/initiate-payment?journeyId=${journeyId.value}",
      InitiatePaymentRequest(returnUrl)
    )

  def getPaymentStatus(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[OpenBankingPaymentStatusResponse] =
    http.GET[OpenBankingPaymentStatusResponse](
      url = s"$serviceUrl/open-banking/session/$sessionDataId/payment-status?journeyId=${journeyId.value}"
    )

  def urlConsumed(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Boolean] =
    http.GET[Boolean](
      s"$serviceUrl/open-banking/session/$sessionDataId/url-consumed?journeyId=${journeyId.value}"
    )

  def clearPayment(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Unit] =
    http.DELETE[Unit](
      s"$serviceUrl/open-banking/session/$sessionDataId/clear-payment?journeyId=${journeyId.value}"
    )

  def setEmail(
    sessionDataId:          String,
    email:                  String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Unit] =
    http
      .POST[SetEmailRequest, Unit](
        url = s"$serviceUrl/open-banking/session/$sessionDataId/set-email?journeyId=${journeyId.value}",
        SetEmailRequest(email)
      )

  def setFutureDate(
    sessionDataId:          String,
    maybeFutureDate:        LocalDate,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Unit] =
    http
      .POST[SetFutureDateRequest, Unit](
        url = s"$serviceUrl/open-banking/session/$sessionDataId/update-date?journeyId=${journeyId.value}",
        SetFutureDateRequest(maybeFutureDate)
      )

  def clearFutureDate(
                       sessionDataId:          String,
                       journeyId:              JourneyId
                     )(implicit headerCarrier: HeaderCarrier
                     ): Future[Unit] =
    http
      .POSTEmpty[Unit](
        s"$serviceUrl/open-banking/session/$sessionDataId/update-date?journeyId=${journeyId.value}"
      )

  def sendEmail(
    sessionDataId:          String,
    journeyId:              JourneyId,
    taxType:                 String
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Unit] =
    http.POST[InitiateEmailSendRequest, Unit](
      s"$serviceUrl/open-banking/session/$sessionDataId/send-email?journeyId=${journeyId.value}",
      InitiateEmailSendRequest("en", taxType)
    )

  def setEmailSentFlag(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Unit] =
    http.POSTEmpty[Unit](
      s"$serviceUrl/open-banking/session/$sessionDataId/set-email-sent-flag?journeyId=${journeyId.value}"
    )

  def clearEmail(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Unit] =
    http.DELETE[Unit](
      s"$serviceUrl/open-banking/session/$sessionDataId/clear-email?journeyId=${journeyId.value}"
    )
}
