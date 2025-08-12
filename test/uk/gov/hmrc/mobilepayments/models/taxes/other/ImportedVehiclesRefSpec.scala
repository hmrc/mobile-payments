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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.ImportedVehiclesRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.ImportedVehiclesRef.*

class ImportedVehiclesRefSpec extends ReferenceSupport[ImportedVehiclesRef] {

  val validReferences = Seq(("NOVA19E123456", "NOVA19E123456"),
                            ("Nova19E123456", "NOVA19E123456"),
                            ("Nova19x123456", "NOVA19X123456"),
                            ("N  ova19x    123456", "NOVA19X123456")
                           )
  val invalidReferences = Seq("NOVA195123456", "NOVA19E1234567", "NOVAA19E123456", "NOVA193E123456", "NOVP19E123456", "Colm19E123456", "")

  testRef2("ImportedVehiclesRefSpec", validReferences, invalidReferences, createValid, ImportedVehiclesRef.apply)
}
