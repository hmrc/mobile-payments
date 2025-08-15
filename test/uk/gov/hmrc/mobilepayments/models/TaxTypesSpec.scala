/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.models

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.dto.response.{TaxType, TaxTypes}
class TaxTypesSpec extends BaseSpec {

  "de/serialize TaxTypes" in {

    val taxTypes: Map[String, TaxType] = Map(
      "economicCrimeLevy"       -> TaxTypes.economicCrimeLevy,
      "selfAssessment"          -> TaxTypes.selfAssessment,
      "vat"                     -> TaxTypes.vat,
      "epaye"                   -> TaxTypes.epaye,
      "other"                   -> TaxTypes.other,
      "p800"                    -> TaxTypes.p800,
      "stampDuty"               -> TaxTypes.stampDuty,
      "cds"                     -> TaxTypes.cds,
      "pngr"                    -> TaxTypes.pngr,
      "corporationTax"          -> TaxTypes.corporationTax,
      "capitalGainsTax"         -> TaxTypes.capitalGainsTax,
      "epayeTpes"               -> TaxTypes.epayeTpes,
      "class2NationalInsurance" -> TaxTypes.class2NationalInsurance,
      "class3NationalInsurance" -> TaxTypes.class3NationalInsurance,
      "mib"                     -> TaxTypes.mib,
      "insurancePremium"        -> TaxTypes.insurancePremium,
      "bioFuelsAndRoadGas"      -> TaxTypes.bioFuelsAndRoadGas,
      "airPassengerDuty"        -> TaxTypes.airPassengerDuty,
      "parcels"                 -> TaxTypes.parcels,
      "beerDuty"                -> TaxTypes.beerDuty,
      "landfillTax"             -> TaxTypes.landfillTax,
      "aggregatesLevy"          -> TaxTypes.aggregatesLevy,
      "climateChangeLevy"       -> TaxTypes.climateChangeLevy,
      "jobRetentionScheme"      -> TaxTypes.jobRetentionScheme,
      "childBenefitRepayments"  -> TaxTypes.childBenefitRepayments,
      "niEuVatOss"              -> TaxTypes.niEuVatOss,
      "niEuVatIoss"             -> TaxTypes.niEuVatIoss,
      "plasticPackagingTax"     -> TaxTypes.plasticPackagingTax,
      "inheritanceTax"          -> TaxTypes.inheritanceTax,
      "wineAndCiderTax"         -> TaxTypes.wineAndCiderTax,
      "spiritDrinks"            -> TaxTypes.spiritDrinks,
      "importedVehicles"        -> TaxTypes.importedVehicles,
      "ated"                    -> TaxTypes.ated,
      "trust"                   -> TaxTypes.trust,
      "p302"                    -> TaxTypes.p302,
      "alcoholDuty"             -> TaxTypes.alcoholDuty,
      "vatConsumerToConsumer"   -> TaxTypes.vatConsumerToConsumer,
      "pillar2"                 -> TaxTypes.pillar2
    )

    TaxTypes.values.toList.foreach(tt => taxTypes.values should contain(tt))

    taxTypes.foreach { tt =>
      val jsValue = Json.toJson(tt._2)
      withClue(s"serialize $tt") {
        jsValue shouldBe JsString(tt._1)
      }
      withClue(s"deserialize $tt") {
        jsValue.as[TaxType] shouldBe tt._2
      }
    }
  }
}
