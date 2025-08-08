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

import uk.gov.hmrc.mobilepayments.models.payapi.times.period.TaxMonth.*

import java.time.Month.APRIL
import java.time.{LocalDate, Month}

trait TaxPeriod {
  val startMonth: Month
  val endMonth: Month

  final def startDateInTaxYear(taxYear: TaxYear): LocalDate =
    if (startMonth.getValue >= APRIL.getValue) LocalDate.of(taxYear.startYear, startMonth, taxMonthStartDay)
    else LocalDate.of(taxYear.endYear, startMonth, taxMonthStartDay)

  final def endDateInTaxYear(taxYear: TaxYear): LocalDate =
    if (endMonth.getValue > APRIL.getValue) LocalDate.of(taxYear.startYear, endMonth, taxMonthEndDay)
    else LocalDate.of(taxYear.endYear, endMonth, taxMonthEndDay)
}
