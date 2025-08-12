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

package uk.gov.hmrc.mobilepayments.models.taxes.vat

import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.XRef14Char
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.XRef14Char.*

class XRef14CharSpec extends BaseSpec {

  "expect createValid success for XRef14Char in registration number format" in {
    val refs = Seq("XA123456789012", "Xa123456789012", " XA123456 789012")

    refs.foreach { ref =>
      createValid(ref) shouldBe Right(XRef14Char(ref.replaceAll(" ", "").toUpperCase))
    }
  }

  "expect createValid failure for incorrect XRef14Char" in {
    val refs = Seq("XRPPT000123456789", "A1234567890123456789", "A1234534**", "", "XA12345678901234", "XA1234567890123")

    refs.foreach { ref =>
      createValid(ref) shouldNot be(Right(XRef14Char(ref)))
    }
  }
}
