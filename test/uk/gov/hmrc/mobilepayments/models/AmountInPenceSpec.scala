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

package uk.gov.hmrc.mobilepayments.models

import org.scalatest.Assertion
import play.api.libs.json.*
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.models.payapi.AmountInPence

class AmountInPenceSpec extends BaseSpec {

  import AmountInPenceSpec.*

  "AmountInPence" should {

    "should show amounts in pounds correctly" in {
      AmountInPence(20099).formatInPounds shouldBe "£200.99"

      AmountInPence(200099).formatInPounds shouldBe "£2,000.99"
    }

    "should show amounts in decimal" in {
      AmountInPence(20099).formatInDecimal  shouldBe "200.99"
      AmountInPence(200099).formatInDecimal shouldBe "2,000.99"
    }

    "convert to pounds correctly" in {
      AmountInPence(20099).inPounds shouldBe BigDecimal("200.99")
    }

    "convert to pounds correctly formatted" in {
      AmountInPence(200099).inPoundsRoundedFormatted shouldBe "2,000.99"
      AmountInPence(10000).inPoundsRoundedFormatted  shouldBe "100"
    }

    "is greater then" in {
      AmountInPence(1) > AmountInPence(2) shouldBe false
      AmountInPence(2) > AmountInPence(2) shouldBe false
      AmountInPence(3) > AmountInPence(2) shouldBe true
    }

    "is greater then or equal to" in {
      AmountInPence(1) >= AmountInPence(2) shouldBe false
      AmountInPence(2) >= AmountInPence(2) shouldBe true
      AmountInPence(3) >= AmountInPence(2) shouldBe true
    }

    "addition" in {
      AmountInPence(1) + AmountInPence(0) shouldBe AmountInPence(1)
      AmountInPence(2) + AmountInPence(2) shouldBe AmountInPence(4)
    }

    "should have a reads instance" should {

      def test(json: String)(expectedResult: JsResult[Test]): Assertion = {
        Json.parse(json).validate[Test] shouldBe expectedResult
      }

      val path = JsPath \ "amountInPence"

      "which accepts positive integer values" in {
        test("""{ "amountInPence" : 1 }""")(JsSuccess(Test(AmountInPence(1L))))
      }

      // TODO: Restore this test when we can be sure of backwards compatibility with old payments that have AmountInPence(0)
      "which excludes negative integer values" ignore {
        test("""{ "amountInPence" : -1 }""")(JsError(path, "Expected positive integer but got negative integer"))
      }

      // TODO: Restore this test when we can be sure of backwards compatibility with old payments that have AmountInPence(0)
      "which excludes zero values" ignore {
        test("""{ "amountInPence" : 0 }""")(JsError(path, "Expected positive integer but got zero"))
        test("""{ "amountInPence" : 0.0 }""")(JsError(path, "Expected positive integer but got zero"))

      }

      "which excludes non-integer values" in {
        test("""{ "amountInPence" : 1.2 }""")(JsError(path, "Expected positive integer but got non-integral number"))
      }

      "which excludes values which are not numbers" in {
        test("""{ "amountInPence" : "abc" }""")(JsError(path, "Expected positive integer but got type JsString"))

      }

    }

  }

}

object AmountInPenceSpec {

  case class Test(amountInPence: AmountInPence)

  implicit val testReads: Reads[Test] = Json.reads[Test]

}
