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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.mobilepayments.models.payapi.times.period.CalendarQuarter.*

import java.time.LocalDate
import java.time.Month.*

final case class CalendarQuarterlyPeriod(quarter: CalendarQuarter, year: Int) {
  def previousPeriod: CalendarQuarterlyPeriod = quarter match {
    case JanuaryToMarch => CalendarQuarterlyPeriod(OctoberToDecember, year - 1)
    case _              => CalendarQuarterlyPeriod(quarter.previousTaxQuarter, year)
  }

  def isAfter(other: CalendarQuarterlyPeriod): Boolean =
    (year > other.year) || (year == other.year && quarter.intValue > other.quarter.intValue)

  def isBefore(other: CalendarQuarterlyPeriod): Boolean = this != other && !isAfter(other)

  def periodCode = s"${quarter.intValue.toString}${year.toString.takeRight(2)}"
}

object CalendarQuarterlyPeriod {
  implicit val format: Format[CalendarQuarterlyPeriod] = Json.format

  def apply(date: LocalDate): CalendarQuarterlyPeriod = date.getMonth match {
    case JANUARY | FEBRUARY | MARCH    => CalendarQuarterlyPeriod(JanuaryToMarch, date.getYear)
    case APRIL | MAY | JUNE            => CalendarQuarterlyPeriod(AprilToJune, date.getYear)
    case JULY | AUGUST | SEPTEMBER     => CalendarQuarterlyPeriod(JulyToSeptember, date.getYear)
    case OCTOBER | NOVEMBER | DECEMBER => CalendarQuarterlyPeriod(OctoberToDecember, date.getYear)
  }
}
