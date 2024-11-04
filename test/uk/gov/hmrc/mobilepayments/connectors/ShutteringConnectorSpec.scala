/*
 * Copyright 2024 HM Revenue & Customs
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
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.mocks.ConnectorStub

import scala.concurrent.Future

class ShutteringConnectorSpec extends BaseSpec with ConnectorStub with MobilePaymentsTestData {

  val sut = new ShutteringConnector(mockHttp, "https://baseUrl")

  "when getShutteringStatus call is successful it" should {
    "return shuttering true" in {
      performGET(Future.successful(shutteredResponse))(mockHttp, mockRequestBuilder)
      await(sut.getShutteringStatus(journeyId)).shuttered shouldBe true
    }
  }

  "when getShutteringStatus call returns Upstream5xxResponse it" should {
    "return shuttering false" in {
      performGET(Future.failed(UpstreamErrorResponse("error", 500, 500)))(mockHttp, mockRequestBuilder)
      await(sut.getShutteringStatus(journeyId)).shuttered shouldBe false
    }
  }
}
