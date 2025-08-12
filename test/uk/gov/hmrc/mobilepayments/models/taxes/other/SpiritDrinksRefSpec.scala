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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sd.SpiritDrinksReference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sd.SpiritDrinksReference.*

class SpiritDrinksRefSpec extends ReferenceSupport[SpiritDrinksReference] {

  val validReferences = Seq("18001234567890")
  val invalidReferences = Seq("XR12345678912", "xaA123456789123", "XR1234 56789123", "XB", "", "1800", "180012345678901234")

  testRef("SpiritDrinksRefSpec", validReferences, invalidReferences, createValid, SpiritDrinksReference.apply)
}
