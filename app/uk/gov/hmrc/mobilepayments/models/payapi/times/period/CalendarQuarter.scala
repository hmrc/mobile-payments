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

sealed abstract class CalendarQuarter private (val intValue: Int, val startMonth: Month, val endMonth: Month) extends EnumEntry {
  final def nextTaxQuarter: CalendarQuarter = {
    val cycledIndex = if (intValue == 4) 1 else intValue + 1
    CalendarQuarter.calendarQuarters
      .find(_.intValue == cycledIndex)
      .getOrElse(throw new RuntimeException(s"Trying to get next quarter to ${this.toString} but cycledIndex ${cycledIndex.toString} was invalid."))
  }

  final def previousTaxQuarter: CalendarQuarter = {
    val cycledIndex = if (intValue == 1) 4 else intValue - 1
    CalendarQuarter.calendarQuarters
      .find(_.intValue == cycledIndex)
      .getOrElse(
        throw new RuntimeException(s"Trying to get previous quarter to ${this.toString} but cycledIndex ${cycledIndex.toString} was invalid.")
      )
  }
}

object CalendarQuarter extends Enum[CalendarQuarter] {
  case object JanuaryToMarch    extends CalendarQuarter(1, JANUARY, MARCH)
  case object AprilToJune       extends CalendarQuarter(2, APRIL, JUNE)
  case object JulyToSeptember   extends CalendarQuarter(3, JULY, SEPTEMBER)
  case object OctoberToDecember extends CalendarQuarter(4, OCTOBER, DECEMBER)

  def values: immutable.IndexedSeq[CalendarQuarter] = findValues
  def calendarQuarters: List[CalendarQuarter] = values.toList

  def apply(date: LocalDate): CalendarQuarter = date.getMonth match {
    case JANUARY | FEBRUARY | MARCH    => JanuaryToMarch
    case APRIL | MAY | JUNE            => AprilToJune
    case JULY | AUGUST | SEPTEMBER     => JulyToSeptember
    case OCTOBER | NOVEMBER | DECEMBER => OctoberToDecember
  }

  implicit val format: Format[CalendarQuarter] = EnumFormat(CalendarQuarter)
}
