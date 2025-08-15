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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.other

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

final case class InheritanceTaxRef(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object InheritanceTaxRef {
  implicit val format: Format[InheritanceTaxRef] = implicitly[Format[String]].inmap(InheritanceTaxRef(_), _.value)
  private val inheritanceTaxRefRegx = """^(F|f|L|l|A|a)([0-9]{6})([\/]?)([0-9]{2})([a-zA-z]{1})$"""

  def createValid(input: String): Either[String, InheritanceTaxRef] = {
    val inputWithoutSpaces = input.replaceAll("\\s", "")
    if (inputWithoutSpaces.matches(inheritanceTaxRefRegx)) Right(InheritanceTaxRef(inputWithoutSpaces.toUpperCase))
    else Left("InheritanceTaxRef $input did not pass regex check")
  }
}
