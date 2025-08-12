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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.LandfillTaxRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.LandfillTaxRef.*

class LandfillRegistrationRefSpec extends ReferenceSupport[LandfillTaxRef] {

  val validLandfillRegistrationRef = Seq("XMGR12345678901", "XMBF12345678901")
  val inValidLandfillRegistrationRef = Seq("XM1238901", "XMCF12348901", "XMBF1234567", "XB", "")

  testRef("LandfillRegistrationRefSpec", validLandfillRegistrationRef, inValidLandfillRegistrationRef, createValid, LandfillTaxRef.apply)
}
