/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.controllers.payments

import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

trait PaymentController {

  def createPayment(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent]

  def updatePayment(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent]

  def urlConsumed(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent]

  def getPaymentStatus(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent]

  def latestPayments(
    utr:       String,
    journeyId: JourneyId
  ): Action[AnyContent]

  def getPayByCardURL(
    utr:       String,
    journeyId: JourneyId
  ): Action[JsValue]
}
