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

package uk.gov.hmrc.mobilepayments.models.payapi

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import play.api.mvc.PathBindable
import uk.gov.hmrc.mobilepayments.domain.dto.response.ValueClassBinder.valueClassBinder

final case class SearchTag(value: String)

object SearchTag {
  implicit val format: Format[SearchTag] = implicitly[Format[String]].inmap(SearchTag(_), _.value)
  implicit val searchTagBinder: PathBindable[SearchTag] = valueClassBinder(_.value)
}
