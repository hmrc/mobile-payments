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

package uk.gov.hmrc.mobilepayments.controllers

import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse, Upstream5xxResponse}
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.BanksResponse
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, MockResponses}
import uk.gov.hmrc.mobilepayments.services.OpenBankingService
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.{ExecutionContext, Future}

class LiveBankControllerSpec extends BaseSpec with AuthorisationStub with MockResponses {

  private val confidenceLevel: ConfidenceLevel    = ConfidenceLevel.L200
  private val mockService:     OpenBankingService = mock[OpenBankingService]

  implicit val mockAuditConnector: AuditConnector = mock[AuditConnector]
  implicit val mockAuthConnector:  AuthConnector  = mock[AuthConnector]

  private val sut = new LiveBankController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    Helpers.stubControllerComponents(),
    mockService
  )

  "when get banks invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      mockGetBanks(Future.successful(banksResponse))

      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[BanksResponse]
      response.data.size shouldBe 2
    }
  }

  "when get banks invoked and service returns NotFoundException then" should {
    "return 404" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      mockGetBanks(Future.failed(Upstream4xxResponse("Error", 404, 404)))

      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 404
    }
  }

  "when get banks invoked and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      mockGetBanks(Future.failed(new Upstream4xxResponse("Error", 401, 401)))

      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get banks invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get banks invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      mockGetBanks(Future.failed(new Upstream5xxResponse("Error", 502, 502)))

      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 500
    }
  }

  private def mockGetBanks(f: Future[BanksResponse]) =
    (mockService
      .getBanks(_: JourneyId)(_: ExecutionContext, _: HeaderCarrier))
      .expects(*, *, *)
      .returning(f)
}
