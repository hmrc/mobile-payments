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

package uk.gov.hmrc.mobilepayments.models.taxes.nino

import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.mobilepayments.common.BaseSpec

class NinoSpec extends BaseSpec {

  "expect isValid success for a correct Nino in correct format" in {
    val refs = Seq("AA111111A", "BB222222B")

    refs.foreach { ref =>
      Nino.isValid(ref) shouldBe true
    }
  }

  "expect isValid failure for incorrect Nino" in {
    val refs = Seq("This is not a valid nino", "AAA11111A", "AA123456789A", "AA123456AA", "")

    refs.foreach { ref =>
      Nino.isValid(ref) shouldBe false
    }
  }

}
