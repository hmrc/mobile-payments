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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.vat

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

final case class CalendarPeriod(month: Int, year: Int) {

  def atFirstDayOfMonth: LocalDate = LocalDate.of(year, month, 1)

  lazy val month2Digits: String = if (month < 10) "0" + month.toString else month.toString
  lazy val year2Digit: String = ("00" + year.toString).takeRight(2)

  def asReferenceSuffix: String = month2Digits + year2Digit
}

object CalendarPeriod {
  implicit val vatPeriodFormat: OFormat[CalendarPeriod] = Json.format[CalendarPeriod]

  def fromLocalDate(date: LocalDate): CalendarPeriod = CalendarPeriod(date.getMonthValue, date.getYear)
}

