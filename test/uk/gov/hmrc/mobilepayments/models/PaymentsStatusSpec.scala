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

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.models.payapi.{PaymentStatus, PaymentStatuses}

class PaymentsStatusSpec extends BaseSpec {
  "(de)serialize (prevents accidental refactoring) " in {
    val statuses: List[(String, PaymentStatus)] = List(
      "Created"     -> PaymentStatuses.Created,
      "Successful"  -> PaymentStatuses.Successful,
      "Sent"        -> PaymentStatuses.Sent,
      "Validated"   -> PaymentStatuses.Validated,
      "Failed"      -> PaymentStatuses.Failed,
      "Cancelled"   -> PaymentStatuses.Cancelled,
      "SoftDecline" -> PaymentStatuses.SoftDecline
    )
    statuses.foreach { tt =>
      val jsValue = Json.toJson(tt._2)
      withClue(s"serialize $tt") {
        jsValue shouldBe JsString(tt._1)
      }
      withClue(s"deserialize $tt") {
        jsValue.as[PaymentStatus] shouldBe tt._2
      }

    }
    withClue("sanity check that we've tested all statuses") {
      statuses.map(_._2).toSet shouldBe PaymentStatuses.values.toSet
    }
  }
  "final statuses" in {
    PaymentStatuses.terminalStatuses shouldBe List(PaymentStatuses.Successful, PaymentStatuses.Failed, PaymentStatuses.Cancelled)
  }
}
