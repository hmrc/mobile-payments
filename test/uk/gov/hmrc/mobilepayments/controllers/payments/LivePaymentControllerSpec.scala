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

package uk.gov.hmrc.mobilepayments.controllers.payments

import openbanking.cor.model.response.InitiatePaymentResponse
import org.scalamock.handlers.CallHandler
import payapi.corcommon.model.Origin
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse, Upstream5xxResponse}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.request.PayByCardRequestGeneric
import uk.gov.hmrc.mobilepayments.domain.dto.request.TaxTypeEnum
import uk.gov.hmrc.mobilepayments.domain.dto.response.{LatestPaymentsResponse, PayByCardResponse, PaymentStatusResponse, SessionDataResponse, UrlConsumedResponse}
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, ShutteringMock}
import uk.gov.hmrc.mobilepayments.services.{AuditService, OpenBankingService, PaymentsService, ShutteringService}
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success

import scala.concurrent.{ExecutionContext, Future}

class LivePaymentControllerSpec
    extends BaseSpec
    with AuthorisationStub
    with MobilePaymentsTestData
    with ShutteringMock {

  private val mockOpenBankingService: OpenBankingService = mock[OpenBankingService]
  private val sessionDataId:          String             = "51cc67d6-21da-11ec-9621-0242ac130002"
  private val utr:                    String             = "12212321"
  private val nino:                   String             = "CS700100A"

  implicit val mockShutteringService: ShutteringService = mock[ShutteringService]
  implicit val mockAuthConnector:     AuthConnector     = mock[AuthConnector]
  implicit val mockAuditService:      AuditService      = mock[AuditService]
  implicit val mockPaymentsService:   PaymentsService   = mock[PaymentsService]

  private val sut = new LivePaymentController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    Helpers.stubControllerComponents(),
    mockOpenBankingService,
    mockShutteringService,
    mockAuditService,
    mockPaymentsService
  )

  "when create payment invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockUpdatePayment(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("PUT", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader, contentHeader)

      val result = sut.updatePayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  "when get payment status invoked and service returns success then" should {
    "return 200 and trigger sending of email if status Verified or Complete" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockGetPaymentStatus(Future successful PaymentStatusResponse("Verified"))
      mockGetSession(Future successful sessionDataResponse)
      mockSendEmail()

      val request = FakeRequest("GET", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[PaymentStatusResponse]
      response.status shouldEqual "Verified"
    }

    "return 200 and do not trigger sending of email if status is not Verified or Complete" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockGetPaymentStatus(Future successful PaymentStatusResponse("Authorised"))
      mockGetSession(Future successful sessionDataResponse)

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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
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
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockGetUrlConsumed(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed")
        .withHeaders(acceptJsonHeader)

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  "when get latest payments invoked and service returns success then" should {
    "return 200 and latest payments" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockGetLatestPayments(Future successful Right(Some(latestPaymentsResponse)))

      val request = FakeRequest("GET", s"/payments/latest-payments/$utr")
        .withHeaders(acceptJsonHeader)

      val result = sut.latestPaymentsLegacy(utr, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[LatestPaymentsResponse]
      response.payments.size               shouldBe 2
      response.payments.head.amountInPence shouldBe 11100
      response.payments.head.date.toString shouldBe "2022-05-07"
    }
  }

  "when get latest payments invoked and service returns None" should {
    "return 404 Not Found" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockGetLatestPayments(Future successful Right(None))

      val request = FakeRequest("GET", s"/payments/latest-payments/$utr")
        .withHeaders(acceptJsonHeader)

      val result = sut.latestPaymentsLegacy(utr, journeyId)(request)
      status(result) shouldBe 404
    }
  }

  "when get latest payments invoked and service returns Error String" should {
    "return 500 Internal Server Error" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockGetLatestPayments(Future successful Left("Unknown response"))

      val request = FakeRequest("GET", s"/payments/latest-payments/$utr")
        .withHeaders(acceptJsonHeader)

      val result = sut.latestPaymentsLegacy(utr, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  "when get latest payments invoked and valid utr for authorised user found but for a different utr" should {
    "return 403" in {
      stubAuthorisationGrantAccess(authorisedResponse)

      val request = FakeRequest("GET", "/payments/latest-payments/123123123")
        .withHeaders(acceptJsonHeader)

      val result = sut.latestPaymentsLegacy("123123123", journeyId)(request)
      status(result) shouldBe 403

    }
  }

  "when get latest payments invoked and no UTR is found on account" should {
    "return 401" in {
      stubAuthorisationGrantAccess(authorisedNoEnrolmentsResponse)

      val request = FakeRequest("GET", s"/payments/latest-payments/$utr")
        .withHeaders(acceptJsonHeader)

      val result = sut.latestPaymentsLegacy(utr, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get pay by card url invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockPayByCardUrl(Future successful payByCardResponse)

      val request = FakeRequest("POST", s"/payments/pay-by-card/$utr")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234))

      val result = sut.getPayByCardURL(utr, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[PayByCardResponse]
      response.payByCardUrl shouldBe "/pay/choose-a-way-to-pay?traceId=12345678"
    }
  }

  "when get pay by card url invoked with malformed json then" should {
    "return 400" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()

      val request = FakeRequest("POST", s"/payments/pay-by-card/$utr")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("bad-key" -> 1234))

      val result = sut.getPayByCardURL(utr, journeyId)(request)
      status(result) shouldBe 400
    }
  }

  "when get pay by card url invoked and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockPayByCardUrl(Future failed Upstream4xxResponse("Error", 401, 401))

      val request = FakeRequest("POST", s"/payments/pay-by-card/$utr")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234))

      val result = sut.getPayByCardURL(utr, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get pay by card url invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("POST", s"/payments/pay-by-card/$utr")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234))

      val result = sut.getPayByCardURL(utr, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get pay by card url invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      mockPayByCardUrl(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("POST", s"/payments/pay-by-card/$utr")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234))

      val result = sut.getPayByCardURL(utr, journeyId)(request)
      status(result) shouldBe 500
    }
  }

  "when get pay by card url invoked and valid utr for authorised user found but for a different utr" should {
    "return 403" in {
      stubAuthorisationGrantAccess(authorisedResponse)

      val request = FakeRequest("POST", "/payments/pay-by-card/123123123")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234))

      val result = sut.getPayByCardURL("123123123", journeyId)(request)
      status(result) shouldBe 403

    }
  }

  "when get pay by card url invoked and no UTR is found on account" should {
    "return 401" in {
      stubAuthorisationGrantAccess(authorisedNoEnrolmentsResponse)

      val request = FakeRequest("POST", s"/payments/pay-by-card/$utr")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234))

      val result = sut.getPayByCardURL(utr, journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get pay by card url generic invoked with self assessment and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      stubGetNinoFromAuth(Some(nino))
      mockPayByCardUrlGeneric(Future successful payByCardResponse)

      val request = FakeRequest("POST", s"/payments/pay-by-card")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234, "taxType" -> "appSelfAssessment", "reference" -> utr))

      val result = sut.getPayByCardURLGeneric(journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[PayByCardResponse]
      response.payByCardUrl shouldBe "/pay/choose-a-way-to-pay?traceId=12345678"
    }
  }

  "when get pay by card url generic invoked with simple assessment and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      stubGetNinoFromAuth(Some(nino))
      mockPayByCardUrlGeneric(Future successful payByCardResponse)

      val request = FakeRequest("POST", s"/payments/pay-by-card")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(
          Json.obj("amountInPence" -> 1234, "taxType" -> "appSimpleAssessment", "taxYear" -> 2023, "reference" -> utr)
        )

      val result = sut.getPayByCardURLGeneric(journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[PayByCardResponse]
      response.payByCardUrl shouldBe "/pay/choose-a-way-to-pay?traceId=12345678"
    }
  }

  "when get pay by card url generic invoked with malformed json then" should {
    "return 400" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()

      val request = FakeRequest("POST", s"/payments/pay-by-card")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("bad-key" -> 1234))

      val result = sut.getPayByCardURLGeneric(journeyId)(request)
      status(result) shouldBe 400
    }
  }

  "when get pay by card url generic invoked and service returns 401 then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      stubGetNinoFromAuth(Some(nino))
      mockPayByCardUrlGeneric(Future failed Upstream4xxResponse("Error", 401, 401))

      val request = FakeRequest("POST", s"/payments/pay-by-card")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234, "taxType" -> "appSelfAssessment", "reference" -> utr))

      val result = sut.getPayByCardURLGeneric(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get pay by card url generic invoked and no nino is found then" should {
    "return 401" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      stubGetNinoFromAuth(None)
      mockPayByCardUrlGeneric(Future failed Upstream4xxResponse("Error", 401, 401))

      val request = FakeRequest("POST", s"/payments/pay-by-card")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234, "taxType" -> "appSelfAssessment", "reference" -> utr))

      val result = sut.getPayByCardURLGeneric(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get pay by card url generic invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("POST", s"/payments/pay-by-card")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234, "taxType" -> "appSelfAssessment", "reference" -> utr))

      val result = sut.getPayByCardURLGeneric(journeyId)(request)
      status(result) shouldBe 401
    }
  }

  "when get pay by card url generic invoked and service returns 5XX then" should {
    "return 500" in {
      stubAuthorisationGrantAccess(authorisedResponse)
      shutteringDisabled()
      stubGetNinoFromAuth(Some(nino))
      mockPayByCardUrlGeneric(Future failed Upstream5xxResponse("Error", 502, 502))

      val request = FakeRequest("POST", s"/payments/pay-by-card")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
        .withBody(Json.obj("amountInPence" -> 1234, "taxType" -> "appSelfAssessment", "reference" -> utr))

      val result = sut.getPayByCardURLGeneric(journeyId)(request)
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
      .sendPaymentEvent(_: Option[BigDecimal], _: Option[SaUtr], _: Option[String], _: String)(_: HeaderCarrier,
                                                                                               _: ExecutionContext))
      .expects(*, *, *, *, *, *)
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

  private def mockSendEmail() =
    (mockOpenBankingService
      .sendEmail(_: String, _: JourneyId, _: Origin)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *, *)
      .returning(Future successful Success)

  private def mockGetLatestPayments(future: Future[Either[String, Option[LatestPaymentsResponse]]]) =
    (mockPaymentsService
      .getLatestPayments(_: Option[String], _: Option[String], _: Option[TaxTypeEnum.Value], _: JourneyId)(
        _: ExecutionContext,
        _: HeaderCarrier
      ))
      .expects(*, *, *, *, *, *)
      .returning(future)

  private def mockPayByCardUrl(future: Future[PayByCardResponse]): Unit =
    (mockPaymentsService
      .getPayByCardUrl(_: String, _: Long, _: JourneyId)(_: ExecutionContext, _: HeaderCarrier))
      .expects(*, *, journeyId, *, *)
      .returning(future)

  private def mockPayByCardUrlGeneric(future: Future[PayByCardResponse]): Unit =
    (mockPaymentsService
      .getPayByCardUrlGeneric(_: PayByCardRequestGeneric, _: Option[String], _: JourneyId)(_: ExecutionContext,
                                                                                           _: HeaderCarrier))
      .expects(*, *, journeyId, *, *)
      .returning(future)

}
