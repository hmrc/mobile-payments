package controllers

import openbanking.cor.model.response.InitiatePaymentResponse
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.OpenBankingStub._
import stubs.PayApiStub._
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.response.{LatestPaymentsResponse, PayApiPayByCardResponse, PayByCardResponse, PaymentStatusResponse, UrlConsumedResponse}
import utils.BaseISpec

import java.time.LocalDate

class LivePaymentControllerISpec extends BaseISpec with MobilePaymentsTestData {

  private val paymentUrl: String = "https://some-bank.com?param=dosomething"

  "POST /payments" should {
    "return 200 with payment url" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(response = paymentInitiatedResponseJson)
      stubForGetSession(response      = sessionDataBankSelectedResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
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
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
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
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
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
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.post(Json.parse("{}")))
      response.status shouldBe 521
    }
  }

  "PUT /payments" should {
    "return 200 with the payment url" in {
      grantAccess()
      stubForShutteringDisabled
      stubForInitiatePayment(response = paymentInitiatedResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.obj("paymentUrl" -> paymentUrl)))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[InitiatePaymentResponse]
      parsedResponse.paymentUrl.toString() shouldBe paymentUrl
    }

    "return 500 when request from payment is malformed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForClearPayment()
      stubForInitiatePayment(response = rawMalformedJson)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 500
    }

    "return 404 when a 401 is returned from clear payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForClearPayment(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 404
    }

    "return 401 when a 401 is returned from initiate payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForClearPayment()
      stubForInitiatePayment(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from clear payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForClearPayment(404)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 404
    }

    "return 404 when a 404 is returned from initiate payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForClearPayment()
      stubForInitiatePayment(404)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 401
    }

    "return 404 when unknown error 500 returned from clear payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForClearPayment(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 404
    }

    "return 500 when unknown error 500 returned from initiate payment" in {
      grantAccess()
      stubForShutteringDisabled
      stubForClearPayment()
      stubForInitiatePayment(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader, authorisationJsonHeader)
      val response = await(request.put(Json.parse("{}")))
      response.status shouldBe 521
    }
  }

  "GET /payments/:sessionDataId/url-consumed" should {
    Seq(true, false).foreach { consumed =>
      s"return 200 with the consumed flag equal to $consumed" in {
        grantAccess()
        stubForShutteringDisabled
        stubForUrlConsumed(response = Json.toJson(consumed).toString())

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
        val response = await(request.get)
        response.status shouldBe 200
        val parsedResponse = Json.parse(response.body).as[UrlConsumedResponse]
        parsedResponse.consumed shouldBe consumed
      }
    }

    "return 401 when a 401 is returned from url consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(401)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get)
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from url consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(404)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get)
      response.status shouldBe 401
    }

    "return 500 when unknown error 500 returned from url consumed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForUrlConsumed(500)

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/$sessionDataId/url-consumed?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 521
    }
  }

  "GET /payments" should {
    "GET /payments" should {
      "return 200 with status and trigger sending of email if payment status Verified or Complete " in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(response = paymentStatusResponseJson)
        stubForGetSession(response       = sessionDataPaymentFinalisedResponseJson)
        stubForSendEmail()
        stubForSetEmailSentFlag()

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
        val response = await(request.get())
        response.status shouldBe 200
        val parsedResponse = Json.parse(response.body).as[PaymentStatusResponse]
        parsedResponse.status shouldEqual "Verified"
        verifyEmailSent(sessionDataId)
      }

      "return 200 with status and do not trigger sending of email if emailSent = true " in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(response = paymentStatusResponseJson)
        stubForGetSession(response       = sessionDataPaymentFinalisedEmailSentResponseJson)
        stubForSendEmail()
        stubForSetEmailSentFlag()

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
        val response = await(request.get())
        response.status shouldBe 200
        val parsedResponse = Json.parse(response.body).as[PaymentStatusResponse]
        parsedResponse.status shouldEqual "Verified"
        verifyEmailNotSend(sessionDataId)
      }

      "return 500 when response from status json is malformed" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(response = rawMalformedJson)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
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
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
        val response = await(request.get())
        response.status shouldBe 404
      }

      "return 401 when auth fails" in {
        authorisationRejected()

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
        val response = await(request.get())
        response.status shouldBe 401
      }

      "return 500 when unknown error is returned from open-banking" in {
        grantAccess()
        stubForShutteringDisabled
        stubForGetPaymentStatus(500)

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
        val response = await(request.get())
        response.status shouldBe 500
      }

      "return 521 when shuttered" in {
        grantAccess()
        stubForShutteringEnabled

        val request: WSRequest = wsUrl(
          s"/payments/$sessionDataId?journeyId=$journeyId"
        ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
        val response = await(request.get())
        response.status shouldBe 521
      }
    }
  }

  "GET /payments/latest-payments/:utr" should {
    "return 200 with the latest payments for the user" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetPayments(200, paymentsResponseString())

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[LatestPaymentsResponse]
      parsedResponse.payments.size               shouldBe 1
      parsedResponse.payments.head.date.toString shouldBe LocalDate.now().toString
      parsedResponse.payments.head.amountInPence shouldBe 11100

    }

    "return 404 when no valid payments returned" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetPayments(200, paymentsResponseString(LocalDate.now().minusDays(15).toString))

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 404

    }

    "return 404 when a 404 is returned from get payments" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetPayments(404)

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get)
      response.status shouldBe 401
    }

    "return 500 when a 401 is returned from get payments" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetPayments(401)

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 500
    }

    "return 500 when unknown error 500 returned from get payments" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetPayments(500)

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/latest-payments/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get)
      response.status shouldBe 521
    }
  }

  "GET /payments/pay-by-card/:utr" should {
    "return 200 with the pay by card url" in {
      grantAccess()
      stubForShutteringDisabled
      stubForPayByCard(200, payApiPayByCardResponseJson)

      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.post(Json.obj("amountInPence" -> 100000)))
      response.status shouldBe 200
      println("BODY = " + response.body)
      val parsedResponse = Json.parse(response.body).as[PayByCardResponse]
      parsedResponse.payByCardUrl shouldBe "http://localhost:9056/pay/choose-a-way-to-pay?traceId=12345678"

    }

    "return 404 when a 404 is returned from payments" in {
      grantAccess()
      stubForShutteringDisabled
      stubForPayByCard(404)

      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.post(Json.obj("amountInPence" -> 100000)))
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.post(Json.obj("amountInPence" -> 100000)))
      response.status shouldBe 401
    }

    "return 500 when a 401 is returned from payments" in {
      grantAccess()
      stubForShutteringDisabled
      stubForPayByCard(401)

      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.post(Json.obj("amountInPence" -> 100000)))
      response.status shouldBe 500
    }

    "return 500 when unknown error 500 returned from payments" in {
      grantAccess()
      stubForShutteringDisabled
      stubForPayByCard(500)

      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.post(Json.obj("amountInPence" -> 100000)))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/payments/pay-by-card/$utr?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.post(Json.obj("amountInPence" -> 100000)))
      response.status shouldBe 521
    }
  }
}
