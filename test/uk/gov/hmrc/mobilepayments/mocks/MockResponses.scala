/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.mocks

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.mobilepayments.domain.BanksResponse

trait MockResponses {

  lazy val banksJson: JsValue = Json.parse(rawBanksJson)

  lazy val banksResponse: BanksResponse = Json.fromJson[BanksResponse](banksJson).get

  val rawBanksJson: String = s"""
                                |
                                |{
                                |  "data": [
                                |    {
                                |      "bank_id": "obie-mettle-production",
                                |      "name": "Mettle",
                                |      "friendly_name": "Mettle",
                                |      "is_sandbox": false,
                                |      "logo": "https://public.ecospend.com/images/banks/Mettle.svg",
                                |      "icon": "https://public.ecospend.com/images/banks/Mettle_icon.svg",
                                |      "standard": "obie",
                                |      "country_iso_code": "GB",
                                |      "group": "Mettle",
                                |      "order": 100000,
                                |      "service_status": true,
                                |      "refund_supported": true,
                                |      "abilities": {
                                |        "domestic_payment": true,
                                |        "domestic_scheduled_payment": true,
                                |        "domestic_standing_order": false,
                                |        "domestic_standing_order_installment": false,
                                |        "international_payment": false,
                                |        "international_scheduled_payment": false,
                                |        "international_standing_order": false
                                |      }
                                |    },
                                |    {
                                |      "bank_id": "obie-barclays-business-mobile-production",
                                |      "name": "Barclays Business Mobile",
                                |      "friendly_name": "Barclays Business Mobile",
                                |      "is_sandbox": false,
                                |      "logo": "https://public.ecospend.com/images/banks/Barclays.svg",
                                |      "icon": "https://public.ecospend.com/images/banks/Barclays_icon.svg",
                                |      "standard": "obie",
                                |      "country_iso_code": "GB",
                                |      "division": "Business Mobile",
                                |      "group": "Barclays",
                                |      "order": 0,
                                |      "service_status": true,
                                |      "refund_supported": true,
                                |      "abilities": {
                                |        "domestic_payment": true,
                                |        "domestic_scheduled_payment": true,
                                |        "domestic_standing_order": true,
                                |        "domestic_standing_order_installment": true,
                                |        "international_payment": false,
                                |        "international_scheduled_payment": false,
                                |        "international_standing_order": false
                                |      }
                                |    }
                                |  ],
                                |  "meta": {
                                |    "total_count": 68,
                                |    "total_pages": 1,
                                |    "current_page": 1
                                |  }
                                |}
                                |
          """.stripMargin
}
