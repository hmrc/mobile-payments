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
import openbanking.cor.model.response.CreateSessionDataResponse
import org.scalamock.handlers.CallHandler
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.http.{Upstream4xxResponse, Upstream5xxResponse}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.response.SessionDataResponse
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, ShutteringMock}
import uk.gov.hmrc.mobilepayments.services.ShutteringService

import scala.concurrent.Future

class SandboxSessionControllerSpec
    extends BaseSpec
    with AuthorisationStub
    with MobilePaymentsTestData
    with ShutteringMock {

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200
  private val sessionDataId:   String          = "51cc67d6-21da-11ec-9621-0242ac130002"

  implicit val mockShutteringService: ShutteringService = mock[ShutteringService]
  implicit val mockAuthConnector:     AuthConnector     = mock[AuthConnector]

  private val sut = new SandboxSessionController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    Helpers.stubControllerComponents(),
    mockShutteringService
  )

  "when create session invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()

      val request = FakeRequest("POST", "/sessions")
        .withHeaders(acceptJsonHeader, contentHeader, sandboxHeader)
        .withBody(Json.obj("amount" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[CreateSessionDataResponse]
      response.sessionDataId.value shouldBe "51cc67d6-21da-11ec-9621-0242ac130002"
    }
  }

  "when create session invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("POST", "/sessions")
        .withHeaders(acceptJsonHeader, contentHeader, sandboxHeader)
        .withBody(Json.obj("amount" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get session invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()

      val request = FakeRequest("Get", s"/sessions/$sessionDataId")
        .withHeaders(acceptJsonHeader, sandboxHeader)

      val result = sut.getSession(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[SessionDataResponse]
      response.sessionDataId shouldEqual sessionDataId
      response.amount shouldEqual 125.64
      response.bankId shouldEqual Some("some-bank-id")
      response.saUtr.value shouldEqual "CS700100A"
    }
  }

  "when get session invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("Get", s"/sessions/$sessionDataId")
        .withHeaders(acceptJsonHeader, sandboxHeader)

      val result = sut.getSession(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  private def shutteringDisabled(): CallHandler[Future[Shuttering]] =
    mockShutteringResponse(Shuttering(shuttered = false))
}
