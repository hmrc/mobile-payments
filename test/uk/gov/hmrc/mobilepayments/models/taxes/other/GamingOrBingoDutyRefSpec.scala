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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.GamingOrBingoDutyRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.GamingOrBingoDutyRef.*

class GamingOrBingoDutyRefSpec extends ReferenceSupport[GamingOrBingoDutyRef] {

  val validRefs = Seq("XA1234567890123", "X01234012345678")
  val invalidRefs = Seq("XA12 34567890123", "cdSc1234567891a", "12340000002", "X")
  testRef("GamingOrBingoDutyRefSpec", validRefs, invalidRefs, createValid, GamingOrBingoDutyRef.apply)
}
