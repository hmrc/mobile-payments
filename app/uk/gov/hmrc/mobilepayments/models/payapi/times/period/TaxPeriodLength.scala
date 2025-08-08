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

package uk.gov.hmrc.mobilepayments.models.payapi.times.period

import enumeratum.{Enum, EnumEntry}
import play.api.libs.json.Format
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.EnumFormat

import scala.collection.immutable

sealed abstract class TaxPeriodLength extends EnumEntry

object TaxPeriodLength {
  implicit val format: Format[TaxPeriodLength] = EnumFormat(TaxPeriodLengths)
}

object TaxPeriodLengths extends Enum[TaxPeriodLength] {
  case object Month extends TaxPeriodLength
  case object Quarter extends TaxPeriodLength
  case object Year extends TaxPeriodLength

  override def values: immutable.IndexedSeq[TaxPeriodLength] = findValues
}

