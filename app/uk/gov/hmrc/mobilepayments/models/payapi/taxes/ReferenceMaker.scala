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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes

import uk.gov.hmrc.mobilepayments.models.payapi.Reference
import uk.gov.hmrc.mobilepayments.models.payapi.cgt.*
import uk.gov.hmrc.mobilepayments.models.payapi.p800.P800ChargeRef
import uk.gov.hmrc.mobilepayments.models.payapi.pngr.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.vat.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ad.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.amls.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.cds.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.cdsd.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ct.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.epaye.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ioss.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.mib.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.p302.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.p800.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.parcels.ParcelsChargeReference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.pillar2.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ppt.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sa.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sd.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sdlt.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.trusts.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.vatc2c.*
import uk.gov.hmrc.mobilepayments.models.payapi.times.period.CalendarQuarterlyPeriod

object ReferenceMaker {

  def makeParcelsReference(chargeReference: ParcelsChargeReference): Reference = Reference(chargeReference.ref)

  def makeVatReturnReference(vrn: Vrn, vatPeriod: CalendarPeriod): Reference = Reference(
    vrn.value + vatPeriod.asReferenceSuffix
  )

  def makeVatReturnReference(vrn: Vrn, maybeVatPeriod: Option[CalendarPeriod]): Reference = Reference(
    vrn.value +
      (maybeVatPeriod match {
        case Some(period) => period.asReferenceSuffix
        case None         => ""
      }).toString
  )
  def makeVatReference(vrn: Vrn): Reference = Reference(
    vrn.canonicalizedValue
  )

  def makeVatOtherReference(chargeReference: VatChargeReference): Reference = Reference(chargeReference.reference)

  def makeSaReference(utr: SaUtr): Reference = Reference(utr.parseSaUtr.value + "K")

  def makeCtReference(utr: CtUtr, ctPeriod: CtPeriod, ctChargeType: CtChargeType): Reference = Reference(
    utr.canonicalizedValue + "A001" + ctPeriod.referenceForm + ctChargeType.referenceSuffixForm
  )

  def makeSdltReference(utrn: Utrn): Reference = Reference(
    utrn.canonicalizedValue
  )

  def makeEpayeNiReference(
    accountsOfficeReference: AccountsOfficeReference,
    fixedLengthEpayeTaxPeriod: FixedLengthEpayeTaxPeriod
  ): Reference =
    Reference(accountsOfficeReference.canonicalizedValue + fixedLengthEpayeTaxPeriod.code)

  def makeEpayePenaltyReference(epayePenaltyReference: EpayePenaltyReference): Reference = Reference(epayePenaltyReference.value)

  def makeXReference(xRef: XRef): Reference = Reference(
    xRef.canonicalizedValue
  )

  def makeXRef14Char(xRef14Char: XRef14Char): Reference = Reference(
    xRef14Char.canonicalizedValue
  )

  def makeEconomicCrimeLevyReturnNumber(economicCrimeLevyReturnNumber: EconomicCrimeLevyReturnNumber): Reference = Reference(
    economicCrimeLevyReturnNumber.canonicalizedValue
  )

  def makeSimpleAssessmentRef(simpleAssessmentReference: XRef14Char): Reference = Reference(
    simpleAssessmentReference.canonicalizedValue
  )

  def makeCgtReference(cgtReference: CgtAccountReference): Reference = Reference(
    cgtReference.canonicalizedValue
  )

  def makeLateCisReference(prn: XRef14Char): Reference = Reference(
    prn.canonicalizedValue
  )

  def makeCdsReference(cdsRef: CdsRef): Reference = Reference(
    cdsRef.canonicalizedValue
  )
  def makeCdsCashReference(cdsCashRef: CdsCashRef): Reference = Reference(
    cdsCashRef.canonicalizedValue
  )

  def makeP800Reference(p800Ref: P800Ref): Reference = Reference(
    p800Ref.canonicalizedValue
  )

  def makeP302Reference(p302Ref: P302Ref): Reference = Reference(
    p302Ref.canonicalizedValue
  )

  def makeP800ChargeReference(p800ChargeRef: P800ChargeRef): Reference = Reference(
    p800ChargeRef.canonicalizedValue
  )

  def makeSetaReference(psaNumber: PsaNumber): Reference = Reference(
    psaNumber.canonicalizedValue
  )

  def makeMibReference(mibReference: MibReference): Reference = Reference(
    mibReference.canonicalizedValue
  )

  def makePngrReference(chargeReference: PngrChargeReference): Reference = Reference(
    chargeReference.value
  )

  def makeAmlsReference(amlsPaymentReference: AmlsPaymentReference): Reference = Reference(
    amlsPaymentReference.canonicalizedValue
  )

  def makeSpiritDrinksReference(spiritDrinksReference: SpiritDrinksReference): Reference = Reference(
    spiritDrinksReference.canonicalizedValue
  )

  def makeInheritanceTaxRef(inheritanceTaxRef: InheritanceTaxRef): Reference = Reference(
    inheritanceTaxRef.canonicalizedValue
  )
  def makeWineAndBeerTaxRef(wineAndBeerTaxRef: WineAndCiderTaxRef): Reference = Reference(
    wineAndBeerTaxRef.canonicalizedValue
  )
  def makeImportedVehiclesRef(importedVehiclesRef: ImportedVehiclesRef): Reference = Reference(
    importedVehiclesRef.canonicalizedValue
  )
  def makePptReference(pptReference: PptReference): Reference = Reference(
    pptReference.canonicalizedValue
  )

  def makeAggregatesLevyReference(aggregatesLevyRef: AggregatesLevyRef): Reference = Reference(
    aggregatesLevyRef.canonicalizedValue
  )
  def makeGamingDutyOrBingoReference(gamingOrBingoDutyRef: GamingOrBingoDutyRef): Reference = Reference(
    gamingOrBingoDutyRef.canonicalizedValue
  )

  def makeJrsReference(jrsReference: JrsRef): Reference = Reference(
    jrsReference.canonicalizedValue
  )

  def makeChildBenefitReference(yReference: YRef): Reference = Reference(
    yReference.canonicalisedValue
  )

  def makeNiEuVatOssReference(vrn: Vrn, period: CalendarQuarterlyPeriod): Reference = Reference(s"NI${vrn.value}Q${period.periodCode}")

  def makeNiEuVatIossReference(ioss: Ioss, period: CalendarPeriod): Reference = Reference(s"${ioss.canonicalizedValue}M${period.asReferenceSuffix}")

  def makeBioFuelsReference(regNo: BioFuelsRegistrationNumber): Reference = Reference(regNo.value)

  def makeClimateChangeLevyRef(ref: ClimateChangeLevyRef): Reference = Reference(
    ref.canonicalisedValue
  )
  def makeInsurancePremiumRef(ref: InsurancePremiumRef): Reference = Reference(
    ref.canonicalisedValue
  )
  def makeClass2NiReference(ref: Class2NiReference): Reference = Reference(
    ref.canonicalisedValue
  )
  def makeAirPassReference(ref: AirPassReference): Reference = Reference(
    ref.canonicalisedValue
  )

  def makeBeerDutyRef(ref: BeerDutyRef): Reference = Reference(ref.canonicalisedValue)

  def makeClass3NiRef(ref: Class3NiRef): Reference = Reference(
    ref.canonicalisedValue
  )

  def makeSoftDrinksIndustryLevyRef(softDrinksIndustryLevyRef: SoftDrinksIndustryLevyRef): Reference = Reference(
    softDrinksIndustryLevyRef.canonicalizedValue
  )

  def makeCdsDefermentReference(cdsDefermentReference: CdsDefermentReference): Reference = Reference(
    cdsDefermentReference.canonicalizedValue
  )
  def makeTrustsReference(trustsReference: TrustReference): Reference = Reference(
    trustsReference.canonicalizedValue
  )

  def makeAlcoholDutyReference(alcoholDutyReference: AlcoholDutyReference): Reference = Reference(
    alcoholDutyReference.canonicalizedValue
  )

  def makeAlcoholDutyChargeReference(alcoholDutyChargeReference: AlcoholDutyChargeReference): Reference = Reference(
    alcoholDutyChargeReference.canonicalizedValue
  )

  def makeVatC2cReference(vatC2cReference: VatC2cReference): Reference = Reference(
    vatC2cReference.canonicalizedValue
  )

  def makePillar2Reference(pillar2Reference: Pillar2Reference): Reference = Reference(
    pillar2Reference.canonicalizedValue
  )
}
