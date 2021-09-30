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

package uk.gov.hmrc.mobilepayments

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.dto.{BanksResponse, SessionDataResponse}

import scala.io.Source

trait MobilePaymentsTestData {

  lazy val banksResponse:       BanksResponse       = Json.fromJson[BanksResponse](json("test-banks")).get
  lazy val shutteredResponse:   Shuttering          = Json.fromJson[Shuttering](json("test-shuttered")).get
  lazy val sessionDataResponse: SessionDataResponse = Json.fromJson[SessionDataResponse](json("test-session-data")).get

  val rawMalformedJson: String = "{\"data\": [{,]}"

  private def json(fileName: String): JsValue = {
    val source = Source.fromFile(s"test-common/uk/gov/hmrc/mobilepayments/resources/$fileName.json")
    val raw    = source.getLines.mkString
    source.close()
    Json.parse(raw)
  }

}
