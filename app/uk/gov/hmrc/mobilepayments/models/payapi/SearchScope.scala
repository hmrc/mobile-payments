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

package uk.gov.hmrc.mobilepayments.models.payapi

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import play.api.mvc.PathBindable
import uk.gov.hmrc.mobilepayments.domain.dto.response.ValueClassBinder.valueClassBinder

final case class SearchScope(value: String) extends AnyVal

object SearchScope {

  val `BTA`: SearchScope = SearchScope("BTA")
  val `payments-frontend`: SearchScope = SearchScope("payments-frontend")
  val `third-party-software`: SearchScope = SearchScope("third-party-software")
  val `pillar-2`: SearchScope = SearchScope("pillar-2")
  val `web-chat`: SearchScope = SearchScope("web-chat")

  private val validSearchScopes: Set[String] = Set(
    `payments-frontend`.value,
    `BTA`.value,
    "PNGR",
    "AMLS",
    "ITSA",
    "PTA",
    "DD",
    "PPT",
    "APP",
    "capital-gains-tax",
    "job-retention-scheme",
    "parcels",
    "ni-eu-vat-oss",
    "ni-eu-vat-ioss",
    "VNC",
    "MIB",
    `third-party-software`.value,
    `pillar-2`.value,
    `web-chat`.value
  )

  implicit val format: Format[SearchScope] = implicitly[Format[String]].inmap(SearchScope(_), _.value)
  implicit val searchScopeBinder: PathBindable[SearchScope] = valueClassBinder(_.value)

  implicit class SearchScopeExt(searchScope: SearchScope) {
    def isValid: Boolean = validSearchScopes.contains(searchScope.value)
    val invalidSearchScopeMessage = s"""Cannot parse param searchScope as SearchScope. Unknown search scope: '${searchScope.value}'"""
  }

}
