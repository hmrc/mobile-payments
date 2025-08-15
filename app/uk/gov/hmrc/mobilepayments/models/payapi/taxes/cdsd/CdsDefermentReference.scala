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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.cdsd

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

final case class CdsDefermentReference(value: String) {
  val canonicalizedValue: String = value.replaceAll("\\s", "").toUpperCase
}

object CdsDefermentReference {

  val cdsDefermentRegex = """^CDSD[0-9]{7}$"""

  def createValid(input: String): Either[String, CdsDefermentReference] = {
    val cleanInput = input.replaceAll("\\s", "").toUpperCase
    if (cleanInput.matches(cdsDefermentRegex)) Right(CdsDefermentReference(cleanInput))
    else Left(s"CdsDefermentReference $cleanInput did not pass regex check")
  }

  implicit val format: Format[CdsDefermentReference] = implicitly[Format[String]].inmap(CdsDefermentReference(_), _.value)
}
