/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.domain

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

case class Bank(
  id:   String,
  name: String,
  logo: String,
  icon: String)

object Bank {

  private val reads: Reads[Bank] = (
    (__ \ "bank_id").read[String].orElse(Reads(_ => JsError("Could not parse bank_id value"))) and
    (__ \ "name").read[String].orElse(Reads(_ => JsError("Could not parse name value"))) and
    (__ \ "logo").read[String].orElse(Reads(_ => JsError("Could not parse logo value"))) and
    (__ \ "icon").read[String].orElse(Reads(_ => JsError("Could not parse icon value")))
  )((id, name, logo, icon) => Bank(id, name, logo, icon))

  private val writes: OWrites[Bank] = (bank: Bank) =>
    Json.obj(
      "bank_id" -> bank.id,
      "name"    -> bank.name,
      "logo"    -> bank.logo,
      "icon"    -> bank.icon
    )

  implicit val format: OFormat[Bank] = OFormat(reads, writes)
}
