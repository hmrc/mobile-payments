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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.AirPassReference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.AirPassReference.*

class AirPassReferenceSpec extends ReferenceSupport[AirPassReference] {

  val validReferences = Seq("XAAP00000123456", "XZAP00000564543", "X AAP 00000 123456", "xzap 00000564543")
  val invalidReferences = Seq("XR12345678912",
                              "X123456789123",
                              "X123456789123456",
                              "X12345678912A",
                              "aXR1234567891",
                              "XR1234 56789123",
                              "XR123456A89123",
                              "XB",
                              "1234567890123456"
                             )
  testRef("AirPassReferenceSpec",
          validReferences,
          invalidReferences,
          createValid,
          (str: String) => AirPassReference.apply(createValidRefWithoutSpacesUppercase(str))
         )
}
