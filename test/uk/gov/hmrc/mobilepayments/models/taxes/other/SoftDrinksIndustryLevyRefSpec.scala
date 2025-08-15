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

import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.SoftDrinksIndustryLevyRef
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.SoftDrinksIndustryLevyRef.*

class SoftDrinksIndustryLevyRefSpec extends ReferenceSupport[SoftDrinksIndustryLevyRef] {

  val validReferences = Seq(
    ("X12345678901234", "X12345678901234"),
    ("X123456789012AA", "X123456789012AA"),
    ("XR1234567890123", "XR1234567890123"),
    ("XR123456789012A", "XR123456789012A"),
    ("Xr123456789012A", "XR123456789012A"),
    ("XR123456789012A", "XR123456789012A"),
    ("X11234567890122", "X11234567890122"),
    ("Xabcdefghijklmn", "XABCDEFGHIJKLMN"),
    ("xbb2345  678  90122", "XBB234567890122")
  )

  val invalidReferences = Seq("XR12345678912", "X123456789123", "X123456789123456", "X12345678912A",
    "aXR1234567891", "XR1234 56789123", "XR123456A89123", "XB", "")

  testRef2("SoftDrinksIndustryLevyRef", validReferences, invalidReferences, createValidRef, SoftDrinksIndustryLevyRef.apply)

  val validPenaltyReferences = Seq(
    ("X1234567890123", "X1234567890123"),
    ("X123456789012A", "X123456789012A"),
    ("XR123456789012", "XR123456789012"),
    ("XR123456789012", "XR123456789012"),
    ("Xr123456789012", "XR123456789012"),
    ("XR123456789012", "XR123456789012"),
    ("X1123456789012", "X1123456789012"),
    ("Xabcdefghijklm", "XABCDEFGHIJKLM"),
    ("x\tR12345   6789012", "XR123456789012")
  )

  val invalidPenaltyReferences = Seq("X123456789123456", "aXR1234567891", "XR1234 56789123", "XB", "")

  testRef2("SoftDrinksIndustryLevyPenaltyRef", validPenaltyReferences, invalidPenaltyReferences, createValidPenaltyRef, SoftDrinksIndustryLevyRef.apply)

  val validBothReferences = Seq(
    ("X1234567890123", "X1234567890123"),
    ("X123456789012A", "X123456789012A"),
    ("XR123456789012", "XR123456789012"),
    ("xR123456789012", "XR123456789012"),
    ("Xr123456789012", "XR123456789012"),
    ("XR123456789012", "XR123456789012"),
    ("X1123456789012", "X1123456789012"),
    ("X11   23456789   012", "X1123456789012"),
    ("Xabcdefghijklm", "XABCDEFGHIJKLM"),
    ("X12345678901234", "X12345678901234"),
    ("X123456789012AA", "X123456789012AA"),
    ("XR1234567890123", "XR1234567890123"),
    ("XR123456789012A", "XR123456789012A"),
    ("Xr123456789012A", "XR123456789012A"),
    ("XR123456789012A", "XR123456789012A"),
    ("x11234567890122", "X11234567890122"),
    ("Xabcdefghijklmn", "XABCDEFGHIJKLMN"),
    ("xa\tbcd   efghi   jklmn", "XABCDEFGHIJKLMN")
  )

  val invalidBothReferences = Seq("XR1234 56789123", "XB", "")

  testRef2("SoftDrinksIndustryLevyBothRef", validBothReferences, invalidBothReferences, createValid, SoftDrinksIndustryLevyRef.apply)
}
