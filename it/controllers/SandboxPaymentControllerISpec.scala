package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub.{authorisationRejected, grantAccess}
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.response.PaymentSessionResponse
import utils.BaseISpec

class SandboxPaymentControllerISpec extends BaseISpec with MobilePaymentsTestData {

  val sandboxHeader = "X-MOBILE-USER-ID" -> "208606423740"

  "when payload valid and sandbox header present it" should {
    "return 200" in {
      grantAccess()
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, sandboxHeader)
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[PaymentSessionResponse]
      parsedResponse.paymentUrl    shouldBe "https://tax.service.gov.uk/mobile-payments/ob-payment-result"
      parsedResponse.sessionDataId shouldBe "51cc67d6-21da-11ec-9621-0242ac130002"
    }
  }

  "when an invalid createPaymentsRequest is made it" should {
    "return 400" in {
      grantAccess()
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, sandboxHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 400
    }
  }

  "when request authorisation fails it" should {
    "return 401" in {
      authorisationRejected()
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, sandboxHeader)
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 401
    }
  }

  "when service is shuttered it" should {
    "return 521" in {
      grantAccess()
      stubForShutteringEnabled
      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, sandboxHeader)
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 521
    }
  }
}
