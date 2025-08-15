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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.cds

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

final case class CdsRef(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object CdsRef {
  implicit val format: Format[CdsRef] = implicitly[Format[String]].inmap(CdsRef(_), _.value)
  //Format should be CDSI then 2 digits representing the last 2 of the year aka 66 for 1066 then 10 more digit or alphas
  val cdsRefRegex = "^CDSI[0-9a-zA-Z]{12}$"

  //Format should be XCDSU then 2 digits representing the last 2 of the year aka 66 for 1066 then 7 more digits
  val cdsC18Regex = "^XCDSU[0-9]{9}$"

  def createValid(input: String): Either[String, CdsRef] = {
    if (input.matches(cdsRefRegex) || input.matches(cdsC18Regex))
      Right(CdsRef(input))
    else Left(s"CdsRef $input did not pass regEx check")
  }
}
