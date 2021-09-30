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

import play.api.test.Helpers.await
import uk.gov.hmrc.http.{NotFoundException, _}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.mocks.ConnectorStub

import scala.concurrent.Future

class OpenBankingConnectorSpec extends BaseSpec with ConnectorStub with MobilePaymentsTestData {
  val mockHttp:                   HttpClient    = mock[HttpClient]
  implicit val mockHeaderCarrier: HeaderCarrier = mock[HeaderCarrier]

  val sut = new OpenBankingConnector(mockHttp, "baseUrl")

  "when getBanks call is successful it" should {
    "return banks" in {
      performSuccessfulGET(Future.successful(banksResponse))(mockHttp)
      await(sut.getBanks(journeyId)).data.size shouldBe 4
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
      performSuccessfulPOST(Future.successful(sessionDataResponse))(mockHttp)
      val result = await(sut.createSession(123L, journeyId))
      result.sessionDataId shouldEqual "51cc67d6-21da-11ec-9621-0242ac130002"
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
}
