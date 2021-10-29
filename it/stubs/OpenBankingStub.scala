package stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.auto._
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

object OpenBankingStub {
  val journeyId:     JourneyId = "27085215-69a4-4027-8f72-b04b10ec16b0"
  val sessionDataId: String    = "51cc67d6-21da-11ec-9621-0242ac130002"

  def stubForGetBanks(response: String): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/open-banking/banks?journeyId=$journeyId"
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
          s"/open-banking/banks?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
      )
    )

  def stubForCreateSession(response: String): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(200)
          .withBody(response)
      )
    )

  def stubForCreateSessionFailure(statusCode: Int = 404): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
      )
    )

  def stubForSelectBank(): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/select-bank?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(200)
      )
    )

  def stubForSelectBankFailure(statusCode: Int = 404): StubMapping =
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

  def stubForInitiatePayment(response: String): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/initiate-payment?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(200)
          .withBody(response)
      )
    )

  def stubForInitiatePaymentFailure(statusCode: Int = 404): StubMapping =
    stubFor(
      post(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/initiate-payment?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
      )
    )

  def stubForGetPaymentStatus(response: String): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/payment-status?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(200)
          .withBody(response)
      )
    )

  def stubForGetPaymentStatusFailure(statusCode: Int = 404): StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/open-banking/session/$sessionDataId/payment-status?journeyId=$journeyId"
        )
      ).willReturn(
        aResponse()
          .withStatus(statusCode)
      )
    )
}
