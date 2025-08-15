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
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.Class2NiReference

class Class2NiReferenceSpec extends BaseSpec {

  "expect createValid success for correct Class2NiReference" in {

    val validClass2NiReferences = Seq("1234 12341234512345", "12345678912341234x", "12 34 34 34 56 123 12345", "12345678912341234X", "12345678912341234x")

    validClass2NiReferences.foreach{
      validClass2NiReference =>
        Class2NiReference.createValid(validClass2NiReference) shouldBe Right(Class2NiReference(validClass2NiReference.replaceAll(" ", "").toUpperCase))
    }

  }

  "expect createValid failure for incorrect Class2NiReference" in {
    val inValidClass2NiReferences = Seq("12345678912345", "12345678912!4", "1234567891", "123456789123S123qt", "")

    inValidClass2NiReferences.foreach{
      validClass2NiReference =>
        Class2NiReference.createValid(validClass2NiReference) shouldNot be(Right(Class2NiReference(validClass2NiReference)))
    }

  }

}
