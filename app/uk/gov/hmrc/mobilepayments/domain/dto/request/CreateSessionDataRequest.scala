/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.domain.dto.request

import play.api.libs.json.{Format, Json}
import play.api.libs.json._
import uk.gov.hmrc.domain.SaUtr

sealed abstract class OriginSpecificData(val origin: String)

final case class CreateSessionDataRequest(
                                           amount: BigDecimal,
                                           originSpecificData: OriginSpecificData
                                         )

object CreateSessionDataRequest {
  implicit val format: Format[CreateSessionDataRequest] = Json.format[CreateSessionDataRequest]
}

final case class SimpleAssessmentOriginSpecificData(p302Ref: String) extends OriginSpecificData("AppSimpleAssessment")

final case class SelfAssessmentOriginSpecificData(saUtr: SaUtr) extends OriginSpecificData("AppSa")

object OriginSpecificData {
  implicit val writes: Writes[OriginSpecificData] = (o: OriginSpecificData) =>
    (o match {
      case s: SimpleAssessmentOriginSpecificData => Json.format[SimpleAssessmentOriginSpecificData].writes(s)
      case s: SelfAssessmentOriginSpecificData => Json.format[SelfAssessmentOriginSpecificData].writes(s)
    }) + ("origin" -> Json.toJson(o.origin))

  implicit val reads: Reads[OriginSpecificData] = (json: JsValue) =>
    (__ \ "origin").read[String].reads(json).flatMap {
      case "AppSimpleAssessment" => Json.format[SimpleAssessmentOriginSpecificData].reads(json)
      case "AppSa" => Json.format[SelfAssessmentOriginSpecificData].reads(json)
    }

  implicit val format: Format[OriginSpecificData] = Format(reads, writes)
}
