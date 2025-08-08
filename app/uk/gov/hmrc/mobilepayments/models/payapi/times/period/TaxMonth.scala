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

sealed abstract class TaxMonth private (val intValue: Int, val startMonth: Month, val endMonth: Month) extends TaxPeriod with EnumEntry {

  final def nextTaxMonth: TaxMonth = {
    val cycledIndex = if (intValue == 12) 1 else intValue + 1
    TaxMonth.taxMonths
      .find(_.intValue == cycledIndex)
      .getOrElse(throw new RuntimeException(s"Trying to get next month to ${this.toString} but cycledIndex ${cycledIndex.toString} was invalid."))
  }

  final def previousTaxMonth: TaxMonth = {
    val cycledIndex = if (intValue == 1) 12 else intValue - 1
    TaxMonth.taxMonths
      .find(_.intValue == cycledIndex)
      .getOrElse(throw new RuntimeException(s"Trying to get previous month to ${this.toString} but cycledIndex ${cycledIndex.toString} was invalid."))
  }
}

object TaxMonth extends Enum[TaxMonth] {
  case object AprilMay         extends TaxMonth(1, APRIL, MAY)
  case object MayJune          extends TaxMonth(2, MAY, JUNE)
  case object JuneJuly         extends TaxMonth(3, JUNE, JULY)
  case object JulyAugust       extends TaxMonth(4, JULY, AUGUST)
  case object AugustSeptember  extends TaxMonth(5, AUGUST, SEPTEMBER)
  case object SeptemberOctober extends TaxMonth(6, SEPTEMBER, OCTOBER)
  case object OctoberNovember  extends TaxMonth(7, OCTOBER, NOVEMBER)
  case object NovemberDecember extends TaxMonth(8, NOVEMBER, DECEMBER)
  case object DecemberJanuary  extends TaxMonth(9, DECEMBER, JANUARY)
  case object JanuaryFebruary  extends TaxMonth(10, JANUARY, FEBRUARY)
  case object FebruaryMarch    extends TaxMonth(11, FEBRUARY, MARCH)
  case object MarchApril       extends TaxMonth(12, MARCH, APRIL)

  def values: immutable.IndexedSeq[TaxMonth] = findValues
  def taxMonths: List[TaxMonth] = values.toList

  def apply(date: LocalDate): TaxMonth = {
    if (date.getDayOfMonth >= taxMonthStartDay) taxMonths.find(_.startMonth == date.getMonth)
    else taxMonths.find(_.endMonth == date.getMonth)
  } getOrElse (throw new RuntimeException(s"No tax month found for date: ${date.toString}"))

  val taxMonthStartDay: Int = 6
  val taxMonthEndDay: Int = 5

  implicit val format: Format[TaxMonth] = EnumFormat(TaxMonth)
}
