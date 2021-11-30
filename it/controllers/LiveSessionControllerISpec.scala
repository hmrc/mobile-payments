package controllers

import openbanking.cor.model.response.CreateSessionDataResponse
import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.OpenBankingStub._
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import utils.BaseISpec

class LiveSessionControllerISpec extends BaseISpec with MobilePaymentsTestData {

  "POST /sessions" should {
    "return 200 when payload is valid" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSession(response = createSessionDataResponseJson)

      val request: WSRequest = wsUrl(
        s"/sessions?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.obj("amount" -> 1200, "saUtr" -> "CS700100A")))
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[CreateSessionDataResponse]
      parsedResponse.sessionDataId.value shouldBe "51cc67d6-21da-11ec-9621-0242ac130002"
    }

    "return 500 when request from session is malformed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSession(response = rawMalformedJson)

      val request: WSRequest = wsUrl(
        s"/sessions?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.obj("amount" -> 1200, "saUtr" -> "CS700100A")))
      response.status shouldBe 500
    }

    "return 401 when a 401 is returned from session" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSession(401)

      val request: WSRequest = wsUrl(
        s"/sessions?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.obj("amount" -> 1200, "saUtr" -> "CS700100A")))
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from session" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSession(404)

      val request: WSRequest = wsUrl(
        s"/sessions?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.obj("amount" -> 1200, "saUtr" -> "CS700100A")))
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/sessions?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.obj("amount" -> 1200, "saUtr" -> "CS700100A")))
      response.status shouldBe 401
    }

    "return 500 when unknown error is returned from session" in {
      grantAccess()
      stubForShutteringDisabled
      stubForCreateSession(500)

      val request: WSRequest = wsUrl(
        s"/sessions?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.obj("amount" -> 1200, "saUtr" -> "CS700100A")))
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/sessions?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, contentHeader)
      val response = await(request.post(Json.obj("amount" -> 1200, "saUtr" -> "CS700100A")))
      response.status shouldBe 521
    }
  }
}
