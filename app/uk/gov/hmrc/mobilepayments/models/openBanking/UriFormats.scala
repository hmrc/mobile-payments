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

package uk.gov.hmrc.mobilepayments.models.openBanking

import akka.http.scaladsl.model.Uri
import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.*

import scala.util.{Failure, Success, Try}

object UriFormats {

  private def parseUrl(str: String): JsResult[Uri] = Try(Uri(str)) match {
    case Success(value) => JsSuccess(value)
    case Failure(_)     => JsError(s"'$str' is not an url")
  }

  private val reads: Reads[Uri] = Reads.StringReads.jsrFlatMap(s => parseUrl(s)) andKeep Reads.StringReads.map(_.trim).map(Uri(_))
  private val writes: Writes[Uri] = implicitly[Format[String]].inmap[Uri](Uri(_), _.toString)

  implicit val uriJsonFormat: Format[Uri] = Format(reads, writes)
}
