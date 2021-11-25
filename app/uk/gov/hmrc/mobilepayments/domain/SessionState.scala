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

package uk.gov.hmrc.mobilepayments.domain

import play.api.libs.json._

import java.time.LocalDate

trait SessionState

//object SessionState {
//  implicit val format: Format[SessionState] = Json.format[SessionState]

//  def writes(state: SessionState):Writes
//
//  def reads(json: JsValue): JsResult[SessionState] = json match {
//    case JsString("SessionInitiated") => SessionInitiatedState.format.reads(Json.parse("status: SessionInitiated"))
//    case _ => SessionPaymentState.format.reads(json)
//  }
//}

final case class SessionPaymentState(
  bankId:        Option[String],
  paymentId:     Option[String],
  paymentUrl:    Option[String],
  status:        Option[String],
  dateFinalised: Option[LocalDate])
    extends SessionState

object SessionPaymentState {
  implicit val format: Format[SessionPaymentState] = Json.format[SessionPaymentState]
}

final case class SessionInitiatedState(status: String) extends SessionState

object SessionInitiatedState {
  implicit val format: Format[SessionInitiatedState] = Json.format[SessionInitiatedState]
}
