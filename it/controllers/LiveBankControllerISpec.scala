package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.OpenBankingStub._
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.domain.BanksResponse
import utils.BaseISpec

class LiveBankControllerISpec extends BaseISpec with MobilePaymentsTestData {

  "GET /banks" should {
    "return 200 with bank data" in {
      grantAccess()
      stubForGetBanks(rawBanksJson)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[BanksResponse]
      parsedResponse.data.size shouldBe 2
    }

    "return 500 when response from open-banking is malformed" in {
      grantAccess()
      stubForGetBanks(rawBanksMalformedJson)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 500
    }

    "return 401 when a 401 is returned from open-banking" in {
      grantAccess()
      stubForGetBanksFailure(401)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 401
    }

    "return 404 when a 404 is returned from open-banking" in {
      grantAccess()
      stubForGetBanksFailure()

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 404
    }

    "return 406 when auth fails" in {
      authorisationRejected()

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 401
    }

    "return 500 when unknown error is returned from open-banking" in {
      grantAccess()
      stubForGetBanksFailure(500)

      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader)
      val response = await(request.get())
      response.status shouldBe 500
    }
  }
}
