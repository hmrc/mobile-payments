package stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.auto._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

object OpenBankingStub {
  val journeyId:     JourneyId = "27085215-69a4-4027-8f72-b04b10ec16b0"
  val sessionDataId: String    = "51cc67d6-21da-11ec-9621-0242ac130002"

  def stubForGetBanks(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/open-banking/banks?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )

  def stubForCreateSession(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )

  def stubForSelectBank(statusCode: Int = 200): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/select-bank?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
      )
    )

  def stubForInitiatePayment(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/initiate-payment?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )

  def stubForGetPaymentStatus(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/payment-status?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )

  def stubForUrlConsumed(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/url-consumed?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )

  def stubForClearPayment(statusCode: Int = 200): StubMapping =
    stubFor(
      delete(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/clear-payment?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
      )
    )

  def stubForGetSession(
    statusCode: Int    = 200,
    response:   String = "{}"
  ): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
          .withBody(response)
      )
    )
}
