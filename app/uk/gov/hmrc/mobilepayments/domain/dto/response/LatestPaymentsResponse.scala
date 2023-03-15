/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.domain.dto.response

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.mobilepayments.domain.{AmountInPence, Payment}

import java.time.LocalDate

case class LatestPaymentsResponse(payments: List[LatestPayment])

object LatestPaymentsResponse {

  implicit val format: Format[LatestPaymentsResponse] = Json.format[LatestPaymentsResponse]

  def fromPayments(payments: List[Payment]): LatestPaymentsResponse =
    LatestPaymentsResponse(payments.map(p => LatestPayment.fromPayment(p)))

}

case class LatestPayment(
  amountInPence: Long,
  date:          LocalDate)

object LatestPayment {

  implicit val format: Format[LatestPayment] = Json.format[LatestPayment]

  def fromPayment(payment: Payment): LatestPayment = LatestPayment(payment.amountInPence, payment.createdOn.toLocalDate)

}
