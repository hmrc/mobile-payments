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
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.Class3NiRef

class Class3NiReferenceSpec extends BaseSpec {

  "expect createValid success for correct Class3NiReference" in {
    val validClass3NiReferences =
      Seq("603490017829614130", "603330017829614133", "6033 30017829614133       ", "60349001782961413x", "60349001782961413X")

    validClass3NiReferences.foreach { validClass3NiReference =>
      Class3NiRef.createValid(validClass3NiReference) shouldBe Right(Class3NiRef(validClass3NiReference.replaceAll(" ", "").toUpperCase))
    }

  }

  "expect createValid failure for incorrect Class3NiReference" in {
    val inValidClass3NiReferences = Seq("60349001782961413", "123330017829614133", "11 3330017829614133!4", "1133300178296141", "")

    inValidClass3NiReferences.foreach { validClass3NiReference =>
      Class3NiRef.createValid(validClass3NiReference) shouldNot be(Right(Class3NiRef(validClass3NiReference)))
    }

  }

}
