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

package uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext

import enumeratum.{Enum, EnumEntry}
import play.api.libs.json._

object EnumFormat {

  @SuppressWarnings(Array("org.wartremover.warts.JavaSerializable"))
  def apply[T <: EnumEntry](e: Enum[T]): Format[T] = Format(
    Reads {
      case JsString(value) => e.withNameOption(value).map(JsSuccess(_))
        .getOrElse(JsError(JsonValidationError(
          s"Unknown ${e.getClass.getSimpleName} value: $value",
          s"error.invalid.${e.getClass.getSimpleName.toLowerCase.replace("$", "")}"
        )))
      case _ => JsError("Can only parse String")
    },
    Writes(v => JsString(v.entryName))
  )
  def oFormat[T](format: Format[T]): OFormat[T] = {
    val oFormat: OFormat[T] = new OFormat[T]() {
      override def writes(o: T): JsObject = format.writes(o).as[JsObject]
      override def reads(json: JsValue): JsResult[T] = format.reads(json)
    }
    oFormat
  }
}
