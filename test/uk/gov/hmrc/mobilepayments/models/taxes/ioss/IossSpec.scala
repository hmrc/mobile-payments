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

package uk.gov.hmrc.mobilepayments.models.taxes.ioss

import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ioss.Ioss

class IossSpec extends BaseSpec {

  "expect isValid success for a ioss registration number in correct format" in {
    val refs = Seq("IM1234567890", "IM9876543210", "im9876543210", "Im9876543210", "iM9876543210")

    refs.foreach { ref =>
      Ioss.isValid(ref) shouldBe Right(Ioss(ref))
    }
  }

  "expect isValid failure for incorrect ioss registration number" in {
    val refs = Seq("This is not a valid ioss registration number", "AA1234567890", "1234567890", "IM1234567890IM", "IM12345A7890")

    refs.foreach { ref =>
      Ioss.isValid(ref) shouldNot be(Right(Ioss(ref)))
    }
  }
}
