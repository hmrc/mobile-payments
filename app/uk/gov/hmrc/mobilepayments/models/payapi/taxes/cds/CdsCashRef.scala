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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.cds

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

final case class CdsCashRef(value: String) {
  val canonicalizedValue: String = value.replaceAll("\\s", "").toUpperCase()
}

object CdsCashRef {
  implicit val format: Format[CdsCashRef] = implicitly[Format[String]].inmap(CdsCashRef(_), _.value)

  val cdsRefRegex = "^CDSC[0-9]{11}$"

  def createValid(input: String): Either[String, CdsCashRef] = {
    val cleanInput = input.replaceAll("\\s", "").toUpperCase()
    if (cleanInput.matches(cdsRefRegex)) Right(CdsCashRef(cleanInput))
    else Left(s"CdsCashRef $cleanInput did not pass regEx check")
  }
}
