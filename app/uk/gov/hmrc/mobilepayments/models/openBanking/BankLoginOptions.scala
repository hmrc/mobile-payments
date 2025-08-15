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

import play.api.libs.functional.syntax.toInvariantFunctorOps
import play.api.libs.json.Format

sealed trait BankLoginOptions {
  def value: String
}
case object QRCodeOption extends BankLoginOptions {
  val value: String = "qrcode"
}
case object ComputerOption extends BankLoginOptions {
  val value: String = "computer"
}

object BankLoginOptions {
  def apply(text: String): Option[BankLoginOptions] = {
    if (text.equals(ComputerOption.value)) Some(ComputerOption)
    else if (text.equals(QRCodeOption.value)) Some(QRCodeOption)
    else None
  }

  def toBankLoginOptions(text: String): BankLoginOptions = {
    if (text.equals(ComputerOption.value)) ComputerOption
    else if (text.equals(QRCodeOption.value)) QRCodeOption
    else throw new RuntimeException("value doesn't match BankLoginOptions[qrcode,computer]")
  }

  implicit val format: Format[BankLoginOptions] = implicitly[Format[String]].inmap(BankLoginOptions.toBankLoginOptions, _.value)

}

