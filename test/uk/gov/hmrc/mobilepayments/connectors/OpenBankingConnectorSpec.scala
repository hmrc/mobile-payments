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

import org.scalatest.concurrent.ScalaFutures
import play.api.test.Helpers.await
import uk.gov.hmrc.http.{NotFoundException, _}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.mocks.ConnectorStub

import scala.concurrent.Future

class OpenBankingConnectorSpec extends BaseSpec with ConnectorStub with MobilePaymentsTestData with ScalaFutures {
  val mockHttp:                   HttpClient    = mock[HttpClient]
  implicit val mockHeaderCarrier: HeaderCarrier = mock[HeaderCarrier]

  val sut           = new OpenBankingConnector(mockHttp, "baseUrl")
  val sessionDataId = "51cc67d6-21da-11ec-9621-0242ac130002"
  val returnUrl     = "https://tax.hmrc.gov.uk/payment-result"

  "when getBanks call is successful it" should {
    "return banks" in {
      performSuccessfulGET(Future successful banksResponse)(mockHttp)
      await(sut.getBanks(journeyId)).size shouldBe 19
    }
  }

  "when getBanks call returns NotFoundException it" should {
    "return an error" in {
      performUnsuccessfulGET(new NotFoundException("not found"))(mockHttp)
      intercept[NotFoundException] {
        await(sut.getBanks(journeyId))
      }
    }
  }

  "when createSession call is successful it" should {
    "return session data" in {
      performSuccessfulPOST(Future successful sessionDataResponse)(mockHttp)
      val result = await(sut.createSession(123L, journeyId))
      result.sessionDataId shouldEqual sessionDataId
      result.nextUrl shouldEqual "https://api.foo.com"
    }
  }

  "when createSession call returns NotFoundException it" should {
    "return an error" in {
      performUnsuccessfulPOST(new NotFoundException("not found"))(mockHttp)
      intercept[NotFoundException] {
        await(sut.createSession(123L, journeyId))
      }
    }
  }

  "when selectBank call is successful it" should {
    "return success" in {
      performSuccessfulPOST(Future successful HttpResponse.apply(200, ""))(mockHttp)
      val result = await(sut.selectBank(sessionDataId, "123-asd", journeyId))
      result.status shouldEqual 200
    }
  }

  "when selectBank call returns NotFoundException it" should {
    "return error" in {
      performUnsuccessfulPOST(new NotFoundException("not found"))(mockHttp)
      intercept[NotFoundException] {
        await(sut.selectBank(sessionDataId, "123-asd", journeyId))
      }
    }
  }

  "when initiatePayment call is successful it" should {
    "return payment url" in {
      performSuccessfulPOST(Future successful paymentInitiatedResponse)(mockHttp)
      val result = await(sut.initiatePayment(sessionDataId, returnUrl, journeyId))
      result.paymentUrl shouldEqual "https://some-bank.com?param=dosomething"
    }
  }

  "when initiatePayment call returns NotFoundException it" should {
    "return an error" in {
      performUnsuccessfulPOST(new NotFoundException("not found"))(mockHttp)
      intercept[NotFoundException] {
        await(sut.initiatePayment(sessionDataId, returnUrl, journeyId))
      }
    }
  }

  "when getPaymentStatus call is successful it" should {
    "return banks" in {
      performSuccessfulGET(Future successful paymentStatusOpenBankingResponse)(mockHttp)
      await(sut.getPaymentStatus(sessionDataId, journeyId)).ecospendPaymentStatus shouldEqual "Authorised"
    }
  }

  "when getPaymentStatus call returns NotFoundException it" should {
    "return an error" in {
      performUnsuccessfulGET(new NotFoundException("not found"))(mockHttp)
      intercept[NotFoundException] {
        await(sut.getPaymentStatus(sessionDataId, journeyId))
      }
    }
  }
}
