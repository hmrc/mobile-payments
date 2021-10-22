package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import utils.BaseISpec
import stubs.AuthStub.grantAccess
import stubs.OpenBankingStub.{sessionDataId, stubForCreateSession, stubForInitiatePayment, stubForSelectBank}
import stubs.ShutteringStub.stubForShutteringDisabled
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.response.{PaymentSessionResponse, PaymentStatusResponse}

class SandboxPaymentControllerISpec extends BaseISpec with MobilePaymentsTestData {
  "when payload valid and sandbox header present it" should {
    "return 201" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSession(sessionDataResponseJson)
      stubForSelectBank()
      stubForInitiatePayment(paymentInitiatedResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[PaymentSessionResponse]
      parsedResponse.paymentUrl shouldBe "https://some-bank.com?param=dosomething"
      parsedResponse.sessionDataId shouldBe sessionDataId
    }
  }
}
