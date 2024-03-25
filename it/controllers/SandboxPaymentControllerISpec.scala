package controllers

import openbanking.cor.model.response.InitiatePaymentResponse
import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.response.{LatestPaymentsResponse, PayByCardResponse, PaymentStatusResponse, UrlConsumedResponse}
import utils.BaseISpec

class SandboxPaymentControllerISpec extends BaseISpec with MobilePaymentsTestData {

  private val sessionDataId: String = "51cc67d6-21da-11ec-9621-0242ac130002"

  "POST /payments" should {
    "return 200 when payload valid and sandbox header present it" in {
      stubForShutteringDisabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl
        .toString() shouldBe "https://qa.tax.service.gov.uk/mobile-payments-frontend/sandbox/result/open-banking"
    }

    "return 406 when request authorisation fails it" in {
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 406
    }

    "return 521 when service is shuttered" in {
      stubForShutteringEnabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 521
    }
  }

  "PUT /payments" should {
    "return 200 when payload valid and sandbox header present it" in {
      stubForShutteringDisabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl
        .toString() shouldBe "https://qa.tax.service.gov.uk/mobile-payments-frontend/sandbox/result/open-banking"
    }

    "return 406 when request authorisation fails it" in {
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, contentHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 406
    }

    "return 521 when service is shuttered" in {
      stubForShutteringEnabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 521
    }
  }

  "GET /payments" should {
    "return 200 when sandbox header present it" in {
      stubForShutteringDisabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[PaymentStatusResponse]
      parsedResponse.status shouldBe "Completed"
    }

    "return 406 when request authorisation fails it" in {
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader)
      val response = await(request.get())
      response.status shouldBe 406
    }

    "return 521 when service is shuttered" in {
      stubForShutteringEnabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 521
    }
  }

  "GET /payments/:sessionDataId/url-consumed" should {
    "return 200 when sandbox header present it" in {
      stubForShutteringDisabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[UrlConsumedResponse]
      parsedResponse.consumed shouldBe true
    }

    "return 406 when request authorisation fails it" in {
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader)
      val response = await(request.get())
      response.status shouldBe 406
    }

    "return 521 when service is shuttered" in {
      stubForShutteringEnabled
      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 521
    }
  }

  "GET /payments/pay-by-card" should {
    "return 200 and the payByCardUrl when sandbox header present" in {
      stubForShutteringDisabled

      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader)
      val response = await(request.post(Json.obj()))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[PayByCardResponse]
      parsedResponse.payByCardUrl shouldBe "/"
    }

    "return 406 when request authorisation fails it" in {
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader)
      val response = await(request.post(Json.obj()))
      response.status shouldBe 406
    }

  }

  "GET /payments/latest-payments" should {
    "return 200 and latest payment list when sandbox header present" in {
      stubForShutteringDisabled

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader, acceptJsonHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[LatestPaymentsResponse]
      parsedResponse.payments.size shouldBe 2
    }

    "return 406 when request authorisation fails it" in {
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments/latest-payments?journeyId=$journeyId"
      ).addHttpHeaders(sandboxHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 406
    }

  }
}
