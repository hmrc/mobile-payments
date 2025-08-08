/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.parcels

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

/** Parcel service unique charge reference for a payment transaction. Known to the user as Parcels Report No
  */
final case class ParcelsChargeReference(ref: String) {
  require(ref.length > 0, "Charge Ref must not be an empty string")
  require(ref.length < 15, "Charge Ref must be less than or equal to 14 characters")
}

object ParcelsChargeReference {
  implicit val format: Format[ParcelsChargeReference] = implicitly[Format[String]].inmap(ParcelsChargeReference(_), _.ref)
}
