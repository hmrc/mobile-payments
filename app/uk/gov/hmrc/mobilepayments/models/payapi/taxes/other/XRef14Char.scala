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

final case class XRef14Char(value: String) {
  val canonicalizedValue: String = value.replaceAll(" ", "").toUpperCase()
}

object XRef14Char {
  val generalBettingXRefRegex = """^X[A-Z0-9]{13}$"""

  def createValid(input: String): Either[String, XRef14Char] = {
    val trimmedCondensed = input.replaceAll(" ", "").toUpperCase
    if (trimmedCondensed.matches(generalBettingXRefRegex))
      Right(XRef14Char(trimmedCondensed))
    else Left(s"GeneralBettingXRef $input did not pass regEx check")
  }

  implicit val format: Format[XRef14Char] = implicitly[Format[String]].inmap(XRef14Char(_), _.value)
}
