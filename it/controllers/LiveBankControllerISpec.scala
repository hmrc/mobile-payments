package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.OpenBankingStub._
import stubs.ShutteringStub.{stubForShutteringDisabled, stubForShutteringEnabled}
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.dto.response.BanksResponse
import utils.BaseISpec

class LiveBankControllerISpec extends BaseISpec with MobilePaymentsTestData {

  "GET /banks" should {
    "return 200 with bank data" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetBanks(response = banksResponseJson)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get())
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[BanksResponse]
      parsedResponse.data.size shouldBe 10
    }

    "return 500 when response from open-banking is malformed" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetBanks(response = rawMalformedJson)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get())
      response.status shouldBe 500
    }

    "return 401 when a 401 is returned from open-banking" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetBanks(401)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from open-banking" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetBanks(404)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get())
      response.status shouldBe 404
    }

    "return 401 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 401
    }

    "return 500 when unknown error is returned from open-banking" in {
      grantAccess()
      stubForShutteringDisabled
      stubForGetBanks(500)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get())
      response.status shouldBe 500
    }

    "return 521 when shuttered" in {
      grantAccess()
      stubForShutteringEnabled

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
      val response = await(request.get())
      response.status shouldBe 521
    }
  }
}
