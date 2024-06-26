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

import payapi.corcommon.model.Origin
import play.api.libs.json.{Format, Json}

import java.time.{LocalDate, LocalDateTime}

final case class SessionDataResponse(
  sessionDataId: String,
  amountInPence: BigDecimal,
  bankId:        Option[String],
  state:         String,
  createdOn:     LocalDateTime,
  paymentDate:   Option[LocalDate],
  reference:     String,
  email:         Option[String],
  emailSent:     Option[Boolean],
  origin:        Origin,
  maybeFutureDate: Option[LocalDate])

object SessionDataResponse {
  implicit val format: Format[SessionDataResponse] = Json.format[SessionDataResponse]
}
