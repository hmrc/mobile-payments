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

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

import java.time.Month.APRIL
import java.time.{Clock, LocalDate, Month}

final case class TaxYear(endYear: Int) extends TaxPeriod {
  val startYear: Int = endYear - 1
  val startMonth: Month = APRIL
  val endMonth: Month = APRIL
  def previousTaxYear: TaxYear = TaxYear(endYear - 1)
  def nextTaxYear: TaxYear = TaxYear(endYear + 1)
}

object TaxYear {
  implicit val format: Format[TaxYear] = implicitly[Format[Int]].inmap(TaxYear(_), _.endYear)
  def apply(date: LocalDate): TaxYear = {
    if ((date.getMonth == APRIL && date.getDayOfMonth > TaxMonth.taxMonthEndDay) || date.getMonthValue > APRIL.getValue) TaxYear(date.getYear + 1)
    else TaxYear(date.getYear)
  }

  def getXTaxYearsFromPresent(x: Int)(implicit clock: Clock): List[TaxYear] = (for { i <- 1 to x + 1 } yield getTaxYear(LocalDate.now(clock).minusYears(i))).toList

  def getTaxYear(date: LocalDate): TaxYear = {
    if ((date.getMonthValue == 4 && date.getDayOfMonth > 5) || date.getMonthValue > 4) TaxYear(date.getYear + 1)
    else TaxYear(date.getYear)
  }

}
