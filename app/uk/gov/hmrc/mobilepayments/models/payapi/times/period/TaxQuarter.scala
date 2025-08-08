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

import java.time.Month.*
import java.time.{LocalDate, Month}
import scala.collection.immutable

sealed abstract class TaxQuarter private (val intValue: Int, val startMonth: Month, val endMonth: Month) extends TaxPeriod with EnumEntry {

  final def nextTaxQuarter: TaxQuarter = {
    val cycledIndex = if (intValue == 4) 1 else intValue + 1
    TaxQuarter.taxQuarters
      .find(_.intValue == cycledIndex)
      .getOrElse(throw new RuntimeException(s"Trying to get next quarter to ${this.toString} but cycledIndex ${cycledIndex.toString} was invalid."))
  }

  final def previousTaxQuarter: TaxQuarter = {
    val cycledIndex = if (intValue == 1) 4 else intValue - 1
    TaxQuarter.taxQuarters
      .find(_.intValue == cycledIndex)
      .getOrElse(
        throw new RuntimeException(s"Trying to get previous quarter to ${this.toString} but cycledIndex ${cycledIndex.toString} was invalid.")
      )
  }

}

object TaxQuarter extends Enum[TaxQuarter] {
  case object AprilJuly      extends TaxQuarter(1, APRIL, JULY)
  case object JulyOctober    extends TaxQuarter(2, JULY, OCTOBER)
  case object OctoberJanuary extends TaxQuarter(3, OCTOBER, JANUARY)
  case object JanuaryApril   extends TaxQuarter(4, JANUARY, APRIL)

  def values: immutable.IndexedSeq[TaxQuarter] = findValues
  def taxQuarters: List[TaxQuarter] = values.toList

  def apply(date: LocalDate): TaxQuarter = date.getMonth match {
    case APRIL               => if (date.getDayOfMonth > 5) AprilJuly else JanuaryApril
    case MAY | JUNE          => AprilJuly
    case JULY                => if (date.getDayOfMonth > 5) JulyOctober else AprilJuly
    case AUGUST | SEPTEMBER  => JulyOctober
    case OCTOBER             => if (date.getDayOfMonth > 5) OctoberJanuary else JulyOctober
    case NOVEMBER | DECEMBER => OctoberJanuary
    case JANUARY             => if (date.getDayOfMonth > 5) JanuaryApril else OctoberJanuary
    case FEBRUARY | MARCH    => JanuaryApril
  }
  implicit val format: Format[TaxQuarter] = EnumFormat(TaxQuarter)
}
