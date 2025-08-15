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

package uk.gov.hmrc.mobilepayments.models.taxes.epaye

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.epaye.AccountsOfficeReference
import uk.gov.hmrc.mobilepayments.models.taxes.other.ReferenceSupport

class AccountsOfficeReferenceSpec extends ReferenceSupport[AccountsOfficeReference] {

  val validAccountsOfficeReference: Seq[(String, String)] =
    Seq(("123PH45678900", "123PH45678900"), ("123PH 45678 900", "123PH45678900"), ("123ph45678900", "123PH45678900"))
  val invalidAccountsOfficeReference: Seq[String] = Seq("1234P567890", "ABCDE", "ABC123SD**", "abcd1234cdx", "123", "accountsoffice")

  testRef2(
    "AccountsOfficeReferenceSpec",
    validAccountsOfficeReference,
    invalidAccountsOfficeReference,
    AccountsOfficeReference.createValid,
    AccountsOfficeReference.apply
  )

}
