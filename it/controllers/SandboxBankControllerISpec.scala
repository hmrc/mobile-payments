package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.ShutteringStub._
import uk.gov.hmrc.mobilepayments.domain.dto.response.BanksResponse
import utils.BaseISpec

class SandboxBankControllerISpec extends BaseISpec {

  val sandboxHeader = "X-MOBILE-USER-ID" -> "208606423740"

  "when payload valid and sandbox header present it" should {
    "return 201" in {
      grantAccess()
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, sandboxHeader)
      val response = await(request.get())
      response.status shouldBe 200
      val parsedResponse = Json.parse(response.body).as[BanksResponse]
      parsedResponse.data.head.bankId       shouldBe "obie-barclays-personal"
      parsedResponse.data.head.name         shouldBe "Barclays Personal"
      parsedResponse.data.head.friendlyName shouldBe "Barclays Personal"
      parsedResponse.data.head.logoUrl      shouldBe "https://logo.com"
      parsedResponse.data.head.group        shouldBe "Barclays"
    }
  }

  "when request authorisation fails it" should {
    "return 401" in {
      authorisationRejected()
      stubForShutteringDisabled
      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, sandboxHeader)
      val response = await(request.get())
      response.status shouldBe 401
    }
  }

  "when service is shuttered it" should {
    "return 521" in {
      grantAccess()
      stubForShutteringEnabled
      val request: WSRequest = wsUrl(
        s"/banks?journeyId=$journeyId"
      ).addHttpHeaders(acceptJsonHeader, sandboxHeader)
      val response = await(request.get())
      response.status shouldBe 521
    }
  }
}
