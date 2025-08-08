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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.other

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

/**
 * User reference for "Pay duty on biofuels or gas for road use"
 * see: https://www.gov.uk/guidance/pay-duty-on-biofuels-or-gas-for-road-use
 */
final case class BioFuelsRegistrationNumber(value: String)

object BioFuelsRegistrationNumber {
  val bioFuelsRegNumberRegex = """^XM(GR|BF)\d{11}$"""

  def createValid(input: String): Either[String, BioFuelsRegistrationNumber] =
    if (input.matches(bioFuelsRegNumberRegex)) Right(BioFuelsRegistrationNumber(input))
    else Left(s"BioFuelsRegistrationNumber $input did not pass reeEx check")

  implicit val format: Format[BioFuelsRegistrationNumber] = implicitly[Format[String]].inmap(BioFuelsRegistrationNumber(_), _.value)
}
