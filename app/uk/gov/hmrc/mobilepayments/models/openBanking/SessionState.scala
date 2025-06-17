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

package uk.gov.hmrc.mobilepayments.models.openBanking


import akka.http.scaladsl.model.Uri
import play.api.libs.json.{Format, JsResult, JsString, JsSuccess, JsValue, Json, OFormat}
import uk.gov.hmrc.mobilepayments.models.openBanking.ecospend.EcospendFinishedStatuses.Verified
import uk.gov.hmrc.mobilepayments.models.openBanking.ecospend.{EcospendFinalStatus, EcospendFinishedStatus, EcospendNonFinalStatus, EcospendPaymentId, EcospendPaymentStatus}

import java.time.{LocalDate, LocalTime}

sealed trait SessionState {
  def email: Option[Email]
  def toString: String
}

object SessionState {
  def bankId(state: SessionState): Option[BankId] = state match {
    case SessionInitiated           => None
    case BankSelected(bankId, _)    => Some(bankId)
    case state: PaymentSessionState => Some(state.bankId)
  }

  def paymentId(state: SessionState): Option[EcospendPaymentId] = state match {
    case SessionInitiated           => None
    case BankSelected(_, _)         => None
    case state: PaymentSessionState => Some(state.paymentId)
  }

  def status(state: SessionState): Option[EcospendPaymentStatus] = state match {
    case SessionInitiated           => None
    case BankSelected(_, _)         => None
    case state: PaymentSessionState => Some(state.status)
  }

  implicit val format: Format[SessionState] = new Format[SessionState] {
    def writes(o: SessionState): JsValue = o match {
      case SessionInitiated                   => JsString("SessionInitiated")
      case bankSelected: BankSelected         => BankSelected.format.writes(bankSelected)
      case paymentInitiated: PaymentInitiated => PaymentInitiated.format.writes(paymentInitiated)
      case paymentFinished: PaymentFinished   => PaymentFinished.format.writes(paymentFinished)
      case paymentFinalised: PaymentFinalised => PaymentFinalised.format.writes(paymentFinalised)
    }

    @SuppressWarnings(Array("org.wartremover.warts.JavaSerializable"))
    def reads(json: JsValue): JsResult[SessionState] = json match {
      case JsString("SessionInitiated") => JsSuccess(SessionInitiated)
      case _ =>
        PaymentFinalised.format.reads(json)
          .orElse(PaymentInitiated.format.reads(json))
          .orElse(PaymentFinished.format.reads(json))
          .orElse(BankSelected.format.reads(json))
    }

  }
}

case object SessionInitiated extends SessionState {
  def email: Option[Email] = None

  override def toString: String = s"[SessionState: SessionInitiated]"
}

final case class BankSelected(bankId: BankId, email: Option[Email]) extends SessionState {
  override def toString: String = s"[SessionState: BankSelected] [bankId: ${bankId.toString}]"
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object BankSelected {
  def apply(bankId: BankId): BankSelected = BankSelected(bankId, None)

  implicit val format: OFormat[BankSelected] = Json.format[BankSelected]
}

sealed trait PaymentSessionState extends SessionState {
  def paymentId: EcospendPaymentId
  def paymentUrl: Uri
  def status: EcospendPaymentStatus
  def bankId: BankId
  def bankUrlHit: Option[Boolean]
  def bankLoginOption: Option[BankLoginOptions]
  def emailSent: Option[Boolean]

  override def toString: String = s"[PaymentSessionState: ${this.getClass.getSimpleName}] [bankId: ${bankId.toString}] [paymentId: ${paymentId.toString}] [status: ${status.toString} [bankUrlHit: ${bankUrlHit.toString} [bankLoginOption: ${bankLoginOption.toString}] [emailSent: ${emailSent.toString}]"
}

final case class PaymentInitiated(
                                   bankId:          BankId,
                                   email:           Option[Email],
                                   paymentId:       EcospendPaymentId,
                                   paymentUrl:      Uri,
                                   status:          EcospendNonFinalStatus,
                                   bankUrlHit:      Option[Boolean],
                                   bankLoginOption: Option[BankLoginOptions],
                                   emailSent:       Option[Boolean]
                                 ) extends PaymentSessionState

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object PaymentInitiated {
  implicit val format: OFormat[PaymentInitiated] = Json.format[PaymentInitiated]
}

sealed trait PaymentFinishedSessionState extends PaymentSessionState

final case class PaymentFinished(
                                  bankId:          BankId,
                                  email:           Option[Email],
                                  paymentId:       EcospendPaymentId,
                                  paymentUrl:      Uri,
                                  dateFinalised:   LocalDate,
                                  timeFinalised:   Option[LocalTime], //Optional because this is a new field. can be made mandatory after 60 days (mongo TTL)
                                  bankUrlHit:      Option[Boolean],
                                  bankLoginOption: Option[BankLoginOptions],
                                  emailSent:       Option[Boolean]
                                ) extends PaymentFinishedSessionState {
  def status: EcospendFinishedStatus = Verified

  override def toString: String = super.toString + s" [dateFinalised: ${dateFinalised.toString}] [timeFinalised: ${timeFinalised.toString}]"
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object PaymentFinished {
  implicit val format: OFormat[PaymentFinished] = Json.format[PaymentFinished]
}

final case class PaymentFinalised(
                                   bankId:          BankId,
                                   email:           Option[Email],
                                   paymentId:       EcospendPaymentId,
                                   paymentUrl:      Uri,
                                   status:          EcospendFinalStatus,
                                   dateFinalised:   LocalDate,
                                   timeFinalised:   Option[LocalTime], //Optional because this is a new field. can be made mandatory after 60 days (mongo TTL)
                                   bankUrlHit:      Option[Boolean],
                                   bankLoginOption: Option[BankLoginOptions],
                                   emailSent:       Option[Boolean]
                                 ) extends PaymentFinishedSessionState {
  override def toString: String = super.toString + s" [dateFinalised: ${dateFinalised.toString}] [timeFinalised: ${timeFinalised.toString}]"
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object PaymentFinalised {
  implicit val format: OFormat[PaymentFinalised] = Json.format[PaymentFinalised]
}
