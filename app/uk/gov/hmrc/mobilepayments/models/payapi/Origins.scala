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

import enumeratum.{Enum, EnumEntry}
import play.api.libs.json.*
import play.api.mvc.{JavascriptLiteral, QueryStringBindable}
import uk.gov.hmrc.mobilepayments.domain.dto.response.ValueClassBinder.bindableA
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.*

import scala.collection.immutable

sealed abstract class Origin extends EnumEntry {
  def toTaxType: TaxType
  def canPayByDD: Boolean = false
}

object Origin {

  implicit val originJsBinder: JavascriptLiteral[Origin] = new JavascriptLiteral[Origin] {
    def to(value: Origin): String = value.toString
  }

  private val reads: Reads[Origin] = EnumFormat(Origins)

  private val writes: Writes[Origin] = EnumFormat(Origins)

  implicit val format: Format[Origin] = Format(reads, writes)

  implicit val pathBinder: QueryStringBindable[Origin] = bindableA(_.toString)
}

object Origins extends Enum[Origin] {

  /*Payments originating in Payments Frontend*/

  /** Mobile app Journey (AppSa) Self Assessment (Sa)
    */
  case object AppSa extends Origin {
    def toTaxType: TaxType = TaxTypes.selfAssessment
  }
  
  /*Payments originating in Mib*/

  /** Simple Assessment
    */
  case object AppSimpleAssessment extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  override def values: immutable.IndexedSeq[Origin] = findValues

  lazy val notUsedOrigins: Set[Origin] = Set()

  lazy val liveOrigins: Set[Origin] = values.toSet -- notUsedOrigins
}
