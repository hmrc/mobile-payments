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
import uk.gov.hmrc.mobilepayments.domain.dto.response._
import uk.gov.hmrc.mobilepayments.domain.{Bank, BankGroupData, Shuttering}

import scala.io.Source

trait MobilePaymentsTestData {

  lazy val banksResponse: List[Bank] = Json.fromJson[List[Bank]](js("banks-response")).get

  lazy val banksResponseGrouped: List[BankGroupData] =
    Json.fromJson[List[BankGroupData]](js("banks-response-grouped")).get

  lazy val paymentStatusOpenBankingResponse: OpenBankingPaymentStatusResponse =
    Json.fromJson[OpenBankingPaymentStatusResponse](js("payment-status-ob-response")).get

  lazy val paymentStatusResponse: PaymentStatusResponse =
    Json.fromJson[PaymentStatusResponse](js("payment-status-response")).get

  lazy val shutteredResponse: Shuttering = Json.fromJson[Shuttering](js("shuttered-response")).get

  lazy val sessionDataResponse: CreateSessionDataResponse =
    Json.fromJson[CreateSessionDataResponse](js("session-data-response")).get

  lazy val paymentInitiatedResponse: InitiatePaymentResponse =
    Json.fromJson[InitiatePaymentResponse](js("payment-initiated-response")).get

  lazy val paymentInitiatedUpdateResponse: InitiatePaymentResponse =
    Json.fromJson[InitiatePaymentResponse](js("payment-initiated-update-response")).get

  lazy val paymentSessionResponse: InitiatePaymentResponse =
    Json.fromJson[InitiatePaymentResponse](js("payment-session-response")).get

  lazy val rawMalformedJson:                   String = "{\"data\": [{,]}"
  lazy val banksResponseJson:                  String = json("banks-response")
  lazy val sessionDataResponseJson:            String = json("session-data-response")
  lazy val createPaymentRequestJson:           String = json("create-payment-request")
  lazy val paymentInitiatedResponseJson:       String = json("payment-initiated-response")
  lazy val paymentInitiatedUpdateResponseJson: String = json("payment-initiated-update-response")
  lazy val paymentStatusResponseJson:          String = json("payment-status-ob-response")

  private def json(fileName: String): String = {
    val source = Source.fromFile(s"test-common/uk/gov/hmrc/mobilepayments/resources/test-$fileName.json")
    val raw    = source.getLines.mkString
    source.close()
    raw
  }

  private def js(fileName: String): JsValue =
    Json.parse(json(fileName))
}
