package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WSRequest
import stubs.AuthStub._
import stubs.ShutteringStub._
import utils.BaseISpec
import uk.gov.hmrc.mobilepayments.domain.Bank

class SandboxBankControllerISpec extends BaseISpec{

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
      val parsedResponse = Json.parse(response.body).as[Bank]
      parsedResponse.id(0) shouldBe "obie-mettle-production"
      parsedResponse.name(1) shouldBe "Barclays Business Mobile"
      parsedResponse.logo(2) shouldBe "https://public.ecospend.com/images/banks/UlsterNI.svg"
      parsedResponse.icon(3) shouldBe "https://public.ecospend.com/images/banks/Barclays_icon.svg"
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
