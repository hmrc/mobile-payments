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
 * The reference for Class 2 National Insurance (Ni)
 */

final case class Class2NiReference(value: String) {
  val canonicalisedValue: String = value.trim.toUpperCase()
}

object Class2NiReference {
  implicit val format: Format[Class2NiReference] = implicitly[Format[String]].inmap(Class2NiReference(_), _.value)
  private val class2NiReferenceRegx = """\d{17}(x|X|\d)$"""

  def createValid(input: String): Either[String, Class2NiReference] = {
    val InputNoSpaces = input.replaceAll(" ", "").toUpperCase
    if (InputNoSpaces.matches(class2NiReferenceRegx)) Right(Class2NiReference(InputNoSpaces))
    else Left("Class2NiReference $input did not pass regx check")
  }
}
