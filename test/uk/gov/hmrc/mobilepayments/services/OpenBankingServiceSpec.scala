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

import akka.http.scaladsl.model.Uri
import openbanking.cor.model.response.{CreateSessionDataResponse, InitiatePaymentResponse}
import openbanking.cor.model.{OriginSpecificSessionData, SessionData}
import play.api.test.Helpers.await
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.connectors.OpenBankingConnector
import uk.gov.hmrc.mobilepayments.domain.dto.response.OpenBankingPaymentStatusResponse
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.domain.{AmountInPence, Bank}

import java.time.LocalDate
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
  private val paymentUrl:    Uri                  = "https://some-bank.com?param=dosomething"

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
      mockCreateSession(Future successful createSessionDataResponse)

      val result = Await.result(sut.createSession(amount, saUtr, journeyId), 0.5.seconds)
      result.sessionDataId.value shouldEqual "51cc67d6-21da-11ec-9621-0242ac130002"
    }
  }

  "when createSession invoked and connector fails then" should {
    "return an error" in {
      mockCreateSession(Future failed UpstreamErrorResponse("Error", 400, 400))

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
      result.paymentUrl shouldEqual paymentUrl
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

  "when updatePayment invoked" should {
    "return a new payment session response and clear the payment" in {
      mockClearPayment(Future successful ())
      mockInitiatePayment(Future successful paymentInitiatedUpdateResponse)

      val result = Await.result(sut.updatePayment(sessionDataId, journeyId), 0.5.seconds)
      result.paymentUrl.toString() shouldEqual "https://some-updated-bank.com?param=dosomething"
    }
  }

  "when updatePayment invoked and initiate payment fails" should {
    "return an error" in {
      mockClearPayment(Future successful ())
      mockInitiatePayment(Future failed UpstreamErrorResponse("Error", 400, 400))
      intercept[UpstreamErrorResponse] {
        await(sut.updatePayment(sessionDataId, journeyId))
      }
    }
  }

  "when urlConsumed invoked and url consumed succeeds" should {
    Seq(true, false).foreach { consumed =>
      s"return $consumed" in {
        mockUrlConsumed(Future successful consumed)

        val result = Await.result(sut.urlConsumed(sessionDataId, journeyId), 0.5.seconds)
        result.consumed shouldEqual consumed
      }
    }
  }

  "when urlConsumed invoked and url consumed fails" should {
    "return an error" in {
      mockUrlConsumed(Future failed UpstreamErrorResponse("Error", 400, 400))
      intercept[UpstreamErrorResponse] {
        await(sut.urlConsumed(sessionDataId, journeyId))
      }
    }
  }

  "when getSession invoked and status is initiated and connector getSession returns success then" should {
    "return session" in {
      mockSession(Future successful sessionInitiatedDataResponse)

      val result = Await.result(sut.getSession(sessionDataId, journeyId), 0.5.seconds)
      result.sessionDataId shouldEqual "51cc67d6-21da-11ec-9621-0242ac130002"
      result.amount shouldEqual BigDecimal.valueOf(125.64)
      result.bankId shouldEqual None
      result.state shouldEqual "SessionInitiated"
      result.paymentDate shouldEqual None
      result.saUtr.value shouldEqual "CS700100A"
    }
  }

  "when getSession invoked and status is bank selected and connector getSession returns success then" should {
    "return session" in {
      mockSession(Future successful sessionBankSelectedDataResponse)

      val result = Await.result(sut.getSession(sessionDataId, journeyId), 0.5.seconds)
      result.sessionDataId shouldEqual "51cc67d6-21da-11ec-9621-0242ac130002"
      result.amount shouldEqual BigDecimal.valueOf(125.64)
      result.bankId shouldEqual Some("a-bank-id")
      result.state shouldEqual "BankSelected"
      result.paymentDate shouldEqual None
      result.saUtr.value shouldEqual "CS700100A"
    }
  }

  "when getSession invoked and status is payment finished and connector getSession returns success then" should {
    "return session" in {
      mockSession(Future successful sessionPaymentFinishedDataResponse)

      val result = Await.result(sut.getSession(sessionDataId, journeyId), 0.5.seconds)
      result.sessionDataId shouldEqual "51cc67d6-21da-11ec-9621-0242ac130002"
      result.amount shouldEqual BigDecimal.valueOf(125.64)
      result.bankId shouldEqual Some("a-bank-id")
      result.state shouldEqual "PaymentFinished"
      result.paymentDate shouldEqual Some(LocalDate.now())
      result.saUtr.value shouldEqual "CS700100A"
    }
  }

  "when getSession invoked and status is payment finalised and connector getSession returns success then" should {
    "return session" in {
      mockSession(Future successful sessionPaymentFinalisedDataResponse)

      val result = Await.result(sut.getSession(sessionDataId, journeyId), 0.5.seconds)
      result.sessionDataId shouldEqual "51cc67d6-21da-11ec-9621-0242ac130002"
      result.amount shouldEqual BigDecimal.valueOf(125.64)
      result.bankId shouldEqual Some("a-bank-id")
      result.state shouldEqual "PaymentFinalised"
      result.paymentDate shouldEqual Some(LocalDate.parse("2021-12-01"))
      result.saUtr.value shouldEqual "CS700100A"
    }
  }

  "when getSession invoked and connector fails then" should {
    "return an error" in {
      mockSession(Future failed UpstreamErrorResponse("Error", 400, 400))

      intercept[UpstreamErrorResponse] {
        await(sut.getSession(sessionDataId, journeyId))
      }
    }
  }

  private def mockBanks(future: Future[List[Bank]]): Unit =
    (mockConnector
      .getBanks(_: JourneyId)(_: HeaderCarrier))
      .expects(journeyId, hc)
      .returning(future)

  private def mockCreateSession(future: Future[CreateSessionDataResponse]): Unit =
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

  private def mockUrlConsumed(future: Future[Boolean]): Unit =
    (mockConnector
      .urlConsumed(_: String, _: JourneyId)(_: HeaderCarrier))
      .expects(sessionDataId, journeyId, hc)
      .returning(future)

  private def mockClearPayment(future: Future[Unit]): Unit =
    (mockConnector
      .clearPayment(_: String, _: JourneyId)(_: HeaderCarrier))
      .expects(sessionDataId, journeyId, hc)
      .returning(future)

  private def mockSession(future: Future[SessionData[OriginSpecificSessionData]]): Unit =
    (mockConnector
      .getSession(_: String, _: JourneyId)(_: HeaderCarrier))
      .expects(sessionDataId, journeyId, hc)
      .returning(future)
}
