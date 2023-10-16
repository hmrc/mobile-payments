/*
 * Copyright 2022 HM Revenue & Customs
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

import payapi.corcommon.model.{SearchOptions, TaxType}
import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.mobilepayments.domain.AmountInPence

import play.api.libs.json._

sealed abstract class OriginSpecificData(val origin: String)

final case class CreateSessionDataRequest(
                                           amount: AmountInPence,
                                           originSpecificData: OriginSpecificData
                                         )

object CreateSessionDataRequest {
  implicit val format: Format[CreateSessionDataRequest] = Json.format[CreateSessionDataRequest]
}

final case class SimpleAssessmentOriginSpecificData(
                                                     pa302ref: String,
                                                   ) extends OriginSpecificData("appSi")

final case class SelfAssessmentOriginSpecificData(
                                                   saUtr: String,
                                                 ) extends OriginSpecificData("appSa")

object OriginSpecificData {
  implicit val writes: Writes[OriginSpecificData] = (o: OriginSpecificData) =>
    (o match {
      case s: SimpleAssessmentOriginSpecificData => Json.format[SimpleAssessmentOriginSpecificData].writes(s)
      case s: SelfAssessmentOriginSpecificData => Json.format[SelfAssessmentOriginSpecificData].writes(s)
    }) + ("origin" -> Json.toJson(o.origin))
}
