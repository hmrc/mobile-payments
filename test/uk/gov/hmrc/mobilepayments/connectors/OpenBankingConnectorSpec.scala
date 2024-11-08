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

import openbanking.cor.model.SessionInitiated
import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers.await
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http._
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.dto.request.SelfAssessmentOriginSpecificData
import uk.gov.hmrc.mobilepayments.mocks.ConnectorStub

import java.time.LocalDateTime
import scala.concurrent.Future

class OpenBankingConnectorSpec extends BaseSpec with ConnectorStub with MobilePaymentsTestData with ScalaFutures {


  val sut           = new OpenBankingConnector(mockHttp, "https://baseUrl")
  val sessionDataId = "51cc67d6-21da-11ec-9621-0242ac130002"
  val returnUrl     = "https://tax.hmrc.gov.uk/payment-result"
  val amount        = BigDecimal((12500 * 100).longValue())

  "when getBanks call is successful it" should {
    "return banks" in {
      performGET(Future successful banksResponse)
      await(sut.getBanks(journeyId)).size shouldBe 19
    }
  }

  "when getBanks call returns NotFoundException it" should {
    "return an error" in {
      performGET(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.getBanks(journeyId))
      }
    }
  }

  "when createSession call is successful it" should {
    "return session data" in {
      performSuccessfulPOST(Future successful createSessionDataResponse)
      val result = await(sut.createSession(amount, SelfAssessmentOriginSpecificData(SaUtr("CS700100A")), journeyId))
      result.sessionDataId.value shouldEqual sessionDataId
    }
  }

  "when createSession call returns NotFoundException it" should {
    "return an error" in {
      performSuccessfulPOST(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.createSession(amount, SelfAssessmentOriginSpecificData(SaUtr("CS700100A")), journeyId))
      }
    }
  }

  "when selectBank call is successful it" should {
    "return success" in {
      performPOST(Future successful HttpResponse.apply(200, ""))
      val result = await(sut.selectBank(sessionDataId, "123-asd", journeyId))
      result.status shouldEqual 200
    }
  }

  "when selectBank call returns NotFoundException it" should {
    "return error" in {
      performPOST(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.selectBank(sessionDataId, "123-asd", journeyId))
      }
    }
  }

  "when initiatePayment call is successful it" should {
    "return payment url" in {
      performPOST(Future successful paymentInitiatedResponse)
      val result = await(sut.initiatePayment(sessionDataId, returnUrl, journeyId))
      result.paymentUrl.toString() shouldEqual "https://some-bank.com?param=dosomething"
    }
  }

  "when initiatePayment call returns NotFoundException it" should {
    "return an error" in {
      performPOST(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.initiatePayment(sessionDataId, returnUrl, journeyId))
      }
    }
  }

  "when getPaymentStatus call is successful it" should {
    "return banks" in {
      performGET(Future successful paymentStatusOpenBankingResponse)
      await(sut.getPaymentStatus(sessionDataId, journeyId)).ecospendPaymentStatus shouldEqual "Verified"
    }
  }

  "when getPaymentStatus call returns NotFoundException it" should {
    "return an error" in {
      performGET(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.getPaymentStatus(sessionDataId, journeyId))
      }
    }
  }

  "when urlConsumed call is successful it" should {
    Seq(true, false).foreach { consumed =>
      s"return $consumed" in {
        performGET(Future successful consumed)
        val result = await(sut.urlConsumed(sessionDataId, journeyId))
        result shouldBe consumed
      }
    }
  }

  "when urlConsumed call returns NotFoundException it" should {
    "return an error" in {
      performGET(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.urlConsumed(sessionDataId, journeyId))
      }
    }
  }

  "when clearPayment call is successful it" should {
    "return unit" in {
      performDELETE(Future successful ())
      val result: Unit = await(sut.clearPayment(sessionDataId, journeyId))
      result shouldBe ()
    }
  }

  "when clearPayment call returns NotFoundException it" should {
    "return an error" in {
      performDELETE(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.clearPayment(sessionDataId, journeyId))
      }
    }
  }

  "when getSession call is successful it" should {
    "return session" in {
      performGET(Future successful sessionInitiatedDataResponse)
      val result = await(sut.getSession(sessionDataId, journeyId))
      result._id.value       shouldBe "51cc67d6-21da-11ec-9621-0242ac130002"
      result.sessionId.value shouldBe "a-session-id"
      result.amount.value    shouldBe 12564
      result.sessionState    shouldBe SessionInitiated
      result.createdOn       shouldBe LocalDateTime.parse("2021-11-03T10:15:30")
    }
  }

  "when getSession call returns NotFoundException it" should {
    "return an error" in {
      performGET(Future.failed(new NotFoundException("not found")))
      intercept[NotFoundException] {
        await(sut.getSession(sessionDataId, journeyId))
      }
    }
  }
}
