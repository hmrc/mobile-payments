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
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse, Upstream5xxResponse}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.response.{PaymentStatusResponse, SessionDataResponse, UrlConsumedResponse}
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, ShutteringMock}
import uk.gov.hmrc.mobilepayments.services.{AuditService, OpenBankingService, ShutteringService}
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success

import scala.concurrent.{ExecutionContext, Future}

class LivePaymentControllerSpec
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

  private val sut = new LivePaymentController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    Helpers.stubControllerComponents(),
    mockOpenBankingService,
    mockShutteringService,
    mockAuditService
  )

  "when create payment invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockInitiatePayment(Future successful paymentSessionResponse)
      mockGetSession(Future successful sessionDataResponse)
      stubPaymentEvent()

      val request = FakeRequest("POST", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.createPayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[InitiatePaymentResponse]
      response.paymentUrl.toString() shouldEqual "https://some-bank.com?param=dosomething"
    }
  }

  "when create payment invoked and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockInitiatePayment(Future failed Upstream4xxResponse("Error", 401, 401))

      val request = FakeRequest("POST", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.createPayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
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

  "when create payment invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockInitiatePayment(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("POST", s"/payments/$sessionDataId")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.createPayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  "when update payment invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockUpdatePayment(Future successful paymentSessionResponse)
      //      stubPaymentEvent()

      val request = FakeRequest("PUT", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader, contentHeader)

      val result = sut.updatePayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[InitiatePaymentResponse]
      response.paymentUrl.toString() shouldEqual "https://some-bank.com?param=dosomething"
    }
  }

  "when update payment invoked and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockUpdatePayment(Future failed Upstream4xxResponse("Error", 401, 401))

      val request = FakeRequest("PUT", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader, contentHeader)

      val result = sut.updatePayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when update payment invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("PUT", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader, contentHeader)

      val result = sut.updatePayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when update payment invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockUpdatePayment(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("PUT", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader, contentHeader)

      val result = sut.updatePayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  "when get payment status invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetPaymentStatus(Future successful PaymentStatusResponse("Authorised"))

      val request = FakeRequest("GET", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[PaymentStatusResponse]
      response.status shouldEqual "Authorised"
    }
  }

  "when get payment status invoked and service returns NotFoundException then" should {
    "return 404" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetPaymentStatus(Future.failed(Upstream4xxResponse("Error", 404, 404)))

      val request = FakeRequest("GET", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 404
    }
  }

  "when get payment status and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetPaymentStatus(Future.failed(new Upstream4xxResponse("Error", 401, 401)))

      val request = FakeRequest("GET", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get payment status invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("GET", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get payment status invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetPaymentStatus(Future.failed(new Upstream5xxResponse("Error", 502, 502)))

      val request = FakeRequest("GET", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  "when get url consumed invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetUrlConsumed(Future successful UrlConsumedResponse(true))

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[UrlConsumedResponse]
      response.consumed shouldBe true
    }
  }

  "when get url consumed invoked and service returns NotFoundException then" should {
    "return 404" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetUrlConsumed(Future.failed(Upstream4xxResponse("Error", 404, 404)))

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 404
    }
  }

  "when get url consumed and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetUrlConsumed(Future.failed(new Upstream4xxResponse("Error", 401, 401)))

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get url consumed status invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get url consumed invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(confidenceLevel)
      shutteringDisabled()
      mockGetUrlConsumed(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  private def mockInitiatePayment(future: Future[InitiatePaymentResponse]) =
    (mockOpenBankingService
      .initiatePayment(_: String, _: JourneyId)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(future)

  private def mockUpdatePayment(future: Future[InitiatePaymentResponse]) =
    (mockOpenBankingService
      .updatePayment(_: String, _: JourneyId)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(future)

  private def shutteringDisabled(): CallHandler[Future[Shuttering]] =
    mockShutteringResponse(Shuttering(shuttered = false))

  private def mockGetPaymentStatus(f: Future[PaymentStatusResponse]) =
    (mockOpenBankingService
      .getPaymentStatus(_: String, _: JourneyId)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(f)

  private def stubPaymentEvent() =
    (mockAuditService
      .sendPaymentEvent(_: BigDecimal, _: SaUtr, _: String)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *, *)
      .returning(Future successful Success)

  private def mockGetUrlConsumed(f: Future[UrlConsumedResponse]) =
    (mockOpenBankingService
      .urlConsumed(_: String, _: JourneyId)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(f)

  private def mockGetSession(future: Future[SessionDataResponse]) =
    (mockOpenBankingService
      .getSession(_: String, _: JourneyId)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(future)
}
