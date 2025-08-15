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

package uk.gov.hmrc.mobilepayments.models.taxes.other

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.XRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.XRef.*

class XRefSpec extends ReferenceSupport[XRef] {

  val validXRef: Seq[(String, String)] = Seq(("XE123456789012", "XE123456789012"),
                                             ("XE1 23456 789 012", "XE123456789012"),
                                             ("xe123456789012", "XE123456789012"),
                                             ("xe1 23456 789 012", "XE123456789012")
                                            )
  val invalidXRef: Seq[String] = Seq("1234P567890", "ABCDE", "ABC123SD**", "abcd1234cdx", "123", "xref")

  testRef2("XRefSpec", validXRef, invalidXRef, createValid, XRef.apply)

}
