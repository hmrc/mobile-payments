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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.ad

import play.api.libs.functional.syntax.*
import play.api.libs.json.{Format, Json}

/** The Alcohol Products Producer Approval ID
  */
final case class AlcoholDutyReference(value: String) {
  val canonicalizedValue: String = value.replaceAll("\\s", "").toUpperCase()
}

object AlcoholDutyReference {
  implicit val format: Format[AlcoholDutyReference] = implicitly[Format[String]].inmap(AlcoholDutyReference(_), _.value)
  private val alcoholDutyRefRegx = """^X[A-Z]ADP[0-9]{10}$"""

  def createValid(input: String): Either[String, AlcoholDutyReference] = {
    if (input.matches(alcoholDutyRefRegx)) Right(AlcoholDutyReference(input))
    else Left(s"AlcoholDutyRef $input did not pass regex check")
  }
}
