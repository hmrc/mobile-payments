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

import play.api.test.Helpers.await
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.connectors.OpenBankingConnector
import uk.gov.hmrc.mobilepayments.domain.dto.response.{InitiatePaymentResponse, OpenBankingPaymentStatusResponse, SessionDataResponse}
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.domain.{AmountInPence, Bank}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class OpenBankingServiceSpec extends BaseSpec with MobilePaymentsTestData {

  private val mockConnector: OpenBankingConnector = mock[OpenBankingConnector]
  private val amount:        BigDecimal           = 102.85
  private val amountInPence: AmountInPence        = AmountInPence(amount)
  private val saUtr:         SaUtr                = SaUtr("CS700100A")
  private val bankId:        String               = "asd-123"
  private val sessionDataId: String               = "51cc67d6-21da-11ec-9621-0242ac130002"
  private val returnUrl:     String               = "https://tax.service.gov.uk/mobile-payments/ob-payment-result"

  private val sut = new OpenBankingService(mockConnector, returnUrl)

  "when getBanks invoked and connector returns success with banks then" should {
    "return banks" in {
      mockBanks(Future successful banksResponse)

      val result = Await.result(sut.getBanks(journeyId), 0.5.seconds)
      result.data.size shouldBe 10
    }
  }

  "when getBanks invoked and connector returns NotFoundException then" should {
    "return an error" in {
      mockBanks(Future failed UpstreamErrorResponse("Error", 400, 400))

      intercept[UpstreamErrorResponse] {
        await(sut.getBanks(journeyId))
      }
    }
  }

  "when createSession invoked and connector succeeds then" should {
    "return session data response" in {
      mockSession(Future successful sessionDataResponse)

      val result = Await.result(sut.createSession(amount, saUtr, journeyId), 0.5.seconds)
      result.sessionDataId shouldEqual "51cc67d6-21da-11ec-9621-0242ac130002"
    }
  }

  "when createSession invoked and connector fails then" should {
    "return an error" in {
      mockSession(Future failed UpstreamErrorResponse("Error", 400, 400))

      intercept[UpstreamErrorResponse] {
        Await.result(sut.createSession(amount, saUtr, journeyId), 0.5.seconds)
      }
    }
  }

  "when selectBank invoked and connector succeeds then" should {
    "return unit response" in {
      mockSelectBank(Future successful HttpResponse.apply(200, ""))

      val result = Await.result(sut.selectBank(sessionDataId, bankId, journeyId), 0.5.seconds)
      result shouldEqual ()
    }
  }

  "when selectBank invoked and connector fails then" should {
    "return an error" in {
      mockSelectBank(Future failed UpstreamErrorResponse("Error", 400, 400))

      intercept[UpstreamErrorResponse] {
        Await.result(sut.selectBank(sessionDataId, bankId, journeyId), 0.5.seconds)
      }
    }
  }

  "when initiatePayment invoked and connector succeeds then" should {
    "return payment session response" in {
      mockInitiatePayment(Future successful paymentInitiatedResponse)

      val result = Await.result(sut.initiatePayment(sessionDataId, journeyId), 0.5.seconds)
      result.paymentUrl shouldEqual "https://some-bank.com?param=dosomething"
    }
  }

  "when initiatePayment invoked and connector fails then" should {
    "return an error" in {
      mockInitiatePayment(Future failed UpstreamErrorResponse("Error", 400, 400))

      intercept[UpstreamErrorResponse] {
        Await.result(sut.initiatePayment(sessionDataId, journeyId), 0.5.seconds)
      }
    }
  }

  "when getPaymentStatus invoked and connector getPaymentStatus returns success then" should {
    "return banks" in {
      mockPaymentStatus(Future successful paymentStatusOpenBankingResponse)

      val result = Await.result(sut.getPaymentStatus(sessionDataId, journeyId), 0.5.seconds)
      result.status shouldEqual "Authorised"
    }
  }

  "when getPaymentStatus invoked and connector getPaymentStatus returns NotFoundException then" should {
    "return an error" in {
      mockPaymentStatus(Future failed UpstreamErrorResponse("Error", 400, 400))

      intercept[UpstreamErrorResponse] {
        await(sut.getPaymentStatus(sessionDataId, journeyId))
      }
    }
  }

  private def mockBanks(future: Future[List[Bank]]): Unit =
    (mockConnector
      .getBanks(_: JourneyId)(_: HeaderCarrier))
      .expects(journeyId, hc)
      .returning(future)

  private def mockSession(future: Future[SessionDataResponse]): Unit =
    (mockConnector
      .createSession(_: AmountInPence, _: SaUtr, _: JourneyId)(_: HeaderCarrier))
      .expects(amountInPence, saUtr, journeyId, hc)
      .returning(future)

  private def mockSelectBank(future: Future[HttpResponse]): Unit =
    (mockConnector
      .selectBank(_: String, _: String, _: JourneyId)(_: HeaderCarrier))
      .expects(sessionDataId, bankId, journeyId, hc)
      .returning(future)

  private def mockInitiatePayment(future: Future[InitiatePaymentResponse]): Unit =
    (mockConnector
      .initiatePayment(_: String, _: String, _: JourneyId)(_: HeaderCarrier))
      .expects(sessionDataId, returnUrl, journeyId, hc)
      .returning(future)

  private def mockPaymentStatus(future: Future[OpenBankingPaymentStatusResponse]): Unit =
    (mockConnector
      .getPaymentStatus(_: String, _: JourneyId)(_: HeaderCarrier))
      .expects(sessionDataId, journeyId, hc)
      .returning(future)
}
