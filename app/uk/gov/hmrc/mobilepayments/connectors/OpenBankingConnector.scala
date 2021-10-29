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

package uk.gov.hmrc.mobilepayments.connectors

import com.google.inject.Inject
import com.google.inject.name.Named
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http._
import uk.gov.hmrc.mobilepayments.domain.dto.request.{CreateSessionDataRequest, InitiatePaymentRequest, SelectBankRequest}
import uk.gov.hmrc.mobilepayments.domain.dto.response._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenBankingConnector @Inject() (
  http:                              HttpClient,
  @Named("open-banking") serviceUrl: String
)(implicit ex:                       ExecutionContext) {

  def getBanks(journeyId: JourneyId)(implicit headerCarrier: HeaderCarrier): Future[BanksResponse] = {
    val journey = journeyId.value
    http.GET[BanksResponse](
      url = s"$serviceUrl/open-banking/banks?journeyId=$journey"
    )
  }

  def createSession(
    amount:                 Long,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[SessionDataResponse] = {
    val journey = journeyId.value
    http.POST[CreateSessionDataRequest, SessionDataResponse](
      url = s"$serviceUrl/open-banking/session?journeyId=$journey",
      CreateSessionDataRequest(amount)
    )
  }

  def selectBank(
    sessionDataId:          String,
    bankId:                 String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[HttpResponse] = {
    val journey = journeyId.value
    http.POST[SelectBankRequest, HttpResponse](
      url = s"$serviceUrl/open-banking/session/$sessionDataId/select-bank?journeyId=$journey",
      SelectBankRequest(bankId)
    )
  }

  def initiatePayment(
    sessionDataId:          String,
    returnUrl:              String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[InitiatePaymentResponse] = {
    val journey = journeyId.value
    http.POST[InitiatePaymentRequest, InitiatePaymentResponse](
      url = s"$serviceUrl/open-banking/session/$sessionDataId/initiate-payment?journeyId=$journey",
      InitiatePaymentRequest(returnUrl)
    )
  }

  def getPaymentStatus(
    sessionDataId:          String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[OpenBankingPaymentStatusResponse] = {
    val journey = journeyId.value
    http.GET[OpenBankingPaymentStatusResponse](
      url = s"$serviceUrl/open-banking/session/$sessionDataId/payment-status?journeyId=$journey"
    )
  }
}
