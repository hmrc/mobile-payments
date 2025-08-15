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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.vatc2c

import play.api.libs.json.*

final case class VatC2cReference(value: String) {
  val canonicalizedValue: String = value.replaceAll("\\s", "").toUpperCase
}

object VatC2cReference {

  private val vatC2cRefRegx = """^XVC[a-zA-Z0-9]{12}$"""

  def createValid(input: String): Either[String, VatC2cReference] = {
    if (input.matches(vatC2cRefRegx)) Right(VatC2cReference(input))
    else Left(s"VatC2cRef $input did not pass regex check")
  }

  implicit val format: Format[VatC2cReference] = new Format[VatC2cReference] {
    override def reads(json: JsValue): JsResult[VatC2cReference] = json match {
      case JsString(s) =>
        createValid(s) match {
          case Right(ref) => JsSuccess(ref)
          case Left(err)  => JsError(err)
        }
      case _ => JsError("Expected JSON string")
    }

    override def writes(o: VatC2cReference): JsValue = JsString(o.value)
  }
}
