package stubs

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.auto.*
import uk.gov.hmrc.mobilepayments.domain.types.JourneyId

object PayApiStub {
  val journeyId: String = "27085215-69a4-4027-8f72-b04b10ec16b0"
  val utr: String = "1122334455"

  def stubForGetPayments(
    statusCode: Int = 200,
    response: String = "{}",
    taxType: String = "selfAssessment",
    reference: String = utr
  ): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/pay-api/v2/payment/search/$reference?taxType=$taxType&journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )

  def stubForPayByCard(
    statusCode: Int = 200,
    response: String = "{}"
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

  def stubForPayByCardSimpleAssessment(
    statusCode: Int = 200,
    response: String = "{}"
  ): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/pay-api/app/simple-assessment/journey/start?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )
}
