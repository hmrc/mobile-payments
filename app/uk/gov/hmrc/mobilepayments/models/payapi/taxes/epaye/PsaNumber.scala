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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.epaye

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

/** Settlement Agreement (PSA) reference number This represents a reference for PAYE Settlement Agreement tax
  */
final case class PsaNumber(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object PsaNumber {

  implicit val format: Format[PsaNumber] = implicitly[Format[String]].inmap(PsaNumber(_), _.value)

  val psaRegex = """^X[A-Z](([0-9A-Z]{13})|([0-9A-Z]\d{11}))$"""

  def createValid(input: String): Either[String, PsaNumber] = {
    val inputWithoutSpacesAndUpperCase = input.replaceAll("\\s", "").toUpperCase()
    if (inputWithoutSpacesAndUpperCase.matches(psaRegex))
      Right(PsaNumber(inputWithoutSpacesAndUpperCase))
    else Left(s"PsaNumber $input did not pass regEx check")
  }

}
