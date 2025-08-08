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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.ppt

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

final case class PptReference(value: String) {
  val canonicalizedValue: String = value.replaceAll(" ", "").toUpperCase()
}

object PptReference {
  private val pptReferenceRegex: String = """^X[A-Z]PPT[0-9]{10}$"""

  def createValid(input: String): Either[String, PptReference] = {
    val trimmedInput = input.replaceAll(" ", "").toUpperCase
    if (trimmedInput.matches(pptReferenceRegex)) {
      Right(PptReference(trimmedInput))
    } else {
      Left(s"PptReference $input did not pass regex check")
    }
  }

  implicit val format: Format[PptReference] = implicitly[Format[String]].inmap(PptReference(_), _.value)
}
