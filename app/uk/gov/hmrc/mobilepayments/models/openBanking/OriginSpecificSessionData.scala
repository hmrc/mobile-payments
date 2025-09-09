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

import play.api.libs.json.{Json, *}
import uk.gov.hmrc.mobilepayments.domain.dto.response.Origin
import uk.gov.hmrc.mobilepayments.domain.dto.response.Origins.*
import uk.gov.hmrc.mobilepayments.models.payapi.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ReferenceMaker
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.p800.P800Ref
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sa.SaUtr

sealed abstract class OriginSpecificSessionData(val origin: Origin) {
  def paymentReference: Reference
  val returnUrl: Option[String]
  def searchTag: SearchTag
}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object OriginSpecificSessionData {
  implicit val reads: Reads[OriginSpecificSessionData] = (json: JsValue) =>
    (__ \ "origin").read[Origin].reads(json).flatMap {   
      case AppSa                    => Json.format[AppSaSessionData].reads(json)
      case AppSimpleAssessment      => Json.format[AppSimpleAssessmentSessionData].reads(json)
    }

  implicit val writes: OWrites[OriginSpecificSessionData] = (o: OriginSpecificSessionData) =>
    (o match {
      case sessionData: AppSaSessionData               => Json.format[AppSaSessionData].writes(sessionData)
      case sessionData: AppSimpleAssessmentSessionData => Json.format[AppSimpleAssessmentSessionData].writes(sessionData)
    }) + ("origin" -> Json.toJson(o.origin))

  implicit val format: OFormat[OriginSpecificSessionData] = OFormat(reads, writes)
}

sealed abstract class SelfAssessmentSessionData(origin: Origin) extends OriginSpecificSessionData(origin) {
  def saUtr: SaUtr
  def paymentReference: Reference = ReferenceMaker.makeSaReference(saUtr)
  val returnUrl: Option[String]
}

final case class AppSaSessionData(saUtr: SaUtr, override val returnUrl: Option[String] = None) extends SelfAssessmentSessionData(AppSa) {
  def searchTag = SearchTag(saUtr.value)
}

sealed abstract class PayeSessionData(origin: Origin) extends OriginSpecificSessionData(origin) {}

final case class AppSimpleAssessmentSessionData(p302Ref: P800Ref, override val returnUrl: Option[String] = None)
    extends OriginSpecificSessionData(AppSimpleAssessment) {
  def paymentReference: Reference = Reference(p302Ref.canonicalizedValue)
  def searchTag = SearchTag(p302Ref.canonicalizedValue)
}
