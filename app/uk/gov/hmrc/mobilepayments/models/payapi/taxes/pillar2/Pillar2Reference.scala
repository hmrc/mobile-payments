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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.pillar2

import play.api.libs.json.*

case class Pillar2Reference(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase
}

object Pillar2Reference {
  // implicit val format: Format[Pillar2Reference] = Json.valueFormat[Pillar2Reference]

  private val pillar2ReferenceRegex: String = """^XMPLR\d{10}$"""

  def createValid(input: String): Either[String, Pillar2Reference] = {
    if (input.matches(pillar2ReferenceRegex)) Right(Pillar2Reference(input))
    else Left(s"Pillar2Reference $input did not pass regex check")
  }

  implicit val format: Format[Pillar2Reference] = new Format[Pillar2Reference] {
    override def reads(json: JsValue): JsResult[Pillar2Reference] = json match {
      case JsString(s) =>
        createValid(s) match {
          case Right(ref) => JsSuccess(ref)
          case Left(err)  => JsError(err)
        }
      case _ => JsError("Expected JSON string")
    }

    override def writes(o: Pillar2Reference): JsValue = JsString(o.value)
  }
}
