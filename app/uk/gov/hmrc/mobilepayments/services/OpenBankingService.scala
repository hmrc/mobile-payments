/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilepayments.connectors.OpenBankingConnector
import uk.gov.hmrc.mobilepayments.domain.dto.response._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.domain.{AmountInPence, Bank, BankGroupData}

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
    amount:                 BigDecimal,
    saUtr:                  SaUtr,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[SessionDataResponse] = connector.createSession(AmountInPence(amount), saUtr, journeyId)

  def getSession(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[SessionDataResponse] = connector.getSession(sessionDataId, journeyId)

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
    paymentUrl:             String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    executionContext:       ExecutionContext
  ): Future[InitiatePaymentResponse] =
    connector.initiatePayment(sessionDataId, openBankingPaymentReturnUrl, journeyId)

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

  private def groupBanks(banks: List[Bank])(implicit hc: HeaderCarrier): Future[List[BankGroupData]] =
    Future successful banks
      .groupBy(_.group)
      .values
      .toList
      .map(BankGroupData.buildBankGroupData)
      .sortWith((bankGroupData, nextBankGroupData) => bankGroupData.bankGroupName < nextBankGroupData.bankGroupName)

}
