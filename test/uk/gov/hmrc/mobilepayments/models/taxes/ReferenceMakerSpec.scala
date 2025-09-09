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

package uk.gov.hmrc.mobilepayments.models.taxes

import org.scalatest.Assertions.withClue
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.models.payapi.Reference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ReferenceMaker.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.p800.P800Ref
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sa.SaUtr


class ReferenceMakerSpec extends BaseSpec {
  "make reference" in {
    withClue("add a K at the end if not present in the tax reference") {
      makeSaReference(SaUtr("1097172564")) shouldBe Reference("1097172564K")
    }
    withClue("don't add a K if already present") {
      makeSaReference(SaUtr("1097172564K")) shouldBe Reference("1097172564K")
    }
    withClue("uppercase and don't add a K if already present") {
      makeSaReference(SaUtr("1097172564k")) shouldBe Reference("1097172564K")
    }
  }
}
