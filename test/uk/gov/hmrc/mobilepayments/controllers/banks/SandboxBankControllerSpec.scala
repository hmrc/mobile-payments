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

package uk.gov.hmrc.mobilepayments.controllers.banks

import org.scalamock.handlers.CallHandler
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.response.BanksResponse
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, ShutteringMock}
import uk.gov.hmrc.mobilepayments.services.ShutteringService

import scala.concurrent.Future

class SandboxBankControllerSpec
    extends BaseSpec
    with AuthorisationStub
    with MobilePaymentsTestData
    with ShutteringMock {

  implicit val mockShutteringService: ShutteringService = mock[ShutteringService]
  implicit val mockAuthConnector:     AuthConnector     = mock[AuthConnector]

  private val sessionDataId: String = "51cc67d6-21da-11ec-9621-0242ac130002"

  private val sut = new SandboxBankController(
    Helpers.stubControllerComponents(),
    mockShutteringService
  )

  "when get banks invoked and service returns success then" should {
    "return 200" in {
      shutteringDisabled()

      val request = FakeRequest("GET", "/banks")
        .withHeaders(acceptJsonHeader)

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[BanksResponse]
      response.data.size shouldBe 9
    }
  }

  "when get banks invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("GET", "/banks")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 406
    }
  }

  "when select bank invoked then" should {
    "return 201" in {
      shutteringDisabled()

      val request = FakeRequest("POST", s"/banks/$sessionDataId")
        .withHeaders(acceptJsonHeader, contentHeader, sandboxHeader)
        .withBody(Json.obj("bankId" -> "12345"))

      val result = sut.selectBank(sessionDataId, journeyId)(request)
      status(result) shouldBe 201
    }
  }

  "when select bank invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("POST", s"/banks/$sessionDataId")
        .withHeaders(contentHeader, sandboxHeader)
        .withBody(Json.obj("bankId" -> "12345"))

      val result = sut.selectBank(sessionDataId, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  private def shutteringDisabled(): CallHandler[Future[Shuttering]] =
    mockShutteringResponse(Shuttering(shuttered = false))
}
