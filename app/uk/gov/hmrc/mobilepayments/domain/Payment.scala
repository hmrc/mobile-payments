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

package uk.gov.hmrc.mobilepayments.domain

import play.api.libs.json.{Json, OFormat, Reads}
import uk.gov.hmrc.mobilepayments.domain.dto.response.TaxType
import uk.gov.hmrc.mobilepayments.domain.types.JourneyId
import uk.gov.hmrc.mobilepayments.models.payapi.*

import java.time.LocalDateTime

case class Payment(id: JourneyId,
                   reference: Reference,
                   transactionReference: TransactionReference,
                   amountInPence: Long,
                   status: PaymentStatus,
                   createdOn: LocalDateTime,
                   taxType: TaxType
                  )

object Payment {

  implicit val format: OFormat[Payment] = Json.format[Payment]

}

case class PaymentRecordListFromApi(payments: List[Payment])

object PaymentRecordListFromApi {
  implicit val reader: Reads[PaymentRecordListFromApi] = Json.reads[PaymentRecordListFromApi]
}
