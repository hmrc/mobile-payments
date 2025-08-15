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

final case class BeerDutyRef(value: String) {
  val canonicalisedValue: String = value.trim.toUpperCase()
}

object BeerDutyRef {
  val beerDutyRegex = """^([0-9a-zA-Z]{8})$"""

  def createValid(input: String): Either[String, BeerDutyRef] = {
    val inputWithoutSpaces = input.replaceAll("\\s", "")
    if (inputWithoutSpaces.matches(beerDutyRegex))
      Right(BeerDutyRef(inputWithoutSpaces.toUpperCase))
    else
      Left(s"[BeerDutyRef: $input] did not pass regEx check")
  }

  implicit val format: Format[BeerDutyRef] = implicitly[Format[String]].inmap(BeerDutyRef(_), _.value)
}
