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

final case class Class3NiRef(value: String) {
  val canonicalisedValue: String = value.trim.toUpperCase()
}

object Class3NiRef {

  implicit val format: Format[Class3NiRef] = implicitly[Format[String]].inmap(Class3NiRef(_), _.value)

  private val class3NiReferenceRegx = """(60)\d{15}(x|X|\d)$"""

  def createValid(input: String): Either[String, Class3NiRef] = {
    val inputNoSpacesToUpperCase = input.replaceAll(" ", "").toUpperCase
    if (inputNoSpacesToUpperCase.matches(class3NiReferenceRegx)) Right(Class3NiRef(inputNoSpacesToUpperCase))
    else Left("Class3NiRef $input did not pass regx check")
  }

}
