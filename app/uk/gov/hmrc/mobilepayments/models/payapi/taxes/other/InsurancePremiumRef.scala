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

final case class InsurancePremiumRef(value: String) {
  val canonicalisedValue = value.trim.toUpperCase()
}

object InsurancePremiumRef {

  private val insurancePremiumRefRegx = """^(x|X)([a-zA-Z]{1})(ip|IP|Ip|iP)([0-9]{11})$"""

  def createValid(input: String): Either[String, InsurancePremiumRef] = {
    val toUpperCaseNoSpaces = input.toUpperCase().replaceAll(" ", "")
    if (toUpperCaseNoSpaces.matches(insurancePremiumRefRegx)) Right(InsurancePremiumRef(toUpperCaseNoSpaces))
    else Left("InsurancePremiumRef $input did not pass regx check")
  }
  implicit val format: Format[InsurancePremiumRef] = implicitly[Format[String]].inmap(InsurancePremiumRef(_), _.value)

}
