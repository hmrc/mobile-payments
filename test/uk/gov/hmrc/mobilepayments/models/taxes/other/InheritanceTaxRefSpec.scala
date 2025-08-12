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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.InheritanceTaxRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.InheritanceTaxRef.*

class InheritanceTaxRefSpec extends ReferenceSupport[InheritanceTaxRef] {

  val validReferences = Seq(
    ("F123456/19X", "F123456/19X"),
    ("f123456/19X", "F123456/19X"),
    ("L123456/19X", "L123456/19X"),
    ("a123456/19x", "A123456/19X"),
    ("F12345619X", "F12345619X"),
    ("L12345619X", "L12345619X"),
    ("A12345619X", "A12345619X"),
    ("A123456/19Q", "A123456/19Q"),
    ("f123    45619X", "F12345619X")
  )
  val invalidReferences = Seq("P123456/19X",
                              "a123456/1999X",
                              "L12345678/19X",
                              "L123456199Q",
                              "aXR1234567891",
                              "XR1234 56789123",
                              "L12345678219X",
                              "XR123456A89123",
                              "XB",
                              ""
                             )

  testRef2("InheritanceTaxRefSpec", validReferences, invalidReferences, createValid, InheritanceTaxRef.apply)
}
