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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.WineAndCiderTaxRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.WineAndCiderTaxRef.*

class WineAndBeerTaxRefSpec extends ReferenceSupport[WineAndCiderTaxRef] {

  val validReferences = Seq(("N12345678900", "N12345678900"), ("n12345678900", "N12345678900"))
  val invalidReferences =
    Seq("XR12345678912", "X123456789123", "X123456789123456", "X12345678912A", "aXR1234567891", "XR1234 56789123", "XR123456A89123", "XB", "")

  testRef2("WineAndBeerTaxRefSpec", validReferences, invalidReferences, createValid, WineAndCiderTaxRef.apply)
}
