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

package uk.gov.hmrc.mobilepayments.controllers.session

import org.scalamock.handlers.CallHandler
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse, Upstream5xxResponse}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.response.CreateSessionDataResponse
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, ShutteringMock}
import uk.gov.hmrc.mobilepayments.services.{AuditService, OpenBankingService, ShutteringService}

import scala.concurrent.{ExecutionContext, Future}

class LiveSessionControllerSpec
    extends BaseSpec
    with AuthorisationStub
    with MobilePaymentsTestData
    with ShutteringMock {

  private val confidenceLevel:        ConfidenceLevel    = ConfidenceLevel.L200
  private val mockOpenBankingService: OpenBankingService = mock[OpenBankingService]
  private val sessionDataId:          String             = "51cc67d6-21da-11ec-9621-0242ac130002"

  implicit val mockShutteringService: ShutteringService = mock[ShutteringService]
  implicit val mockAuthConnector:     AuthConnector     = mock[AuthConnector]
  implicit val mockAuditService:      AuditService      = mock[AuditService]

  private val sut = new LiveSessionController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    Helpers.stubControllerComponents(),
    mockOpenBankingService,
    mockShutteringService
  )

  "when create session invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockCreateSession(Future successful sessionDataResponse)

      val request = FakeRequest("POST", "/sessions")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amount" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[CreateSessionDataResponse]
      response.sessionDataId shouldEqual sessionDataId
    }
  }

  "when create session invoked with malformed json then" should {
    "return 400" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()

      val request = FakeRequest("POST", "/sessions")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("bad-key" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 400
    }
  }

  "when create session invoked and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockCreateSession(Future failed Upstream4xxResponse("Error", 401, 401))

      val request = FakeRequest("POST", "/sessions")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amount" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when create session invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("POST", "/sessions")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amount" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when create session invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockCreateSession(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("POST", "/sessions")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amount" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 500
    }
  }

  private def mockCreateSession(future: Future[CreateSessionDataResponse]) =
    (mockOpenBankingService
      .createSession(_: BigDecimal, _: SaUtr, _: JourneyId)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *, *)
      .returning(future)

  private def shutteringDisabled(): CallHandler[Future[Shuttering]] =
    mockShutteringResponse(Shuttering(shuttered = false))
}
