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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.ClimateChangeLevyRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.ClimateChangeLevyRef.*

class ClimateChangeLevyRefSpec extends ReferenceSupport[ClimateChangeLevyRef] {

  val validClimateChangeLevyRefs =
    Seq(("1234 1234 12345", "1234123412345"), ("1234567891234", "1234567891234"), ("12 34 34 34 56 123", "1234343456123"))
  val inValidClimateChangeLevyRefs = Seq("12345678912345", "12345678912!4", "1234567891", "123456789123S", "")
  testRef2("ClimateChangeLevyRefSpec", validClimateChangeLevyRefs, inValidClimateChangeLevyRefs, createValid, ClimateChangeLevyRef.apply)
}
