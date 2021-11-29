package controllers

import openbanking.cor.model.response.InitiatePaymentResponse
import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.OpenBankingStub._
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.response.PaymentStatusResponse
import utils.BaseISpec

class LivePaymentControllerISpec extends BaseISpec with MobilePaymentsTestData {

  private val paymentUrl: String = "https://some-bank.com?param=dosomething"

  "POST /payments" should {
    "return 200 with payment url" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(response = paymentInitiatedResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl.toString() shouldBe "https://some-bank.com?param=dosomething"
    }

    "return 500 when request from payment is malformed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(response = rawMalformedJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 500
    }

    "return 401 when a 401 is returned from payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(404)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 401
    }

    "return 500 when unknown error is returned from payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 521
    }
  }

  "PUT /payments" should {
    "return 200 with the same payment url when URL not consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(false).toString())
      stubForInitiatePayment(response = paymentInitiatedResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl.toString() shouldBe paymentUrl
    }

    "return 200 with the same payment url when URL consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment()
      stubForInitiatePayment(response = paymentInitiatedUpdateResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl.toString() shouldBe "https://some-updated-bank.com?param=dosomething"
    }

    "return 500 when request from payment is malformed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment()
      stubForInitiatePayment(response = rawMalformedJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 500
    }

    "return 401 when a 401 is returned from url consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 401
    }

    "return 404 when a 401 is returned from clear payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 404
    }

    "return 401 when a 401 is returned from initiate payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment()
      stubForInitiatePayment(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from url consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(404)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 404
    }

    "return 404 when a 404 is returned from clear payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment(404)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 404
    }

    "return 404 when a 404 is returned from initiate payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment()
      stubForInitiatePayment(404)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 401
    }

    "return 500 when unknown error 500 returned from url consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 500
    }

    "return 404 when unknown error 500 returned from clear payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 404
    }

    "return 500 when unknown error 500 returned from initiate payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(response = Json.toJson(true).toString())
      stubForClearPayment()
      stubForInitiatePayment(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 521
    }
  }

  "GET /payments" should {
    "GET /payments" should {
      "return 200 with status" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(response = paymentStatusResponseJson)

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
        stubForGetPaymentStatus(response = rawMalformedJson)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 500
      }

      "return 401 when a 401 is returned from open-banking" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(401)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader)
        val response = await(request.get())
        response.status shouldBe 401
      }

      "return 404 when a 404 is returned from open-banking" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(404)

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
        stubForGetPaymentStatus(500)

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
