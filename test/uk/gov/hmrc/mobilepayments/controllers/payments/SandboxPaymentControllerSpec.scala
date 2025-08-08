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

import org.scalamock.handlers.CallHandler
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.response.{LatestPaymentsResponse, PaymentStatusResponse, UrlConsumedResponse}
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, ShutteringMock}
import uk.gov.hmrc.mobilepayments.models.openBanking.response.InitiatePaymentResponse
import uk.gov.hmrc.mobilepayments.services.ShutteringService

import java.time.LocalDate
import scala.concurrent.Future

class SandboxPaymentControllerSpec extends BaseSpec with AuthorisationStub with MobilePaymentsTestData with ShutteringMock {

  private val sessionDataId: String = "51cc67d6-21da-11ec-9621-0242ac130002"
  private val utr: String = "11223344"

  implicit val mockShutteringService: ShutteringService = mock[ShutteringService]
  implicit val mockAuthConnector: AuthConnector = mock[AuthConnector]

  private val sut = new SandboxPaymentController(
    "https://qa.tax.service.gov.uk/mobile-payments-frontend/sandbox/result/open-banking",
    Helpers.stubControllerComponents(),
    mockShutteringService
  )

  "when create payment invoked and service returns success then" should {
    "return 200" in {
      shutteringDisabled()

      val request = FakeRequest("POST", s"/payments/$sessionDataId")
        .withHeaders(acceptJsonHeader)

      val result = sut.createPayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[InitiatePaymentResponse]
      response.paymentUrl
        .toString() shouldEqual "https://qa.tax.service.gov.uk/mobile-payments-frontend/sandbox/result/open-banking"
    }
  }

  "when create payment invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("POST", s"/payments/$sessionDataId")

      val result = sut.createPayment(sessionDataId, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  "when get payment status invoked and service returns success then" should {
    "return 200" in {
      shutteringDisabled()

      val request = FakeRequest("GET", s"/payments/$sessionDataId?journeyId=$journeyId")
        .withHeaders(acceptJsonHeader)

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[PaymentStatusResponse]
      response.status shouldEqual "Completed"
    }
  }

  "when get payment status invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("GET", s"/payments/$sessionDataId?journeyId=$journeyId")

      val result = sut.getPaymentStatus(sessionDataId, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  "when get url consumed invoked and service returns success then" should {
    "return 200" in {
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
    "return 406" in {

      val request = FakeRequest("GET", s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId")

      val result = sut.urlConsumed(sessionDataId, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  "when get latest payments invoked and service returns success then" should {
    "return 200 and payments" in {

      val request = FakeRequest("GET", s"/payments/latest-payments/$utr?journeyId=$journeyId")
        .withHeaders(acceptJsonHeader)

      val result = sut.latestPaymentsLegacy(utr, journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[LatestPaymentsResponse]
      response.payments.size               shouldBe 2
      response.payments.head.amountInPence shouldBe 12000
      response.payments.head.date.toString shouldBe LocalDate.now().minusDays(10).toString
    }
  }

  "when get latest payments invoked and auth fails then" should {
    "return 406" in {

      val request = FakeRequest("GET", s"/payments/latest-payments/$utr?journeyId=$journeyId")

      val result = sut.latestPaymentsLegacy(utr, journeyId)(request)
      status(result) shouldBe 406
    }
  }

  private def shutteringDisabled(): CallHandler[Future[Shuttering]] =
    mockShutteringResponse(Shuttering(shuttered = false))
}
