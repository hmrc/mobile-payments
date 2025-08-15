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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.epaye

import play.api.libs.functional.syntax._
import play.api.libs.json.Format
import uk.gov.hmrc.referencechecker.EpayeReferenceChecker

final case class AccountsOfficeReference(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object AccountsOfficeReference {

  implicit val format: Format[AccountsOfficeReference] = implicitly[Format[String]].inmap(AccountsOfficeReference(_), _.value)

  def createValid(input: String): Either[String, AccountsOfficeReference] = {
    val inputWithoutSpacesAndUpperCase = input.replaceAll("\\s", "").toUpperCase()
    if (inputWithoutSpacesAndUpperCase.matches(EpayeReferenceChecker.refRegex) && EpayeReferenceChecker.isValid(inputWithoutSpacesAndUpperCase))
      Right(AccountsOfficeReference(inputWithoutSpacesAndUpperCase))
    else
      Left(s"AccountsOfficeReference: $input - did not pass regEx check")
  }

}

