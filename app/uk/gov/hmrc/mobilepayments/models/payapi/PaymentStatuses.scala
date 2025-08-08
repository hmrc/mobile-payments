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

package uk.gov.hmrc.mobilepayments.models.payapi

import enumeratum.EnumEntry
import play.api.libs.json.Format
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.EnumFormat

sealed abstract class PaymentStatus(val validNextStates: Seq[PaymentStatus] = Seq(), val isTerminalState: Boolean) extends EnumEntry {
  def canBeChangedTo(newStatus: PaymentStatus): Boolean = this == newStatus || validNextStates.contains(newStatus)
}

object PaymentStatus {
  implicit val format: Format[PaymentStatus] = EnumFormat(PaymentStatuses)
}

import enumeratum.*

object PaymentStatuses extends Enum[PaymentStatus] {

  case object Created    extends PaymentStatus(validNextStates = Seq(Sent), isTerminalState = false)
  case object Successful extends PaymentStatus(isTerminalState = true)

  case object Sent extends PaymentStatus(Seq[PaymentStatus](Successful, Failed, Cancelled), isTerminalState = false)

  case object Validated   extends PaymentStatus(isTerminalState = false)
  case object Failed      extends PaymentStatus(isTerminalState = true)
  case object Cancelled   extends PaymentStatus(isTerminalState = true)
  case object SoftDecline extends PaymentStatus(isTerminalState = false)

  override def values = findValues

  lazy val terminalStatuses: List[PaymentStatus] = findValues.filter(_.isTerminalState).toList
}
