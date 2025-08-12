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

import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.InsurancePremiumRef

class InsurancePremiumRefSpec extends BaseSpec {

  "expect createValid success for correct InsurancePremiumRef" in {

    val validInsurancePremiumRefs =
      Seq("XMIP12341234512", "xMIP12341234512", "xMIP 1234123 451 2", "xeip92736251723", "XfiP73847263540", "xeip92736251723", "XfiP73847263540")

    validInsurancePremiumRefs.foreach { validInsurancePremiumRef =>
      InsurancePremiumRef.createValid(validInsurancePremiumRef) shouldBe Right(
        InsurancePremiumRef(validInsurancePremiumRef.replaceAll(" ", "").toUpperCase)
      )
    }

  }

  "expect createValid failure for incorrect InsurancePremiumRef" in {
    val inValidInsurancePremiumRefs = Seq("123456789123456", "12345678912!4", "1234567891", "123456789123S", "")

    inValidInsurancePremiumRefs.foreach { inValidInsurancePremiumRef =>
      InsurancePremiumRef.createValid(inValidInsurancePremiumRef) shouldNot be(Right(InsurancePremiumRef(inValidInsurancePremiumRef)))
    }
  }
}
