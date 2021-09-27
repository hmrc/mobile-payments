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
import uk.gov.hmrc.http._
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.controllers.errors.UpstreamError
import uk.gov.hmrc.mobilepayments.domain.BanksResponse
import uk.gov.hmrc.mobilepayments.mocks.{ConnectorStub, MockResponses}

import scala.concurrent.Future

class OpenBankingConnectorSpec extends BaseSpec with ConnectorStub with MockResponses {
  val mockGet:                    CoreGet       = mock[CoreGet]
  implicit val mockHeaderCarrier: HeaderCarrier = mock[HeaderCarrier]

  val sut = new OpenBankingConnector(mockGet, "baseUrl")

  "when getBanks call is successful it" should {
    "return banks" in {
      val getSuccess: Future[BanksResponse] = Future.successful(banksResponse)
      performSuccessfulGET(getSuccess)(mockGet)
      await(sut.getBanks(journeyId)).right.get.data.size shouldBe 2
    }
  }

  "when getBanks call returns UpstreamErrorResponse it" should {
    "return an error" in {
      val error: UpstreamErrorResponse = UpstreamErrorResponse("error", 500)
      performUnsuccessfulGET(error)(mockGet)
      await(sut.getBanks(journeyId)).left.get shouldBe UpstreamError("error")
    }
  }
}
