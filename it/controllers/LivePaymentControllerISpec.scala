package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.OpenBankingStub._
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.response.{InitiatePaymentResponse, PaymentStatusResponse}
import utils.BaseISpec

class LivePaymentControllerISpec extends BaseISpec with MobilePaymentsTestData {

  "POST /payments" should {
    "return 200 with payment url" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(paymentInitiatedResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl shouldBe "https://some-bank.com?param=dosomething"
    }

    "return 500 when request from payment is malformed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(rawMalformedJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 500
    }

    "return 401 when a 401 is returned from payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePaymentFailure(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePaymentFailure()

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 401
    }

    "return 500 when unknown error is returned from payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePaymentFailure(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 521
    }

    "GET /payments" should {
      "return 200 with status" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(paymentStatusResponseJson)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 200
        val parsedResponse = Json.parse(response.body).as[PaymentStatusResponse]
        parsedResponse.status shouldEqual "Authorised"
      }

      "return 500 when response from status json is malformed" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(rawMalformedJson)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 500
      }

      "return 401 when a 401 is returned from open-banking" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatusFailure(401)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 401
      }

      "return 404 when a 404 is returned from open-banking" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatusFailure()

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 404
      }

      "return 401 when auth fails" in {
        authorisationRejected()

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 401
      }

      "return 500 when unknown error is returned from open-banking" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatusFailure(500)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 500
      }

      "return 521 when shuttered" in {
        grantAccess()
        stubForShutteringEnabled

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 521
      }
    }
  }
}
