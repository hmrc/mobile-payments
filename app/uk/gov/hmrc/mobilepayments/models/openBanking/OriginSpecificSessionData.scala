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

package uk.gov.hmrc.mobilepayments.models.openBanking


import payapi.corcommon.model.Origins._
import payapi.corcommon.model.cgt.CgtAccountReference
import payapi.corcommon.model.taxes.ReferenceMaker
import payapi.corcommon.model.taxes.ad.{AlcoholDutyChargeReference, AlcoholDutyReference}
import payapi.corcommon.model.taxes.amls.AmlsPaymentReference
import payapi.corcommon.model.taxes.cds.{CdsCashRef, CdsRef}
import payapi.corcommon.model.taxes.cdsd.CdsDefermentReference
import payapi.corcommon.model.taxes.ct.{CtChargeType, CtPeriod, CtUtr}
import payapi.corcommon.model.taxes.epaye._
import payapi.corcommon.model.taxes.ioss.Ioss
import payapi.corcommon.model.taxes.other._
import payapi.corcommon.model.taxes.p302.{P302ChargeRef, P302Ref}
import payapi.corcommon.model.taxes.trusts.TrustReference
import payapi.corcommon.model.taxes.p800.P800Ref
import payapi.corcommon.model.taxes.pillar2.Pillar2Reference
import payapi.corcommon.model.taxes.ppt.PptReference
import payapi.corcommon.model.taxes.sa.SaUtr
import payapi.corcommon.model.taxes.sd.SpiritDrinksReference
import payapi.corcommon.model.taxes.vat.{CalendarPeriod, VatChargeReference, Vrn}
import payapi.corcommon.model.times.period.CalendarQuarterlyPeriod
import payapi.corcommon.model.{Origin, Reference, SearchTag}
import payapi.corcommon.model.taxes.sdlt.Utrn
import payapi.corcommon.model.taxes.vatc2c.VatC2cReference
import payapi.corcommon.model.thirdpartysoftware.{ClientJourneyId, FriendlyName}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json, _}

sealed abstract class OriginSpecificSessionData(val origin: Origin) {
  def paymentReference: Reference
  val returnUrl: Option[String]
  def searchTag: SearchTag
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object OriginSpecificSessionData {
  implicit val reads: Reads[OriginSpecificSessionData] = (json: JsValue) =>
    (__ \ "origin").read[Origin].reads(json).flatMap {
      case PfSa                => Json.format[PfSaSessionData].reads(json)
      case BtaSa               => Json.format[BtaSaSessionData].reads(json)
      case ItSa                => Json.format[ItSaSessionData].reads(json)
      case PtaSa               => Json.format[PtaSaSessionData].reads(json)
      case BtaEpayeGeneral     => Json.format[BtaEpayeGeneralSessionData].reads(json)
      case BtaEpayeBill        => Json.format[BtaEpayeBillSessionData].reads(json)
      case BtaEpayeInterest    => Json.format[BtaEpayeInterestSessionData].reads(json)
      case BtaEpayePenalty     => Json.format[BtaEpayePenaltySessionData].reads(json)
      case BtaClass1aNi        => Json.format[BtaClass1aNiSessionData].reads(json)
      case BtaCt               => Json.format[BtaCtSessionData].reads(json)
      case BtaVat              => Json.format[BtaVatSessionData].reads(json)
      case VcVatReturn         => Json.format[VcVatReturnSessionData].reads(json)
      case VcVatOther          => Json.format[VcVatOtherSessionData].reads(json)
      case PfVat               => Json.format[PfVatSessionData].reads(json)
      case PfCt                => Json.format[PfCtSessionData].reads(json)
      case PfEpayeNi           => Json.format[PfEpayeNiSessionData].reads(json)
      case PfEpayeLpp          => Json.format[PfEpayeLppSessionData].reads(json)
      case PfEpayeSeta         => Json.format[PfEpayeSetaSessionData].reads(json)
      case PfEpayeLateCis      => Json.format[PfEpayeLateCisSessionData].reads(json)
      case PfEpayeP11d         => Json.format[PfEpayeP11dSessionData].reads(json)
      case NiEuVatOss          => Json.format[NiEuVatOssSessionData].reads(json)
      case PfNiEuVatOss        => Json.format[PfNiEuVatOssSessionData].reads(json)
      case NiEuVatIoss         => Json.format[NiEuVatIossSessionData].reads(json)
      case PfNiEuVatIoss       => Json.format[PfNiEuVatIossSessionData].reads(json)
      case CapitalGainsTax     => Json.format[CapitalGainsTaxSessionData].reads(json)
      case PtaSimpleAssessment => Json.format[PtaSimpleAssessmentSessionData].reads(json)
      case PfSimpleAssessment => Json.format[PfSimpleAssessmentSessionData].reads(json) match {
        case success: JsSuccess[PfSimpleAssessmentSessionData] => success
        case _ => (
          (JsPath \ "xRef").read[XRef14Char] and
            (JsPath \ "returnUrl").readNullable[String]
          )(PfSimpleAssessmentSessionData.apply _).reads(json)
      }
      case PfBioFuels               => Json.format[PfBioFuelsSessionData].reads(json)
      case PfSdlt                   => Json.format[PfSdltSessionData].reads(json)
      case PfGbPbRgDuty             => Json.format[PfGbPbRgDutySessionData].reads(json)
      case PfMgd                    => Json.format[PfMgdSessionData].reads(json)
      case PfGamingOrBingoDuty      => Json.format[PfGamingOrBingoDutySessionData].reads(json)
      case PfAmls                   => Json.format[PfAmlsSessionData].reads(json)
      case Amls                     => Json.format[AmlsSessionData].reads(json)
      case PfTpes                   => Json.format[PfTpesSessionData].reads(json)
      case PfChildBenefitRepayments => Json.format[PfChildBenefitSessionData].reads(json)
      case PfAggregatesLevy         => Json.format[PfAggregatesLevySessionData].reads(json)
      case PfLandfillTax            => Json.format[PfLandfillTaxSessionData].reads(json)
      case PfCds                    => Json.format[PfCdsSessionData].reads(json)
      case PfClimateChangeLevy      => Json.format[PfClimateChangeLevySessionData].reads(json)
      case PfInsurancePremium       => Json.format[PfInsurancePremiumSessionData].reads(json)
      case PfAirPass                => Json.format[PfAirPassSessionData].reads(json)
      case PfClass2Ni               => Json.format[PfClass2NiSessionData].reads(json)
      case PfBeerDuty               => Json.format[PfBeerDutySessionData].reads(json)
      case PfPsAdmin                => Json.format[PfPsAdminTaxSessionData].reads(json)
      case PfClass3Ni               => Json.format[PfClass3NiSessionData].reads(json)
      case PtaClass3Ni              => Json.format[PtaClass3NiSessionData].reads(json)
      case Ppt                      => Json.format[PptSessionData].reads(json)
      case PfPpt                    => Json.format[PfPptSessionData].reads(json)
      case PfSdil                   => Json.format[PfSdilSessionData].reads(json)
      case BtaSdil                  => Json.format[BtaSdilSessionData].reads(json)
      case PfInheritanceTax         => Json.format[PfInheritanceTaxSessionData].reads(json)
      case PfWineAndCider           => Json.format[PfWineAndCiderTaxSessionData].reads(json)
      case PfSpiritDrinks           => Json.format[PfSpiritDrinksSessionData].reads(json)
      case PfImportedVehicles       => Json.format[PfImportedVehiclesSessionData].reads(json)
      case AppSa                    => Json.format[AppSaSessionData].reads(json)
      case AppSimpleAssessment      => Json.format[AppSimpleAssessmentSessionData].reads(json)
      case PfAted                   => Json.format[PfAtedSessionData].reads(json)
      case PfCdsCash                => Json.format[PfCdsCashSessionData].reads(json)
      case PfCdsDeferment           => Json.format[PfCdsDefermentSessionData].reads(json)
      case PfTrust                  => Json.format[PfTrustSessionData].reads(json)
      case EconomicCrimeLevy        => Json.format[EconomicCrimeLevySessionData].reads(json)
      case PfEconomicCrimeLevy      => Json.format[PfEconomicCrimeLevySessionData].reads(json)
      case PfAlcoholDuty            => Json.format[PfAlcoholDutySessionData].reads(json)
      case AlcoholDuty              => Json.format[AlcoholDutySessionData].reads(json)
      case PfVatC2c                 => Json.format[PfVatC2cSessionData].reads(json)
      case VatC2c                   => Json.format[VatC2cSessionData].reads(json)
      case `3psSa`                  => Json.format[`3psSaSessionData`].reads(json)
      case `3psVat`                 => Json.format[`3psVatSessionData`].reads(json)
      case Pillar2                  => Json.format[Pillar2SessionData].reads(json)
      case PfPillar2                => Json.format[PfPillar2SessionData].reads(json)

      //Todo: Remove PfP800 when PtaP800 is fully available
      case origin @ (PfOther | PtaP800 | PfP800
                     | BcPngr | Parcels | DdVat | DdSdil | Mib | PfSimpleAssessment
                     | PtaSimpleAssessment | PfJobRetentionScheme | JrsJobRetentionScheme) =>
        throw new RuntimeException(s"Trying to read JSON for unimplemented Origin: ${origin.toString}")
    }

  implicit val writes: OWrites[OriginSpecificSessionData] = (o: OriginSpecificSessionData) =>
    (o match {
      case sessionData: PfSaSessionData                => Json.format[PfSaSessionData].writes(sessionData)
      case sessionData: BtaSaSessionData               => Json.format[BtaSaSessionData].writes(sessionData)
      case sessionData: PtaSaSessionData               => Json.format[PtaSaSessionData].writes(sessionData)
      case sessionData: ItSaSessionData                => Json.format[ItSaSessionData].writes(sessionData)
      case sessionData: BtaEpayeGeneralSessionData     => Json.format[BtaEpayeGeneralSessionData].writes(sessionData)
      case sessionData: BtaEpayeBillSessionData        => Json.format[BtaEpayeBillSessionData].writes(sessionData)
      case sessionData: BtaEpayeInterestSessionData    => Json.format[BtaEpayeInterestSessionData].writes(sessionData)
      case sessionData: BtaEpayePenaltySessionData     => Json.format[BtaEpayePenaltySessionData].writes(sessionData)
      case sessionData: BtaClass1aNiSessionData        => Json.format[BtaClass1aNiSessionData].writes(sessionData)
      case sessionData: BtaCtSessionData               => Json.format[BtaCtSessionData].writes(sessionData)
      case sessionData: BtaVatSessionData              => Json.format[BtaVatSessionData].writes(sessionData)
      case sessionData: VcVatReturnSessionData         => Json.format[VcVatReturnSessionData].writes(sessionData)
      case sessionData: VcVatOtherSessionData          => Json.format[VcVatOtherSessionData].writes(sessionData)
      case sessionData: PfVatSessionData               => Json.format[PfVatSessionData].writes(sessionData)
      case sessionData: PfCtSessionData                => Json.format[PfCtSessionData].writes(sessionData)
      case sessionData: PfEpayeNiSessionData           => Json.format[PfEpayeNiSessionData].writes(sessionData)
      case sessionData: PfEpayeLppSessionData          => Json.format[PfEpayeLppSessionData].writes(sessionData)
      case sessionData: PfEpayeSetaSessionData         => Json.format[PfEpayeSetaSessionData].writes(sessionData)
      case sessionData: PfEpayeLateCisSessionData      => Json.format[PfEpayeLateCisSessionData].writes(sessionData)
      case sessionData: PfEpayeP11dSessionData         => Json.format[PfEpayeP11dSessionData].writes(sessionData)
      case sessionData: NiEuVatOssSessionData          => Json.format[NiEuVatOssSessionData].writes(sessionData)
      case sessionData: PfNiEuVatOssSessionData        => Json.format[PfNiEuVatOssSessionData].writes(sessionData)
      case sessionData: NiEuVatIossSessionData         => Json.format[NiEuVatIossSessionData].writes(sessionData)
      case sessionData: PfNiEuVatIossSessionData       => Json.format[PfNiEuVatIossSessionData].writes(sessionData)
      case sessionData: CapitalGainsTaxSessionData     => Json.format[CapitalGainsTaxSessionData].writes(sessionData)
      case sessionData: PtaSimpleAssessmentSessionData => Json.format[PtaSimpleAssessmentSessionData].writes(sessionData)
      case sessionData: PfSimpleAssessmentSessionData  => Json.format[PfSimpleAssessmentSessionData].writes(sessionData)
      case sessionData: PfBioFuelsSessionData          => Json.format[PfBioFuelsSessionData].writes(sessionData)
      case sessionData: PfSdltSessionData              => Json.format[PfSdltSessionData].writes(sessionData)
      case sessionData: PfMgdSessionData               => Json.format[PfMgdSessionData].writes(sessionData)
      case sessionData: PfGamingOrBingoDutySessionData => Json.format[PfGamingOrBingoDutySessionData].writes(sessionData)
      case sessionData: PfGbPbRgDutySessionData        => Json.format[PfGbPbRgDutySessionData].writes(sessionData)
      case sessionData: PfAmlsSessionData              => Json.format[PfAmlsSessionData].writes(sessionData)
      case sessionData: AmlsSessionData                => Json.format[AmlsSessionData].writes(sessionData)
      case sessionData: PfTpesSessionData              => Json.format[PfTpesSessionData].writes(sessionData)
      case sessionData: PfChildBenefitSessionData      => Json.format[PfChildBenefitSessionData].writes(sessionData)
      case sessionData: PfAggregatesLevySessionData    => Json.format[PfAggregatesLevySessionData].writes(sessionData)
      case sessionData: PfLandfillTaxSessionData       => Json.format[PfLandfillTaxSessionData].writes(sessionData)
      case sessionData: PfCdsSessionData               => Json.format[PfCdsSessionData].writes(sessionData)
      case sessionData: PfClimateChangeLevySessionData => Json.format[PfClimateChangeLevySessionData].writes(sessionData)
      case sessionData: PfInsurancePremiumSessionData  => Json.format[PfInsurancePremiumSessionData].writes(sessionData)
      case sessionData: PfClass2NiSessionData          => Json.format[PfClass2NiSessionData].writes(sessionData)
      case sessionData: PfAirPassSessionData           => Json.format[PfAirPassSessionData].writes(sessionData)
      case sessionData: PfBeerDutySessionData          => Json.format[PfBeerDutySessionData].writes(sessionData)
      case sessionData: PfPsAdminTaxSessionData        => Json.format[PfPsAdminTaxSessionData].writes(sessionData)
      case sessionData: PfClass3NiSessionData          => Json.format[PfClass3NiSessionData].writes(sessionData)
      case sessionData: PtaClass3NiSessionData         => Json.format[PtaClass3NiSessionData].writes(sessionData)
      case sessionData: PptSessionData                 => Json.format[PptSessionData].writes(sessionData)
      case sessionData: PfPptSessionData               => Json.format[PfPptSessionData].writes(sessionData)
      case sessionData: PfSdilSessionData              => Json.format[PfSdilSessionData].writes(sessionData)
      case sessionData: BtaSdilSessionData             => Json.format[BtaSdilSessionData].writes(sessionData)
      case sessionData: PfInheritanceTaxSessionData    => Json.format[PfInheritanceTaxSessionData].writes(sessionData)
      case sessionData: PfWineAndCiderTaxSessionData   => Json.format[PfWineAndCiderTaxSessionData].writes(sessionData)
      case sessionData: PfSpiritDrinksSessionData      => Json.format[PfSpiritDrinksSessionData].writes(sessionData)
      case sessionData: PfImportedVehiclesSessionData  => Json.format[PfImportedVehiclesSessionData].writes(sessionData)
      case sessionData: AppSaSessionData               => Json.format[AppSaSessionData].writes(sessionData)
      case sessionData: AppSimpleAssessmentSessionData => Json.format[AppSimpleAssessmentSessionData].writes(sessionData)
      case sessionData: PfAtedSessionData              => Json.format[PfAtedSessionData].writes(sessionData)
      case sessionData: PfCdsCashSessionData           => Json.format[PfCdsCashSessionData].writes(sessionData)
      case sessionData: PfCdsDefermentSessionData      => Json.format[PfCdsDefermentSessionData].writes(sessionData)
      case sessionData: PfTrustSessionData             => Json.format[PfTrustSessionData].writes(sessionData)
      case sessionData: EconomicCrimeLevySessionData   => Json.format[EconomicCrimeLevySessionData].writes(sessionData)
      case sessionData: PfEconomicCrimeLevySessionData => Json.format[PfEconomicCrimeLevySessionData].writes(sessionData)
      case sessionData: PfAlcoholDutySessionData       => Json.format[PfAlcoholDutySessionData].writes(sessionData)
      case sessionData: AlcoholDutySessionData         => Json.format[AlcoholDutySessionData].writes(sessionData)
      case sessionData: PfVatC2cSessionData            => Json.format[PfVatC2cSessionData].writes(sessionData)
      case sessionData: VatC2cSessionData              => Json.format[VatC2cSessionData].writes(sessionData)
      case sessionData: `3psSaSessionData`             => Json.format[`3psSaSessionData`].writes(sessionData)
      case sessionData: `3psVatSessionData`            => Json.format[`3psVatSessionData`].writes(sessionData)
      case sessionData: Pillar2SessionData             => Json.format[Pillar2SessionData].writes(sessionData)
      case sessionData: PfPillar2SessionData           => Json.format[PfPillar2SessionData].writes(sessionData)
    }) + ("origin" -> Json.toJson(o.origin))

  implicit val format: OFormat[OriginSpecificSessionData] = OFormat(reads, writes)
}

sealed abstract class SelfAssessmentSessionData(origin: Origin) extends OriginSpecificSessionData(origin) {
  def saUtr: SaUtr
  def paymentReference: Reference = ReferenceMaker.makeSaReference(saUtr)
  val returnUrl: Option[String]
}

final case class PfSaSessionData(saUtr: SaUtr, returnUrl: Option[String] = None) extends SelfAssessmentSessionData(PfSa) {
  def searchTag = SearchTag(saUtr.value)
}

final case class BtaSaSessionData(saUtr: SaUtr, override val returnUrl: Option[String] = None) extends SelfAssessmentSessionData(BtaSa) {
  def searchTag = SearchTag(saUtr.value)
}

final case class AppSaSessionData(saUtr: SaUtr, override val returnUrl: Option[String] = None) extends SelfAssessmentSessionData(AppSa) {
  def searchTag = SearchTag(saUtr.value)
}

final case class PtaSaSessionData(saUtr: SaUtr, override val returnUrl: Option[String] = None) extends SelfAssessmentSessionData(PtaSa) {
  def searchTag = SearchTag(saUtr.value)
}

final case class ItSaSessionData(saUtr: SaUtr, override val returnUrl: Option[String] = None) extends SelfAssessmentSessionData(ItSa) {
  def searchTag = SearchTag(saUtr.value)
}

sealed trait ThirdPartySoftwareSessionData {
  val clientJourneyId: ClientJourneyId
}

final case class `3psSaSessionData`(
                                     saUtr:                  SaUtr,
                                     clientJourneyId:        ClientJourneyId,
                                     friendlyName:           Option[FriendlyName],
                                     override val returnUrl: Option[String]       = None
                                   ) extends SelfAssessmentSessionData(`3psSa`) with ThirdPartySoftwareSessionData {
  def searchTag = SearchTag(saUtr.value)
}

final case class `3psVatSessionData`(
                                      vrn:                    Vrn,
                                      clientJourneyId:        ClientJourneyId,
                                      friendlyName:           Option[FriendlyName],
                                      override val returnUrl: Option[String]       = None
                                    ) extends VatSessionData(`3psVat`) with ThirdPartySoftwareSessionData {
  def paymentReference: Reference = ReferenceMaker.makeVatReference(vrn)
  def searchTag = SearchTag(vrn.value)
}

sealed abstract class PayeSessionData(origin: Origin) extends OriginSpecificSessionData(origin) {}

final case class BtaEpayeGeneralSessionData(
                                             accountsOfficeReference: AccountsOfficeReference,
                                             period:                  SubYearlyEpayeTaxPeriod,
                                             returnUrl:               Option[String]          = None
                                           ) extends PayeSessionData(BtaEpayeGeneral) {
  def paymentReference: Reference = ReferenceMaker.makeEpayeNiReference(accountsOfficeReference, period)
  def searchTag = SearchTag(accountsOfficeReference.canonicalizedValue)
}

final case class BtaEpayeBillSessionData(
                                          accountsOfficeReference: AccountsOfficeReference,
                                          period:                  SubYearlyEpayeTaxPeriod,
                                          returnUrl:               Option[String]          = None
                                        ) extends PayeSessionData(BtaEpayeBill) {
  def paymentReference: Reference = ReferenceMaker.makeEpayeNiReference(accountsOfficeReference, period)
  def searchTag = SearchTag(accountsOfficeReference.canonicalizedValue)
}

final case class BtaEpayeInterestSessionData(
                                              payeInterestXRef: XRef,
                                              returnUrl:        Option[String] = None
                                            ) extends PayeSessionData(BtaEpayeInterest) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(payeInterestXRef)
  def searchTag = SearchTag(payeInterestXRef.canonicalizedValue)
}

final case class BtaEpayePenaltySessionData(
                                             epayePenaltyReference: EpayePenaltyReference,
                                             returnUrl:             Option[String]        = None
                                           ) extends PayeSessionData(BtaEpayePenalty) {
  def paymentReference: Reference = ReferenceMaker.makeEpayePenaltyReference(epayePenaltyReference)
  def searchTag = SearchTag(epayePenaltyReference.value)
}

final case class BtaClass1aNiSessionData(
                                          accountsOfficeReference: AccountsOfficeReference,
                                          period:                  YearlyEpayeTaxPeriod,
                                          returnUrl:               Option[String]          = None
                                        ) extends PayeSessionData(BtaClass1aNi) {
  def paymentReference: Reference = ReferenceMaker.makeEpayeNiReference(accountsOfficeReference, period)
  def searchTag = SearchTag(accountsOfficeReference.canonicalizedValue)
}

sealed abstract class CoTaxSessionData(origin: Origin) extends OriginSpecificSessionData(origin)

final case class BtaCtSessionData(
                                   utr:          CtUtr,
                                   ctPeriod:     CtPeriod,
                                   ctChargeType: CtChargeType,
                                   returnUrl:    Option[String] = None
                                 ) extends CoTaxSessionData(BtaCt) {
  def paymentReference: Reference = ReferenceMaker.makeCtReference(utr, ctPeriod, ctChargeType)
  def searchTag = SearchTag(utr.canonicalizedValue)
}

final case class PfCtSessionData(
                                  utr:          CtUtr,
                                  ctPeriod:     CtPeriod,
                                  ctChargeType: CtChargeType,
                                  returnUrl:    Option[String] = None
                                ) extends CoTaxSessionData(PfCt) {
  def paymentReference: Reference = ReferenceMaker.makeCtReference(utr, ctPeriod, ctChargeType)
  def searchTag = SearchTag(utr.canonicalizedValue)
}

sealed abstract class VatSessionData(origin: Origin) extends OriginSpecificSessionData(origin)

final case class BtaVatSessionData(vrn: Vrn, returnUrl: Option[String] = None) extends VatSessionData(BtaVat) {
  def paymentReference: Reference = ReferenceMaker.makeVatReference(vrn)
  def searchTag = SearchTag(vrn.value)
}

final case class VcVatReturnSessionData(vrn: Vrn, returnUrl: Option[String] = None) extends VatSessionData(VcVatReturn) {
  def paymentReference: Reference = ReferenceMaker.makeVatReference(vrn)
  def searchTag = SearchTag(vrn.value)
}

final case class VcVatOtherSessionData(vrn: Vrn, vatChargeReference: VatChargeReference, returnUrl: Option[String] = None) extends VatSessionData(VcVatOther) {
  def paymentReference: Reference = ReferenceMaker.makeVatReference(vrn)
  def searchTag = SearchTag(vatChargeReference.reference)
}

final case class PfVatSessionData(vrn: Option[Vrn], chargeRef: Option[XRef14Char], returnUrl: Option[String] = None) extends VatSessionData(PfVat) {
  def vatReference: Option[Reference] = vrn.map(ReferenceMaker.makeVatReference)
  def chargeReference: Option[Reference] = chargeRef.map(ReferenceMaker.makeXRef14Char)

  def paymentReference: Reference = (vatReference, chargeReference) match {
    case (Some(vrn), _) => vrn
    case (_, Some(ref)) => ref
    case _              => throw new IllegalStateException("[OriginSpecificData][PfVatSessionData] Unable to set paymentReference for PfVatSessionData")
  }

  def searchTag = SearchTag(paymentReference.value)
}

final case class PfEpayeNiSessionData(accountsOfficeReference: AccountsOfficeReference, period: SubYearlyEpayeTaxPeriod, returnUrl: Option[String] = None) extends PayeSessionData(PfEpayeNi) {
  def paymentReference: Reference = ReferenceMaker.makeEpayeNiReference(accountsOfficeReference, period)
  def searchTag = SearchTag(accountsOfficeReference.canonicalizedValue)
}

final case class PfEpayeLppSessionData(payeInterestXRef: XRef, returnUrl: Option[String] = None) extends PayeSessionData(PfEpayeLpp) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(payeInterestXRef)
  def searchTag = SearchTag(payeInterestXRef.canonicalizedValue)
}

final case class PfEpayeSetaSessionData(psaNumber: PsaNumber, returnUrl: Option[String] = None) extends PayeSessionData(PfEpayeSeta) {
  def paymentReference: Reference = ReferenceMaker.makeSetaReference(psaNumber)
  def searchTag = SearchTag(psaNumber.canonicalizedValue)
}

final case class PfEpayeLateCisSessionData(payeInterestXRef: XRef14Char, returnUrl: Option[String] = None) extends PayeSessionData(PfEpayeLateCis) {
  def paymentReference: Reference = ReferenceMaker.makeLateCisReference(payeInterestXRef)
  def searchTag = SearchTag(payeInterestXRef.canonicalizedValue)
}

final case class PfEpayeP11dSessionData(accountsOfficeReference: AccountsOfficeReference, period: YearlyEpayeTaxPeriod, returnUrl: Option[String] = None) extends PayeSessionData(PfEpayeP11d) {
  def paymentReference: Reference = ReferenceMaker.makeEpayeNiReference(accountsOfficeReference, period)
  def searchTag = SearchTag(accountsOfficeReference.canonicalizedValue)
}

final case class CapitalGainsTaxSessionData(cgtReference: CgtAccountReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(CapitalGainsTax) {
  def paymentReference: Reference = ReferenceMaker.makeCgtReference(cgtReference)
  def searchTag = SearchTag(cgtReference.canonicalizedValue)
}

final case class NiEuVatOssSessionData(vrn: Vrn, period: CalendarQuarterlyPeriod, returnUrl: Option[String] = None) extends OriginSpecificSessionData(NiEuVatOss) {
  def paymentReference: Reference = ReferenceMaker.makeNiEuVatOssReference(vrn, period)
  def searchTag = SearchTag(vrn.value)
}

final case class PfNiEuVatOssSessionData(vrn: Vrn, period: CalendarQuarterlyPeriod, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfNiEuVatOss) {
  def paymentReference: Reference = ReferenceMaker.makeNiEuVatOssReference(vrn, period)
  def searchTag = SearchTag(vrn.value)
}

final case class NiEuVatIossSessionData(ioss: Ioss, period: CalendarPeriod, returnUrl: Option[String] = None) extends OriginSpecificSessionData(NiEuVatIoss) {
  def paymentReference: Reference = ReferenceMaker.makeNiEuVatIossReference(ioss, period)

  def searchTag = SearchTag(ioss.value)
}

final case class PfNiEuVatIossSessionData(ioss: Ioss, period: CalendarPeriod, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfNiEuVatIoss) {
  def paymentReference: Reference = ReferenceMaker.makeNiEuVatIossReference(ioss, period)
  def searchTag = SearchTag(ioss.value)
}

final case class PtaSimpleAssessmentSessionData(p302Ref: P302Ref, p302ChargeRef: P302ChargeRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PtaSimpleAssessment) {
  def paymentReference: Reference = Reference(p302ChargeRef.canonicalizedValue)
  def searchTag = SearchTag(p302ChargeRef.canonicalizedValue)
}

final case class AppSimpleAssessmentSessionData(p302Ref: P800Ref, override val returnUrl: Option[String] = None) extends OriginSpecificSessionData(AppSimpleAssessment) {
  def paymentReference: Reference = Reference(p302Ref.canonicalizedValue)
  def searchTag = SearchTag(p302Ref.canonicalizedValue)
}

final case class PfSimpleAssessmentSessionData(simpleAssessmentReference: XRef14Char, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfSimpleAssessment) {
  def paymentReference: Reference = ReferenceMaker.makeSimpleAssessmentRef(simpleAssessmentReference)
  def searchTag = SearchTag(simpleAssessmentReference.canonicalizedValue)
}

final case class PfBioFuelsSessionData(bioFuelsRegistrationNumber: BioFuelsRegistrationNumber, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfBioFuels) {
  def paymentReference: Reference = ReferenceMaker.makeBioFuelsReference(bioFuelsRegistrationNumber)
  def searchTag = SearchTag(bioFuelsRegistrationNumber.value)
}

final case class PfSdltSessionData(utrn: Utrn, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfSdlt) {
  def paymentReference: Reference = ReferenceMaker.makeSdltReference(utrn)
  def searchTag = SearchTag(utrn.canonicalizedValue)
}

final case class PfMgdSessionData(xRef14Char: XRef14Char, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfMgd) {
  def paymentReference: Reference = ReferenceMaker.makeXRef14Char(xRef14Char)
  def searchTag = SearchTag(xRef14Char.canonicalizedValue)
}
final case class PfGamingOrBingoDutySessionData(xRef: XRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfGamingOrBingoDuty) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(xRef)
  def searchTag = SearchTag(xRef.canonicalizedValue)
}
final case class PfGbPbRgDutySessionData(generalBettingXRef: XRef14Char, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfGbPbRgDuty) {
  def paymentReference: Reference = ReferenceMaker.makeXRef14Char(generalBettingXRef)
  def searchTag = SearchTag(generalBettingXRef.canonicalizedValue)
}

final case class PfAmlsSessionData(amlsPaymentReference: AmlsPaymentReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfAmls) {
  def paymentReference: Reference = ReferenceMaker.makeAmlsReference(amlsPaymentReference)
  def searchTag = SearchTag(amlsPaymentReference.canonicalizedValue)
}

final case class PfTpesSessionData(xRef: XRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfTpes) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(xRef)
  def searchTag = SearchTag(xRef.canonicalizedValue)
}

final case class PfChildBenefitSessionData(yRef: YRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfChildBenefitRepayments) {
  def paymentReference: Reference = ReferenceMaker.makeChildBenefitReference(yRef)
  def searchTag = SearchTag(yRef.value)
}

final case class PfAggregatesLevySessionData(aggregatesLevyRef: AggregatesLevyRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfAggregatesLevy) {
  def paymentReference: Reference = ReferenceMaker.makeAggregatesLevyReference(aggregatesLevyRef)
  def searchTag = SearchTag(aggregatesLevyRef.canonicalizedValue)
}
final case class PfLandfillTaxSessionData(xRef: XRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfLandfillTax) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(xRef)
  def searchTag = SearchTag(xRef.canonicalizedValue)
}
final case class AmlsSessionData(amlsPaymentReference: AmlsPaymentReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(Amls) {
  def paymentReference: Reference = ReferenceMaker.makeAmlsReference(amlsPaymentReference)
  def searchTag = SearchTag(amlsPaymentReference.canonicalizedValue)
}

final case class PfCdsSessionData(cdsRef: CdsRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfCds) {
  def paymentReference: Reference = ReferenceMaker.makeCdsReference(cdsRef)
  def searchTag = SearchTag(cdsRef.canonicalizedValue)
}

final case class PfClimateChangeLevySessionData(climateChangeLevyRef: ClimateChangeLevyRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfClimateChangeLevy) {
  def paymentReference: Reference = ReferenceMaker.makeClimateChangeLevyRef(climateChangeLevyRef)
  def searchTag = SearchTag(climateChangeLevyRef.value)
}

final case class PfInsurancePremiumSessionData(insurancePremiumRef: InsurancePremiumRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfInsurancePremium) {
  def paymentReference: Reference = ReferenceMaker.makeInsurancePremiumRef(insurancePremiumRef)
  def searchTag = SearchTag(insurancePremiumRef.canonicalisedValue)
}
final case class PfAirPassSessionData(airPassRef: AirPassReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfAirPass) {
  def paymentReference: Reference = ReferenceMaker.makeAirPassReference(airPassRef)
  def searchTag = SearchTag(airPassRef.canonicalisedValue)
}

final case class PfClass2NiSessionData(class2NiReference: Class2NiReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfClass2Ni) {
  def paymentReference: Reference = ReferenceMaker.makeClass2NiReference(class2NiReference)
  def searchTag = SearchTag(class2NiReference.canonicalisedValue)
}

final case class PfBeerDutySessionData(beerDutyRef: BeerDutyRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfBeerDuty) {
  def paymentReference: Reference = ReferenceMaker.makeBeerDutyRef(beerDutyRef)
  def searchTag = SearchTag(beerDutyRef.canonicalisedValue)
}

final case class PfPsAdminTaxSessionData(xRef: XRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfPsAdmin) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(xRef)
  def searchTag = SearchTag(xRef.canonicalizedValue)
}

final case class PptSessionData(pptReference: PptReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(Ppt) {
  def paymentReference: Reference = ReferenceMaker.makePptReference(pptReference)
  def searchTag = SearchTag(pptReference.canonicalizedValue)
}

final case class PfPptSessionData(pptReference: PptReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfPpt) {
  def paymentReference: Reference = ReferenceMaker.makePptReference(pptReference)
  def searchTag = SearchTag(pptReference.canonicalizedValue)
}

final case class PfClass3NiSessionData(class3Ref: Class3NiRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfClass3Ni) {
  def paymentReference: Reference = ReferenceMaker.makeClass3NiRef(class3Ref)
  def searchTag = SearchTag(class3Ref.canonicalisedValue)
}

final case class PtaClass3NiSessionData(class3NiRef: Class3NiRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PtaClass3Ni) {
  def paymentReference: Reference = ReferenceMaker.makeClass3NiRef(class3NiRef)
  def searchTag: SearchTag = SearchTag(class3NiRef.value)
}

final case class PfSdilSessionData(softDrinksIndustryLevyRef: SoftDrinksIndustryLevyRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfSdil) {
  def paymentReference: Reference = ReferenceMaker.makeSoftDrinksIndustryLevyRef(softDrinksIndustryLevyRef)
  def searchTag = SearchTag(softDrinksIndustryLevyRef.canonicalizedValue)
}

final case class BtaSdilSessionData(xRef: XRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(BtaSdil) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(xRef)
  def searchTag = SearchTag(xRef.canonicalizedValue)
}
final case class PfInheritanceTaxSessionData(inheritanceTaxRef: InheritanceTaxRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfInheritanceTax) {
  def paymentReference: Reference = ReferenceMaker.makeInheritanceTaxRef(inheritanceTaxRef)
  def searchTag = SearchTag(inheritanceTaxRef.canonicalizedValue)
}
final case class PfWineAndCiderTaxSessionData(wineAndTaxRef: WineAndCiderTaxRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfWineAndCider) {
  def paymentReference: Reference = ReferenceMaker.makeWineAndBeerTaxRef(wineAndTaxRef)
  def searchTag = SearchTag(wineAndTaxRef.canonicalizedValue)
}
final case class PfSpiritDrinksSessionData(spiritDrinksReference: SpiritDrinksReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfSpiritDrinks) {
  def paymentReference: Reference = ReferenceMaker.makeSpiritDrinksReference(spiritDrinksReference)
  def searchTag = SearchTag(spiritDrinksReference.canonicalizedValue)
}
final case class PfImportedVehiclesSessionData(importedVehiclesRef: ImportedVehiclesRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfImportedVehicles) {
  def paymentReference: Reference = ReferenceMaker.makeImportedVehiclesRef(importedVehiclesRef)
  def searchTag = SearchTag(importedVehiclesRef.canonicalizedValue)
}

final case class PfAtedSessionData(xRef: XRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfAted) {
  def paymentReference: Reference = ReferenceMaker.makeXReference(xRef)
  def searchTag = SearchTag(xRef.canonicalizedValue)
}
final case class PfCdsCashSessionData(cdsRef: CdsCashRef, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfCdsCash) {
  def paymentReference: Reference = ReferenceMaker.makeCdsCashReference(cdsRef)
  def searchTag = SearchTag(cdsRef.canonicalizedValue)
}

final case class PfCdsDefermentSessionData(cdsDefermentReference: CdsDefermentReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfCdsDeferment) {
  def paymentReference: Reference = ReferenceMaker.makeCdsDefermentReference(cdsDefermentReference)
  def searchTag: SearchTag = SearchTag(cdsDefermentReference.canonicalizedValue)
}

final case class PfTrustSessionData(trustReference: TrustReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfTrust) {
  def paymentReference: Reference = ReferenceMaker.makeTrustsReference(trustReference)
  def searchTag: SearchTag = SearchTag(trustReference.canonicalizedValue)
}

final case class EconomicCrimeLevySessionData(economicCrimeLevyReturnNumber: EconomicCrimeLevyReturnNumber, returnUrl: Option[String] = None) extends OriginSpecificSessionData(EconomicCrimeLevy) {
  def paymentReference: Reference = ReferenceMaker.makeEconomicCrimeLevyReturnNumber(economicCrimeLevyReturnNumber)

  def searchTag: SearchTag = SearchTag(economicCrimeLevyReturnNumber.canonicalizedValue)
}

final case class PfEconomicCrimeLevySessionData(economicCrimeLevyReturnNumber: EconomicCrimeLevyReturnNumber, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfEconomicCrimeLevy) {
  def paymentReference: Reference = ReferenceMaker.makeEconomicCrimeLevyReturnNumber(economicCrimeLevyReturnNumber)
  def searchTag: SearchTag = SearchTag(economicCrimeLevyReturnNumber.canonicalizedValue)
}

final case class PfAlcoholDutySessionData(alcoholDutyReference: AlcoholDutyReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfAlcoholDuty) {
  def paymentReference: Reference = ReferenceMaker.makeAlcoholDutyReference(alcoholDutyReference)
  def searchTag: SearchTag = SearchTag(alcoholDutyReference.canonicalizedValue)
}

final case class AlcoholDutySessionData(alcoholDutyReference: AlcoholDutyReference, alcoholDutyChargeReference: Option[AlcoholDutyChargeReference], returnUrl: Option[String] = None) extends OriginSpecificSessionData(AlcoholDuty) {
  //try and use charge reference as reference, if not provided, use alcoholDutyReference instead.
  def paymentReference: Reference =
    alcoholDutyChargeReference.fold(ReferenceMaker.makeAlcoholDutyReference(alcoholDutyReference)) {
      adcr => ReferenceMaker.makeAlcoholDutyChargeReference(adcr)
    }

  def searchTag: SearchTag = SearchTag(alcoholDutyReference.canonicalizedValue)
}

final case class PfVatC2cSessionData(vatC2cReference: VatC2cReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfVatC2c) {
  def paymentReference: Reference = ReferenceMaker.makeVatC2cReference(vatC2cReference)

  def searchTag: SearchTag = SearchTag(vatC2cReference.canonicalizedValue)
}

final case class VatC2cSessionData(vatC2cReference: VatC2cReference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(VatC2c) {
  def paymentReference: Reference = ReferenceMaker.makeVatC2cReference(vatC2cReference)

  def searchTag: SearchTag = SearchTag(vatC2cReference.canonicalizedValue)
}

final case class Pillar2SessionData(pillar2Reference: Pillar2Reference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(Pillar2) {
  def paymentReference: Reference = ReferenceMaker.makePillar2Reference(pillar2Reference)

  def searchTag: SearchTag = SearchTag(pillar2Reference.canonicalizedValue)
}

final case class PfPillar2SessionData(pillar2Reference: Pillar2Reference, returnUrl: Option[String] = None) extends OriginSpecificSessionData(PfPillar2) {
  def paymentReference: Reference = ReferenceMaker.makePillar2Reference(pillar2Reference)
  def searchTag: SearchTag = SearchTag(pillar2Reference.canonicalizedValue)
}
