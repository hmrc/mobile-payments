/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.domain

import uk.gov.hmrc.mobilepayments.common.BaseSpec

class AmountInPenceSpec extends BaseSpec {

  "when object created with amount in pounds" should {
    "produce an amount in pence value" in {

      val sut1 = AmountInPence(120)
      sut1.value shouldBe 12000

      val sut2 = AmountInPence(0)
      sut2.value shouldBe 0

      val sut3 = AmountInPence(548.65)
      sut3.value shouldBe 54865
    }
  }
}
