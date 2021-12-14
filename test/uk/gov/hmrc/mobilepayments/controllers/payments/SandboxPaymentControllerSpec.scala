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

package uk.gov.hmrc.mobilepayments.controllers.payments

import openbanking.cor.model.response.InitiatePaymentResponse
import org.scalamock.handlers.CallHandler
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.response.{PaymentStatusResponse, UrlConsumedResponse}
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, ShutteringMock}
import uk.gov.hmrc.mobilepayments.services.ShutteringService

import scala.concurrent.Future

class SandboxPaymentControllerSpec
    extends BaseSpec
    with AuthorisationStub
    with MobilePaymentsTestData
    with ShutteringMock {

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200
  private val sessionDataId:   String          = "51cc67d6-21da-11ec-9621-0242ac130002"

  implicit val mockShutteringService: ShutteringService = mock[ShutteringService]
  implicit val mockAuthConnector:     AuthConnector     = mock[AuthConnector]

  private val sut = new SandboxPaymentController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    "https://qa.tax.service.gov.uk/mobile-payments-frontend/sandbox/result/open-banking",
    Helpers.stubControllerComponents(),
    mockShutteringService
  )

  "when create payment invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()

      val request = FakeRequest("POST", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.createPayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[InitiatePaymentResponse]
      response.paymentUrl.toString() shouldEqual "https://qa.tax.service.gov.uk/mobile-payments-frontend/sandbox/result/open-banking"
    }
  }

  "when create payment invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("POST", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.createPayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get payment status invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()

      val request = FakeRequest("GET", s"/payments/$sessionDataId?journeyId=$journeyId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[PaymentStatusResponse]
      response.status shouldEqual "Authorised"
    }
  }

  "when get payment status invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("GET", s"/payments/$sessionDataId?journeyId=$journeyId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get url consumed invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[UrlConsumedResponse]
      response.consumed shouldEqual true
    }
  }

  "when get url consumed invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  private def shutteringDisabled(): CallHandler[Future[Shuttering]] =
    mockShutteringResponse(Shuttering(shuttered = false))
}
