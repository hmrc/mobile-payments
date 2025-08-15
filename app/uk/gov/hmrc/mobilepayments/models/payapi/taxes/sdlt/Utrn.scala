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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.sdlt

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format
import uk.gov.hmrc.referencechecker.SdltReferenceChecker

/**
 * Unique Transaction Reference Number (Utrn)
 * It's made up of 11 characters, for example 123456789MC. You'll find it on your electronic SDLT5 certificate or on your paper SDLT return.
 */
final case class Utrn(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object Utrn {

  implicit val format: Format[Utrn] = implicitly[Format[String]].inmap(Utrn(_), _.value)

  val utrnRegex = """^\d{9}M[A-Z]$"""

  def createValid(input: String): Either[String, Utrn] = {
    if (input.matches(utrnRegex))
      if (SdltReferenceChecker.isValid(input)) Right(Utrn(input))
      else Left(s"UTRN $input did not pass modulus check")
    else Left(s"UTRN $input did not pass regEx check")
  }

}
