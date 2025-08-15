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

/**
 * 14 or 15 character reference starting with an X.
 * (Includes PRN - Penalty Reference Number)
 * Also accepts JrsRef format
 */
final case class JrsRef(value: String) {
  def canonicalizedValue: String = value.trim.toUpperCase()
}

object JrsRef {

  val jrsRefRegex = """[xX][a-zA-Z][jJ][rR][sS]\d{9}"""
  val xRefRegex = """^X[A-Z](([0-9A-Z]{13})|([0-9A-Z]\d{11}))$"""

  def createValid(input: String): Either[String, JrsRef] = {
    if (input.matches(jrsRefRegex) || input.matches(xRefRegex)) {
      Right(JrsRef(input))
    } else Left(s"JrsRef $input did not pass regEx check")
  }
  implicit val format: Format[JrsRef] = implicitly[Format[String]].inmap(JrsRef(_), _.value)
}
