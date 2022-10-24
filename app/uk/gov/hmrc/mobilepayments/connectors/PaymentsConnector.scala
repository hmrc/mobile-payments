/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.mobilepayments.connectors

import play.api.Logger
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, NotFoundException}
import uk.gov.hmrc.mobilepayments.domain.PaymentRecordListFromApi
import uk.gov.hmrc.mobilepayments.domain.dto.request.PayApiPayByCardRequest
import uk.gov.hmrc.mobilepayments.domain.dto.response.PayApiPayByCardResponse
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class PaymentsConnector @Inject() (
  http:                                   HttpClient,
  @Named("payments") serviceUrl:          String,
  @Named("payByCardReturnUrl") returnUrl: String,
  @Named("payByCardBackUrl") backUrl:     String
)(implicit ex:                            ExecutionContext) {

  val logger = Logger(this.getClass)

  def getSelfAssessmentPayments(
    utr:                    String,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[Either[String, Option[PaymentRecordListFromApi]]] =
    http.GET[HttpResponse](
      url = s"$serviceUrl/pay-api/v2/payment/search/$utr?taxType=selfAssessment&journeyId=${journeyId.value}"
    ) map { response =>
      response.status match {
        case OK =>
          Try(response.json.as[PaymentRecordListFromApi]) match {
            case Success(data) => Right(Some(data))
            case Failure(_)    => Left("unable to parse data from payment api")
          }
        case NOT_FOUND   => Right(None)
        case BAD_REQUEST => Left("invalid request sent")
        case _           => Left("couldn't handle response from payment api")
      }
    } recover {
      case _: NotFoundException => Right(None)
      case e: Exception => {
        logger.warn(s"Call to pay-api failed: $e")
        Left("exception thrown from payment api")
      }
    }

  def getPayByCardUrl(
    amount:                 Long,
    saUtr:                  SaUtr,
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier
  ): Future[PayApiPayByCardResponse] =
    http.POST[PayApiPayByCardRequest, PayApiPayByCardResponse](
      url = s"$serviceUrl/pay-api/app/sa/journey/start?journeyId=${journeyId.value}",
      PayApiPayByCardRequest(saUtr.utr, amount, returnUrl, backUrl)
    )

}
