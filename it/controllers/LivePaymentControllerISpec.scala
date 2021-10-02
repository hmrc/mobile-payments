package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.OpenBankingStub._
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.request.CreatePaymentRequest
import uk.gov.hmrc.mobilepayments.domain.dto.response.{BanksResponse, InitiatePaymentResponse}
import utils.BaseISpec

class LivePaymentControllerISpec extends BaseISpec with MobilePaymentsTestData {

  "POST /payments" should {
    "return 200 with payment url" in {
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
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl shouldBe "https://some-bank.com?param=dosomething"
    }

    "return 500 when response from create session is malformed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSession(rawMalformedJson)

      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 500
    }

    "return 401 when a 401 is returned from create session" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSessionFailure(401)

      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from create session" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSessionFailure()

      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 401
    }

    "return 500 when unknown error is returned from create session" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSessionFailure(500)

      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse(createPaymentRequestJson)))
      response.status shouldBe 521
    }
  }
}
