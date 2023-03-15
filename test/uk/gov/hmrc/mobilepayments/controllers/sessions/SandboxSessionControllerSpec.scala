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

package uk.gov.hmrc.mobilepayments.controllers.sessions
import openbanking.cor.model.response.CreateSessionDataResponse
import org.scalamock.handlers.CallHandler
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.response.SessionDataResponse
import uk.gov.hmrc.mobilepayments.mocks.ShutteringMock
import uk.gov.hmrc.mobilepayments.services.ShutteringService

import scala.concurrent.Future

class SandboxSessionControllerSpec extends BaseSpec with MobilePaymentsTestData with ShutteringMock {

  private val sessionDataId:   String          = "51cc67d6-21da-11ec-9621-0242ac130002"

  implicit val mockShutteringService: ShutteringService = mock[ShutteringService]
  implicit val mockAuthConnector:     AuthConnector     = mock[AuthConnector]

  private val sut = new SandboxSessionController(
    Helpers.stubControllerComponents(),
    mockShutteringService
  )

  "when create session invoked and service returns success then" should {
    "return 200" in {
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
    "return 406" in {

      val request = FakeRequest("POST", "/sessions")
        .withHeaders(contentHeader, sandboxHeader)
        .withBody(Json.obj("amount" -> 1234, "saUtr" -> "CS700100A"))

      val result = sut.createSession(journeyId)(request)
      status(result) shouldBe 406
    }
  }

  "when get session invoked and service returns success then" should {
    "return 200" in {
      shutteringDisabled()

      val request = FakeRequest("Get", s"/sessions/$sessionDataId")
        .withHeaders(acceptJsonHeader, sandboxHeader)

      val result = sut.getSession(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[SessionDataResponse]
      response.sessionDataId shouldEqual sessionDataId
      response.state shouldEqual "BankSelected"
      response.amount shouldEqual 125.64
      response.bankId shouldEqual Some("obie-barclays-personal")
      response.saUtr.value shouldEqual "1555369056"
    }
  }

  "when get session invoked with control header and service returns success then" should {
    "return 200" in {
      shutteringDisabled()
      val sandboxControlHeader: (String, String) = "SANDBOX-CONTROL" -> "SUCCESS-PAYMENT"

      val request = FakeRequest("Get", s"/sessions/$sessionDataId")
        .withHeaders(acceptJsonHeader, sandboxHeader, sandboxControlHeader)

      val result = sut.getSession(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[SessionDataResponse]
      response.sessionDataId shouldEqual sessionDataId
      response.amount shouldEqual 125.64
      response.bankId shouldEqual Some("obie-barclays-personal")
      response.saUtr.value shouldEqual "1555369056"
      response.email.get shouldEqual "test@test.com"
      response.state shouldEqual "PaymentFinished"
    }
  }

  "when get session invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("Get", s"/sessions/$sessionDataId")
        .withHeaders(sandboxHeader)

      val result = sut.getSession(sessionDataId, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  "when set email invoked then" should {
    "return 201" in {

      val request = FakeRequest("POST", s"/sessions/$sessionDataId/set-email")
        .withHeaders(acceptJsonHeader, sandboxHeader)
        .withBody(Json.obj("email" -> "test@test.com"))

      val result = sut.setEmail(sessionDataId, journeyId)(request)
      status(result) shouldBe 201
    }
  }

  "when set email invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("POST", s"/sessions/$sessionDataId/set-email")
        .withHeaders(sandboxHeader)
        .withBody(Json.obj("email" -> "test@test.com"))

      val result = sut.setEmail(sessionDataId, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  "when clear email invoked then" should {
    "return 204" in {

      val request = FakeRequest("DELETE", s"/sessions/$sessionDataId/clear-email")
        .withHeaders(acceptJsonHeader, sandboxHeader)

      val result = sut.clearEmail(sessionDataId, journeyId)(request)
      status(result) shouldBe 204
    }
  }

  "when clear email invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("DELETE", s"/sessions/$sessionDataId/clear-email")
        .withHeaders(sandboxHeader)

      val result = sut.clearEmail(sessionDataId, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  private def shutteringDisabled(): CallHandler[Future[Shuttering]] =
    mockShutteringResponse(Shuttering(shuttered = false))
}
