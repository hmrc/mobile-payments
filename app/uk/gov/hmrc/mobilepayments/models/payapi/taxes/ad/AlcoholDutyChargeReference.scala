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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.ad

import play.api.libs.functional.syntax.toInvariantFunctorOps
import play.api.libs.json.Format

/**
 * 14 character ETMP charge reference starting with X.
 */

final case class AlcoholDutyChargeReference(value: String) {
  val canonicalizedValue: String = value.replaceAll("\\s", "").toUpperCase
}

object AlcoholDutyChargeReference {
  val chargeReferenceRegex = """^X[0-9a-zA-Z][0-9]{12}$"""

  def createValid(input: String): Either[String, AlcoholDutyChargeReference] = {
    val cleanInput = input.replaceAll("\\s", "").toUpperCase
    if (cleanInput.matches(chargeReferenceRegex)) Right(AlcoholDutyChargeReference(cleanInput))
    else Left(s"AlcoholDutyChargeRef $input did not pass regex check")
  }

  implicit val format: Format[AlcoholDutyChargeReference] = implicitly[Format[String]].inmap(AlcoholDutyChargeReference(_), _.value)
}
