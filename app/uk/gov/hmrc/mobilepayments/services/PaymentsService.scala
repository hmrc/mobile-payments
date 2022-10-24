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

package uk.gov.hmrc.mobilepayments.services

import payapi.corcommon.model.PaymentStatuses.Successful
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilepayments.connectors.PaymentsConnector
import uk.gov.hmrc.mobilepayments.domain.{Payment, PaymentRecordListFromApi}
import uk.gov.hmrc.mobilepayments.domain.dto.response.{LatestPaymentsResponse, PayByCardResponse}
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PaymentsService @Inject() (connector: PaymentsConnector) {

  def getLatestPayments(
    utr:                       String,
    journeyId:                 JourneyId
  )(implicit executionContext: ExecutionContext,
    headerCarrier:             HeaderCarrier
  ): Future[Either[String, Option[LatestPaymentsResponse]]] =
    connector.getSelfAssessmentPayments(utr, journeyId) map {
      case Right(payments) => {
        val recentPayments: List[Payment] =
          payments.map(paymentsList => filterPaymentsOlderThan14DaysOrUnsuccessful(paymentsList)).getOrElse(List.empty)
        if (recentPayments.isEmpty) Right(None)
        else
          Right(
            Some(LatestPaymentsResponse.fromPayments(recentPayments))
          )
      }
      case Right(None) => Right(None)
      case Left(e)     => Left(e)
      case _           => Left("Error calling pay-api")
    }

  def getPayByCardUrl(
    utr:                       String,
    amountInPence:             Long,
    journeyId:                 JourneyId
  )(implicit executionContext: ExecutionContext,
    headerCarrier:             HeaderCarrier
  ): Future[PayByCardResponse] =
    connector
      .getPayByCardUrl(amountInPence, SaUtr(utr), journeyId)
      .map(response => PayByCardResponse(response.urlWithoutDomainPrefix))

  private def filterPaymentsOlderThan14DaysOrUnsuccessful(paymentsFromApi: PaymentRecordListFromApi) =
    paymentsFromApi.payments.filter(payment =>
      (payment.createdOn.isAfter(LocalDate.now().minusDays(14).atStartOfDay()) && payment.status == Successful)
    )

}
