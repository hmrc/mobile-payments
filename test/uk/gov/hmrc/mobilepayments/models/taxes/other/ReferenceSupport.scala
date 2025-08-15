/*
 * Copyright 2025 HM Revenue & Customs
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

class ReferenceSupport[T] extends BaseSpec {

  private def testAgainstValid(refs: Seq[String], createf: String => Either[String, T], testf: String => T) = {
    refs.foreach { ref =>
      createf(ref) shouldBe Right(testf(ref))
    }
  }

  private def testAgainstInvalid(refs: Seq[String], createf: String => Either[String, T], testf: String => T) = {
    refs.foreach { ref =>
      createf(ref) shouldNot be(Right(testf(ref)))
    }
  }

  private def testAgainstValidXRef(createf: String => Either[String, T], testf: String => T) = {
    val validXReferences = Seq(
      "X12345678901234",
      "X123456789012AA",
      "XR1234567890123",
      "XR123456789012A",
      "xr123456789012A",
      "xR123456789012A",
      "xR123456789012a",
      "x11234567890122",
      "xabcdefghijklmn"
    )
    testAgainstValid(validXReferences, createf, testf)
  }

  private def testAgainstInvalidXRef(createf: String => Either[String, T], testf: String => T) = {
    val invalidXReferences =
      Seq("XR12345678912", "X123456789123", "X123456789123456", "X12345678912A", "aXR1234567891", "XR1234 56789123", "XR123456A89123", "XB", "")

    testAgainstInvalid(invalidXReferences, createf, testf)
  }

  def testXRef(typeName: String, createf: String => Either[String, T], testf: String => T) = {
    s"expect createValid success for $typeName" in {
      testAgainstValidXRef(createf, testf)
    }

    s"expect createValid failure for incorrect $typeName" in {
      testAgainstInvalidXRef(createf, testf)
    }
  }

  def testEconomicCrimeLevyReturnNumber(createf: String => Either[String, T], testf: String => T): Unit = {
    "expect createValid success for EconomicCrimeLevyReturnNumber" in {
      val validReturnNumbers = Seq(
        "XE123456789012",
        "XA123456789012",
        "XR 12345 67890 12",
        "xr123456789012",
        "xR123456789012",
        "xR123456789012",
        "x123456 7890123",
        "xabcdefghijklm",
        "X1234 567890123"
      )
      testAgainstValid(validReturnNumbers, createf, testf)
    }

    "expect createValid failure for EconomicCrimeLevyReturnNumber" in {
      val invalidReturnNumbers = Seq("XR12345678912",
                                     "X123456789123",
                                     "X123456789123456",
                                     "X12345678912A",
                                     "aXR1234567891",
                                     "XR12341 56789123",
                                     "XR123456A89123456789",
                                     "XB",
                                     ""
                                    )
      testAgainstInvalid(invalidReturnNumbers, createf, testf)
    }
  }

  val createValidRefWithoutSpacesUppercase: String => String =
    (str: String) => str.replaceAll("\\s", "").toUpperCase()

  def testRef(typeName: String, validRefs: Seq[String], invalidRefs: Seq[String], createf: String => Either[String, T], testf: String => T) = {
    s"expect createValid success for $typeName" in {
      testAgainstValid(validRefs, createf, testf)
    }

    s"expect createValid failure for incorrect $typeName" in {
      testAgainstInvalid(invalidRefs, createf, testf)
    }
  }

  def testAgainstValid2(refs: Seq[Tuple2[String, String]], createf: String => Either[String, T], testf: String => T) = {
    refs.foreach { ref =>
      createf(ref._1) shouldBe Right(testf(ref._2))
    }
  }

  def testRef2(typeName: String,
               validRefs: Seq[Tuple2[String, String]],
               invalidRefs: Seq[String],
               createf: String => Either[String, T],
               testf: String => T
              ) = {
    s"expect createValid success for $typeName" in {
      testAgainstValid2(validRefs, createf, testf)
    }

    s"expect createValid failure for incorrect $typeName" in {
      testAgainstInvalid(invalidRefs, createf, testf)
    }
  }

}
