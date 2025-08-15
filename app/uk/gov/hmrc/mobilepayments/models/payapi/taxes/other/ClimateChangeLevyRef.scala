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

final case class ClimateChangeLevyRef(value: String) {
  val canonicalisedValue: String = value.trim.toUpperCase()
}

object ClimateChangeLevyRef {
  private val climateChangeLevyRegx = """(\d ?){12}\d$"""

  def createValid(input: String): Either[String, ClimateChangeLevyRef] = {
    val inputWithoutSpaces = input.replaceAll("\\s", "")
    if (inputWithoutSpaces.matches(climateChangeLevyRegx)) Right(ClimateChangeLevyRef(inputWithoutSpaces.toUpperCase))
    else Left("ClimateChangeLevyRef $input did not pass regx check")
  }

  implicit val format: Format[ClimateChangeLevyRef] = implicitly[Format[String]].inmap(ClimateChangeLevyRef(_), _.value)
}
