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
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.connectors.OpenBankingConnector
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.mocks.MockResponses

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class OpenBankingServiceSpec extends BaseSpec with MockResponses {

  val mockConnector: OpenBankingConnector = mock[OpenBankingConnector]

  private val sut = new OpenBankingService(mockConnector)

  "test when connector returns success with banks then" should {
    "return banks" in {
      (mockConnector
        .getBanks(_: JourneyId)(_: HeaderCarrier))
        .expects(journeyId, hc)
        .returning(Future.successful(banksResponse))

      val result = Await.result(sut.getBanks(journeyId), 0.5.seconds)
      result.data.size shouldBe 2
    }
  }

  "test when connector returns NotFoundException then" should {
    "return an error" in {
      (mockConnector
        .getBanks(_: JourneyId)(_: HeaderCarrier))
        .expects(journeyId, hc)
        .returning(Future.failed(UpstreamErrorResponse("Error", 400, 400)))
      intercept[UpstreamErrorResponse] {
        await(sut.getBanks(journeyId))
      }
    }
  }
}
