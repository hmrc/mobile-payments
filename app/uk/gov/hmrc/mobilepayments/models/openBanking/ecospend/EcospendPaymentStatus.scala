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

package uk.gov.hmrc.mobilepayments.models.openBanking.ecospend

import enumeratum.{Enum, EnumEntry}
import play.api.libs.json.Format
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.EnumFormat

sealed trait EcospendPaymentStatus extends EnumEntry {
  def isFinished: Boolean = this match {
    case _: EcospendNonFinalStatus => false
    case _: EcospendFinishedStatus => true
  }
}

object EcospendPaymentStatus {
  implicit val format: Format[EcospendPaymentStatus] = EnumFormat(EcospendPaymentStatuses)
}

object EcospendPaymentStatuses extends Enum[EcospendPaymentStatus] {
  override def values: IndexedSeq[EcospendPaymentStatus] =
    EcospendNonFinalStatuses.values ++ EcospendFinishedStatuses.values ++ EcospendFinalStatuses.values
}

sealed trait EcospendNonFinalStatus extends EcospendPaymentStatus

object EcospendNonFinalStatus {
  implicit val format: Format[EcospendNonFinalStatus] = EnumFormat(EcospendNonFinalStatuses)
}

object EcospendNonFinalStatuses extends Enum[EcospendNonFinalStatus] {
  case object Initial extends EcospendNonFinalStatus
  case object AwaitingAuthorization extends EcospendNonFinalStatus
  case object Authorised extends EcospendNonFinalStatus

  override def values: IndexedSeq[EcospendNonFinalStatus] = findValues
}

/**
 * These statuses are statuses that the user can finish the journey in, but they may not be terminal
 */
sealed trait EcospendFinishedStatus extends EcospendPaymentStatus

object EcospendFinishedStatus {
  implicit val format: Format[EcospendFinishedStatus] = EnumFormat(EcospendFinishedStatuses)
}

object EcospendFinishedStatuses extends Enum[EcospendFinishedStatus] {
  case object Verified extends EcospendFinishedStatus

  override def values: IndexedSeq[EcospendFinishedStatus] = findValues
}

/**
 * These statuses are Final and the journey cannot be changes once it is in one
 */
sealed trait EcospendFinalStatus extends EcospendFinishedStatus

object EcospendFinalStatus {
  implicit val format: Format[EcospendFinalStatus] = EnumFormat(EcospendFinalStatuses)
}

object EcospendFinalStatuses extends Enum[EcospendFinalStatus] {
  case object Completed extends EcospendFinalStatus
  case object Canceled extends EcospendFinalStatus
  case object Failed extends EcospendFinalStatus
  case object Rejected extends EcospendFinalStatus
  case object Abandoned extends EcospendFinalStatus

  override def values: IndexedSeq[EcospendFinalStatus] = findValues
}

