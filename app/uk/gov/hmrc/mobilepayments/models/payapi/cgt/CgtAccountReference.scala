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

package uk.gov.hmrc.mobilepayments.models.payapi.cgt

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

final case class CgtAccountReference(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object CgtAccountReference {

  val cgtRefRegEx = """^X[A-Z]CGTP[0-9]{9}$"""

  def createValid(input: String): Either[String, CgtAccountReference] = {
    if (input.matches(cgtRefRegEx)) Right(CgtAccountReference(input))
    else Left(s"CgtReference $input did not pass regEx check")
  }

  implicit val format: Format[CgtAccountReference] = implicitly[Format[String]].inmap(CgtAccountReference(_), _.value)

}
