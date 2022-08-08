package stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.auto._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

object PayApiStub {
  val journeyId: JourneyId = "27085215-69a4-4027-8f72-b04b10ec16b0"
  val utr:       String    = "1122334455"

  def stubForGetPayments(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/pay-api/payment/search/$utr?taxType=selfAssessment&journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )

  def stubForPayByCard(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/pay-api/app/sa/journey/start?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )
}
