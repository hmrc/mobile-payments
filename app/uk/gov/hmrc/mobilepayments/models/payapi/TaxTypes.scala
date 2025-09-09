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

package uk.gov.hmrc.mobilepayments.domain.dto.response

import enumeratum.{EnumEntry, *}
import uk.gov.hmrc.mobilepayments.domain.dto.response.ValueClassBinder.bindableA
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.*
import play.api.libs.json.*
import play.api.mvc.QueryStringBindable

import scala.collection.immutable

sealed abstract class TaxType extends EnumEntry

object TaxType {
  // TODO: remove this code after 60 days once legacy journeys are removed from mongo - written 17/01/20
  // TODO: speak to BTA about making a change
  private val oldReads: Reads[TaxType] = Reads.StringReads
    .jsrFlatMap {
      case "self-assessment" => JsSuccess(TaxTypes.selfAssessment)
      case x                 => JsError(s"Unknown tax type: '$x'")
    }

  private val reads: Reads[TaxType] = EnumFormat(TaxTypes) orElse oldReads

  private val writes: Writes[TaxType] = EnumFormat(TaxTypes)

  implicit val format: Format[TaxType] = Format(reads, writes)

  implicit val pathBinder: QueryStringBindable[TaxType] = bindableA(_.toString)

}

object TaxTypes extends Enum[TaxType] {

  /** Self assessment
    */
  case object selfAssessment extends TaxType
  
  /** Payments which don't have tax type. Amls for example.
    */
  case object other extends TaxType

 

  override def values: immutable.IndexedSeq[TaxType] = findValues
}
