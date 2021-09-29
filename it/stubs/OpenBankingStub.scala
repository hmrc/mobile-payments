package stubs

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.auto._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

object OpenBankingStub {
  val journeyId: JourneyId = "27085215-69a4-4027-8f72-b04b10ec16b0"

  def stubForGetBanks(response: String): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/banks?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(200)
          .withBody(response)
      )
    )

  def stubForGetBanksFailure(statusCode: Int = 404): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/banks?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
      )
    )
}
