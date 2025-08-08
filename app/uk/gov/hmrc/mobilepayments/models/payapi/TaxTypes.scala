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
      case "stamp-duty"      => JsSuccess(TaxTypes.stampDuty)
      case "corporation-tax" => JsSuccess(TaxTypes.corporationTax)
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

  /** Vat
    */
  case object vat extends TaxType

  /** Employers' Pay as you earn (Epaye)
    */
  case object epaye extends TaxType

  /** Taxes, penalties and enquiry settlements
    */
  case object epayeTpes extends TaxType

  /** Class 2 National Insurance
    */
  case object class2NationalInsurance extends TaxType

  /** Payments which don't have tax type. Amls for example.
    */
  case object other extends TaxType

  /** P800 tax type.
    */
  case object p800 extends TaxType

  /** P302 tax type.
    */
  case object p302 extends TaxType

  /** Stamp Duty Land Tax
    */
  case object stampDuty extends TaxType

  /** Merchandise in Baggage
    */
  case object mib extends TaxType

  /** Insurance Premium Tax
    */
  case object insurancePremium extends TaxType

  /** Customs Declaration Service
    */
  case object cds extends TaxType

  /** Trusts
    */
  case object trust extends TaxType

  /** Passengers
    */
  case object pngr extends TaxType

  /** Corporation Tax
    */
  case object corporationTax extends TaxType

  /** Class 3 national insurance
    */
  case object class3NationalInsurance extends TaxType

  /** Bio fuels
    */
  case object bioFuelsAndRoadGas extends TaxType

  /** Air passenger duty
    */
  case object airPassengerDuty extends TaxType

  /** Parcels
    */
  // TODO see if there is any validation required on Parcel references
  case object parcels extends TaxType

  /** Beer duty
    */
  case object beerDuty extends TaxType

  /** Landfill tax
    */
  case object landfillTax extends TaxType

  /** Aggregates levy
    */
  case object aggregatesLevy extends TaxType

  /** Climate change levy
    */
  case object climateChangeLevy extends TaxType

  /** Capital gains tax
    */
  case object capitalGainsTax extends TaxType

  /** Economic crime levy
    */
  case object economicCrimeLevy extends TaxType

  /** Job retention scheme
    */
  case object jobRetentionScheme extends TaxType

  /** Imported Vehicles
    */
  case object importedVehicles extends TaxType

  /** Child benefit repayments
    */
  case object childBenefitRepayments extends TaxType

  /** NI to EU VAT One Stop Shop scheme
    */
  case object niEuVatOss extends TaxType

  /** EU to NI VAT One Stop Shop scheme
    */
  case object niEuVatIoss extends TaxType

  /** Plastic Packaging Tax
    */
  case object plasticPackagingTax extends TaxType

  /** InheritanceTax Tax
    */
  case object inheritanceTax extends TaxType

  /** Wine and Cider Tax
    */
  case object wineAndCiderTax extends TaxType

  /** Spirit Drinks
    */
  case object spiritDrinks extends TaxType

  /** Annual Tax on Enveloped Dwellings
    */
  case object ated extends TaxType

  /** Alcohol Duty
    */
  case object alcoholDuty extends TaxType

  /** Vat Consumer to consumer */
  case object vatConsumerToConsumer extends TaxType

  /** Pillar2 */
  case object pillar2 extends TaxType

  override def values: immutable.IndexedSeq[TaxType] = findValues
}
