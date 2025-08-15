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

  /** Payments Frontend (Pf) Self Assesment (Sa)
    */
  case object PfSa extends Origin {
    def toTaxType: TaxType = TaxTypes.selfAssessment
  }

  /** Payments Frontend (Pf) Vat
    */
  case object PfVat extends Origin {
    def toTaxType: TaxType = TaxTypes.vat
  }

  /** Payments Frontend (Pf) Corporation Tax (Ct)
    */
  case object PfCt extends Origin {
    def toTaxType: TaxType = TaxTypes.corporationTax
  }

  /** Payments Frontend (Pf) Employers' Pay as you earn (Epaye) National Insurance (Ni)
    */
  case object PfEpayeNi extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Payments Frontend (Pf) Employers' Pay as you earn (Epaye) Late Payment Penalty (Lpp)
    */
  case object PfEpayeLpp extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Payments Frontend (Pf) Employers' Pay as you earn (Epaye) Settlement Agreement (Seta)
    */
  case object PfEpayeSeta extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Payments Frontend (Pf) Employers' Pay as you earn (Epaye) Construction Industry Scheme Late Filing Penalty (LateCis)
    */
  case object PfEpayeLateCis extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Payments Frontend (Pf) Employers' Class 1A (P11D) National Insurance
    */
  case object PfEpayeP11d extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Payments Frontend (Pf) Stamp Duty Land Tax (Sdlt)
    */
  case object PfSdlt extends Origin {
    def toTaxType: TaxType = TaxTypes.stampDuty
  }

  /** Payments Frontend (Pf) Customs Declaration Service (Cds)
    */
  case object PfCds extends Origin {
    def toTaxType: TaxType = TaxTypes.cds
  }

  /** Payments Frontend (Pf) Other tax types
    */
  case object PfOther extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Payments Frontend (Pf) P800
    */
  // Todo Remove when PtaP800 is fully installed
  case object PfP800 extends Origin {
    def toTaxType: TaxType = TaxTypes.p800
  }

  /** Payments Frontend (Pta) P800
    */
  case object PtaP800 extends Origin {
    def toTaxType: TaxType = TaxTypes.p800
  }

  /** Payments Frontend (Pf) Class 2 National Insurance
    */
  case object PfClass2Ni extends Origin {
    def toTaxType: TaxType = TaxTypes.class2NationalInsurance
  }

  /** Insurance Premium Tax
    */

  case object PfInsurancePremium extends Origin {
    def toTaxType: TaxType = TaxTypes.insurancePremium
  }

  case object PfPsAdmin extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /*Payments originating in BTA*/

  /** Business Tax Account (Bta) Self Assessment (Sa)
    */
  case object BtaSa extends Origin {
    def toTaxType: TaxType = TaxTypes.selfAssessment
  }

  /** Mobile app Journey (AppSa) Self Assessment (Sa)
    */
  case object AppSa extends Origin {
    def toTaxType: TaxType = TaxTypes.selfAssessment
  }

  /** Business Tax Account (Bta) Vat
    */
  case object BtaVat extends Origin {
    def toTaxType: TaxType = TaxTypes.vat
  }

  /** Business Tax Account (Bta) Employers' Pay as you earn (Epaye) for a specific Bill
    */
  case object BtaEpayeBill extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Business Tax Account (Bta) Employers' Pay as you earn (Epaye) for a specific Penalty charge
    */
  case object BtaEpayePenalty extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Business Tax Account (Bta) Employers' Pay as you earn (Epaye) for a specific Interest charge
    */
  case object BtaEpayeInterest extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Business Tax Account (Bta) Employers' Pay as you earn (Epaye) for a general payment
    */
  case object BtaEpayeGeneral extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Business Tax Account (Bta) employers’ Class 1A National Insurance
    */
  case object BtaClass1aNi extends Origin {
    def toTaxType: TaxType = TaxTypes.epaye
  }

  /** Business Tax Account (Bta) Corporation Tax (Ct)
    */
  case object BtaCt extends Origin {
    def toTaxType: TaxType = TaxTypes.corporationTax
  }

  /** Business Tax Account (Bta) Soft Drinks Industry Levy (Sdil)
    */
  case object BtaSdil extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /*Payments originating in Pngr*/

  /** Border Control (Bc) Passangers (Pngr)
    */
  case object BcPngr extends Origin {
    def toTaxType: TaxType = TaxTypes.pngr
  }

  case object Parcels extends Origin {
    def toTaxType: TaxType = TaxTypes.parcels
  }

  /*Payments originating in Direct Debit*/

  /** Direct Debit (Dd) Vat This identifies Vat payments originated in Direct Debit Frontend.
    */
  case object DdVat extends Origin {
    def toTaxType: TaxType = TaxTypes.vat
  }

  /** Direct Debit (Dd) Sdil This identifies Sdil payments originated in Direct Debit Frontend.
    */
  case object DdSdil extends Origin {
    override def toTaxType: TaxType = TaxTypes.other
  }

  /*Payments originating in View and Change*/

  /** View And Change (Vc) Vat This identifies Vat Return payments originated in View And Change microservice Currently the only charge you can pay by
    * DD
    */
  case object VcVatReturn extends Origin {
    def toTaxType: TaxType = TaxTypes.vat
    override val canPayByDD: Boolean = true
  }

  /** View And Change (Vc) Vat This identifies other Vat charge payments originated in View And Change microservice
    */
  case object VcVatOther extends Origin {
    def toTaxType: TaxType = TaxTypes.vat
  }

  /** Income Tax (It) Self Assesment (Sa) This identifies SA payments originated in the microservice owned by the Income Tax Self Assessment team, who
    * are the SA equivalent of V&C
    */
  case object ItSa extends Origin {
    def toTaxType: TaxType = TaxTypes.selfAssessment
  }

  /** Anti Money Laundering System (Amls) This identifies other payments originated in AMLS
    */
  case object Amls extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Plastic Packaging Tax
    */
  case object Ppt extends Origin {
    def toTaxType: TaxType = TaxTypes.plasticPackagingTax
  }

  /** CDS Cash account originated from pay frontend
    */
  case object PfCdsCash extends Origin {
    def toTaxType: TaxType = TaxTypes.cds
  }

  /** Plastic Packaging Tax originated from pay frontend
    */
  case object PfPpt extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Spirit Drinks originated from pay frontend
    */
  case object PfSpiritDrinks extends Origin {
    def toTaxType: TaxType = TaxTypes.spiritDrinks
  }

  /** Inheritance Tax originated from pay frontend
    */
  case object PfInheritanceTax extends Origin {
    def toTaxType: TaxType = TaxTypes.inheritanceTax
  }

  /*Payments originating in Mib*/

  case object Mib extends Origin {
    def toTaxType: TaxType = TaxTypes.mib
  }

  /** class3NationalInsurance
    */
  case object PfClass3Ni extends Origin {
    def toTaxType: TaxType = TaxTypes.class3NationalInsurance
  }

  /** Personal Tax Account (Pta) Self Assesment (Sa)
    */
  case object PtaSa extends Origin {
    def toTaxType: TaxType = TaxTypes.selfAssessment
  }

  /** Pay frontend Tax Wine and Cider tax
    */
  case object PfWineAndCider extends Origin {
    def toTaxType: TaxType = TaxTypes.wineAndCiderTax
  }

  /** Bio fuels and road gas duty
    */
  case object PfBioFuels extends Origin {
    def toTaxType: TaxType = TaxTypes.bioFuelsAndRoadGas
  }

  /** AirPassenger
    */
  case object PfAirPass extends Origin {
    def toTaxType: TaxType = TaxTypes.airPassengerDuty
  }

  /** Machine Game Duty
    */
  case object PfMgd extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Beer Duty
    */
  case object PfBeerDuty extends Origin {
    def toTaxType: TaxType = TaxTypes.beerDuty
  }

  /** Gaming or Bingo Duty
    */
  case object PfGamingOrBingoDuty extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** General Betting, Pool Betting or Remote Gaming Duty
    */
  case object PfGbPbRgDuty extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Landfill tax
    */
  case object PfLandfillTax extends Origin {
    def toTaxType: TaxType = TaxTypes.landfillTax
  }

  /** Soft Drinks Industry Levy (Sdil)
    */
  case object PfSdil extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Aggregates levy
    */
  case object PfAggregatesLevy extends Origin {
    def toTaxType: TaxType = TaxTypes.aggregatesLevy
  }

  /** Climate Change Levy
    */
  case object PfClimateChangeLevy extends Origin {
    def toTaxType: TaxType = TaxTypes.climateChangeLevy
  }

  /** Simple Assessment
    */
  case object PfSimpleAssessment extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Simple Assessment
    */
  case object PtaSimpleAssessment extends Origin {
    def toTaxType: TaxType = TaxTypes.p302
  }

  /** Simple Assessment
    */
  case object AppSimpleAssessment extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Taxes, penalties or enquiry settlements
    */
  case object PfTpes extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Capital Gains Tax
    */
  case object CapitalGainsTax extends Origin {
    def toTaxType: TaxType = TaxTypes.capitalGainsTax
  }

  /** Economic Crime Levy
    */
  case object EconomicCrimeLevy extends Origin {
    def toTaxType: TaxType = TaxTypes.economicCrimeLevy
  }

  /** Pay frontend Economic Crime Levy
    */
  case object PfEconomicCrimeLevy extends Origin {
    def toTaxType: TaxType = TaxTypes.economicCrimeLevy
  }

  /** Pay frontend Job Retention Scheme (logged out)
    */
  case object PfJobRetentionScheme extends Origin {
    def toTaxType: TaxType = TaxTypes.jobRetentionScheme
  }

  /** JRS Job Retention Scheme (logged in)
    */
  case object JrsJobRetentionScheme extends Origin {
    def toTaxType: TaxType = TaxTypes.jobRetentionScheme
  }

  /** Pf Imported Vehicles (logged Out)
    */
  case object PfImportedVehicles extends Origin {
    def toTaxType: TaxType = TaxTypes.importedVehicles
  }

  /** Pay frontend child benefit repayments (logged out)
    */
  case object PfChildBenefitRepayments extends Origin {
    def toTaxType: TaxType = TaxTypes.childBenefitRepayments
  }

  /** One Stop Shop (OSS) Union scheme to report and pay VAT due on distance sales of goods from Northern Ireland to consumers in the EU
    */
  case object NiEuVatOss extends Origin {
    def toTaxType: TaxType = TaxTypes.niEuVatOss
  }

  /** PF-initiated One Stop Shop (OSS) Union scheme to report and pay VAT due on distance sales of goods from Northern Ireland to consumers in the EU
    */
  case object PfNiEuVatOss extends Origin {
    def toTaxType: TaxType = TaxTypes.niEuVatOss
  }

  /** One Stop Shop (OSS) Union scheme to report and pay VAT due on distance sales of goods from EU to consumers in Northern Ireland
    */
  case object NiEuVatIoss extends Origin {
    def toTaxType: TaxType = TaxTypes.niEuVatIoss
  }
  case object PfNiEuVatIoss extends Origin {
    def toTaxType: TaxType = TaxTypes.niEuVatIoss
  }

  /** Anti Money Laundering Regulation Fees originated from Pay Frontend */
  case object PfAmls extends Origin {
    def toTaxType: TaxType = TaxTypes.other
  }

  /** Annual Tax on Enveloped Dwellings originated from Pay Frontend */
  case object PfAted extends Origin {
    def toTaxType: TaxType = TaxTypes.ated
  }

  /** Customs Declaration Service duty deferment from Pay Frontend */
  case object PfCdsDeferment extends Origin {
    def toTaxType: TaxType = TaxTypes.cds
  }

  /** Trusts from Pay Frontend */
  case object PfTrust extends Origin {
    def toTaxType: TaxType = TaxTypes.trust
  }

  case object PtaClass3Ni extends Origin {
    def toTaxType: TaxType = TaxTypes.class3NationalInsurance
  }

  /** AlcoholDuty */
  case object AlcoholDuty extends Origin {
    def toTaxType: TaxType = TaxTypes.alcoholDuty
  }

  /** AlcoholDuty from Pay Frontend */
  case object PfAlcoholDuty extends Origin {
    def toTaxType: TaxType = TaxTypes.alcoholDuty
  }

  /** Vat consumer to consumer */
  case object VatC2c extends Origin {
    def toTaxType: TaxType = TaxTypes.vatConsumerToConsumer
  }

  /** Vat consumer to consumer from Pay Frontend */
  case object PfVatC2c extends Origin {
    def toTaxType: TaxType = TaxTypes.vatConsumerToConsumer
  }

  // payments originating via third party software

  /** 3PS (Third party software) Self Assessment */
  case object `3psSa` extends Origin {
    def toTaxType: TaxType = TaxTypes.selfAssessment
  }

  /** 3PS (Third party software) VAT */
  case object `3psVat` extends Origin {
    def toTaxType: TaxType = TaxTypes.vat
  }

  /** 3PS (Third party software) Corporation tax */
  //  case object `3psCt` extends Origin {
  //    def toTaxType: TaxType = TaxTypes.corporationTax
  //  }

  /** 3PS (Third party software) Employer's pay as you earn */
  //  case object `3psEpaye` extends Origin {
  //    def toTaxType: TaxType = TaxTypes.epaye
  //  }

  /** Pillar2 */
  case object Pillar2 extends Origin {
    def toTaxType: TaxType = TaxTypes.pillar2
  }

  /** Logged out Pillar2 via pay-frontend */
  case object PfPillar2 extends Origin {
    def toTaxType: TaxType = TaxTypes.pillar2
  }

  override def values: immutable.IndexedSeq[Origin] = findValues

  lazy val notUsedOrigins: Set[Origin] = Set()

  lazy val liveOrigins: Set[Origin] = values.toSet -- notUsedOrigins
}
