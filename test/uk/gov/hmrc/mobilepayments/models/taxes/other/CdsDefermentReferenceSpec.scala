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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.cdsd.CdsDefermentReference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.cdsd.CdsDefermentReference.*

class CdsDefermentReferenceSpec extends ReferenceSupport[CdsDefermentReference] {

  val validRefs = Seq("CDSD0000002", "cdsd0000002", "CDSD 0000002", "cDsD 1234567")
  val invalidRefs = Seq("12340000002", "CDSD", "0000002", "", "1800")
  testRef("CdsDefermentReferenceSpec",
          validRefs,
          invalidRefs,
          createValid,
          (str: String) => CdsDefermentReference.apply(createValidRefWithoutSpacesUppercase(str))
         )
}
