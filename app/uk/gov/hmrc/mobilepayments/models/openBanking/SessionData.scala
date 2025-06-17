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


import payapi.corcommon.model.times.formats.MongoJavaTimeFormats
import payapi.corcommon.model.{AmountInPence, FutureDatedPayment, SearchOptions, TaxType, TaxTypes}
import play.api.Logger
import play.api.libs.json._

import java.time.LocalDateTime

final case class SessionData[+Osd <: OriginSpecificSessionData](
                                                                 _id:                SessionDataId,
                                                                 sessionId:          SessionId,
                                                                 amount:             AmountInPence,
                                                                 taxType:            TaxType,
                                                                 searchOptions:      SearchOptions,
                                                                 sessionState:       SessionState,
                                                                 createdOn:          LocalDateTime,
                                                                 originSpecificData: Osd,
                                                                 futureDatedPayment: Option[FutureDatedPayment]
                                                               )

object SessionData {

  //todo can we remove this now?OPS-9984
  // for backwards compatibility due to dates being stored as strings in mongo in the past
  implicit val localDateTimeReads: Reads[LocalDateTime] = stringOrDate => {
    MongoJavaTimeFormats.localDateTimeFormat.reads(stringOrDate) match {
      case s @ JsSuccess(_, _) => s
      case _@ JsError(_)       => handleStringDate(stringOrDate)
    }
  }

  implicit val localDateTimeWrites: Writes[LocalDateTime] = MongoJavaTimeFormats.localDateTimeFormat.writes(_)

  def handleStringDate(stringDate: JsValue): JsResult[LocalDateTime] = {
    Logger(this.getClass).warn("Failed trying to read mongo date format. Next trying to read string date format")
    Json.fromJson[LocalDateTime](stringDate)(play.api.libs.json.Reads.DefaultLocalDateTimeReads) match {
      case s @ JsSuccess(_, _) => s
      case f @ JsError(_) =>
        Logger(this.getClass)
          .warn("Failed to read a date from mongo date format and string format")
        f
    }
  }

  //todo: Can be set back to a generic OFormat reads and writes after the old doc cleanup is done.
  // This will remove the default for searchOptions and taxType which have not always been there keys on the mongo document
  implicit def reads[Osd <: OriginSpecificSessionData](implicit osdReads: Reads[Osd]): Reads[SessionData[Osd]] =
    (json: JsValue) => JsSuccess(SessionData[Osd](
      (json \ "_id").as[SessionDataId],
      (json \ "sessionId").as[SessionId],
      (json \ "amount").as[AmountInPence],
      (json \ "taxType").asOpt[TaxType].getOrElse(TaxTypes.other),
      (json \ "searchOptions").asOpt[SearchOptions].getOrElse(SearchOptions(None, None)),
      (json \ "sessionState").as[SessionState],
      (json \ "createdOn").as[LocalDateTime](localDateTimeReads),
      (json \ "originSpecificData").as[Osd](osdReads),
      (json \ "futureDatedPayment").asOpt[FutureDatedPayment]
    ))

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val writes: OWrites[SessionData[OriginSpecificSessionData]] = Json.format[SessionData[OriginSpecificSessionData]]
}
