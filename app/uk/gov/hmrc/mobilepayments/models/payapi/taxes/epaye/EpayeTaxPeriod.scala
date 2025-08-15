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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.epaye

import java.time.temporal.ChronoUnit
import java.time.{Clock, LocalDate}
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.*
import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import uk.gov.hmrc.mobilepayments.models.payapi.times.period.*

import scala.util.Try

sealed trait EpayeTaxPeriod {
  def taxFrom: LocalDate
  def taxTo: LocalDate
}

final case class VariableLengthTaxPeriod(taxFrom: LocalDate, taxTo: LocalDate) extends EpayeTaxPeriod {
  def lengthInDays: Long = ChronoUnit.DAYS.between(taxFrom, taxTo)

  def asYearlyPeriod: Either[String, YearlyEpayeTaxPeriod] = asFixedLengthPeriod match {
    case Right(_: SubYearlyEpayeTaxPeriod)   => Left(s"Trying to convert to YearlyPeriod but length was not Yearly.")
    case Right(period: YearlyEpayeTaxPeriod) => Right(period)
    case Left(err)                           => Left(err)
  }

  def asSubYearlyPeriod: Either[String, SubYearlyEpayeTaxPeriod] = asFixedLengthPeriod match {
    case Right(_: YearlyEpayeTaxPeriod)         => Left(s"Trying to convert to SubYearlyPeriod but length was Yearly.")
    case Right(period: SubYearlyEpayeTaxPeriod) => Right(period)
    case Left(err)                              => Left(err)
  }

  def asFixedLengthPeriod: Either[String, FixedLengthEpayeTaxPeriod] = {
    if (taxFrom.getDayOfMonth != 6)
      Left(s"Invalid taxFrom - expected period to start on the 6th but it actually started on: ${taxFrom.getDayOfMonth.toString}")
    else if (taxTo.getDayOfMonth != 5)
      Left(s"Invalid taxTo - expected period to end on the 5th but it actually ended on: ${taxFrom.getDayOfMonth.toString}")
    else
      lengthInDays match {
        case days if days >= 27 && days <= 30   => Right(MonthlyEpayeTaxPeriod(TaxMonth(taxTo), TaxYear(taxTo)))
        case days if days >= 89 && days <= 91   => Right(QuarterlyEpayeTaxPeriod(TaxQuarter(taxTo), TaxYear(taxTo)))
        case days if days >= 364 && days <= 365 => Right(YearlyEpayeTaxPeriod(TaxYear(taxTo)))
        case _ =>
          Left(
            s"Days between ${taxFrom.toString} and ${taxTo.toString} was ${lengthInDays.toString}, which did not equate to a monthly, quarterly or yearly period length."
          )
      }
  }
}

object VariableLengthTaxPeriod {
  implicit val format: Format[VariableLengthTaxPeriod] = Json.format[VariableLengthTaxPeriod]
}

sealed trait FixedLengthEpayeTaxPeriod extends EpayeTaxPeriod {
  def code: String
  def taxYear: TaxYear
  def periodLength: TaxPeriodLength
}

object FixedLengthEpayeTaxPeriod {
  private val mainReads: Reads[FixedLengthEpayeTaxPeriod] =
    MonthlyEpayeTaxPeriod.format
      .castUp[FixedLengthEpayeTaxPeriod] orElse QuarterlyEpayeTaxPeriod.format.castUp orElse YearlyEpayeTaxPeriod.format.castUp

  private val mainWrites: OWrites[FixedLengthEpayeTaxPeriod] = OWrites {
    case period: YearlyEpayeTaxPeriod    => YearlyEpayeTaxPeriod.format.writes(period)
    case period: QuarterlyEpayeTaxPeriod => QuarterlyEpayeTaxPeriod.format.writes(period)
    case period: MonthlyEpayeTaxPeriod   => MonthlyEpayeTaxPeriod.format.writes(period)
  }

  implicit val format: OFormat[FixedLengthEpayeTaxPeriod] = OFormat(mainReads, mainWrites)
}

final case class YearlyEpayeTaxPeriod(taxYear: TaxYear) extends FixedLengthEpayeTaxPeriod {
  override def taxFrom: LocalDate = taxYear.startDateInTaxYear(taxYear)
  override def taxTo: LocalDate = taxYear.endDateInTaxYear(taxYear)
  override def code: String = TaxYear(taxTo).endYear.toString.takeRight(2) + "13"
  override def periodLength: TaxPeriodLength = TaxPeriodLengths.Month
}

object YearlyEpayeTaxPeriod {
  // Legacy reads for NiPeriod format. TODO: Delete after two months from release (written 04/09/19)
  val legacyReads: Reads[YearlyEpayeTaxPeriod] = (
    (__ \ "periodCode").read[String] and
      (__ \ "periodLength").read[TaxPeriodLength].filter(_ == TaxPeriodLengths.Year)
  ) { (code, _) =>
    val taxYear = TaxYear(("20" + code.take(2)).toInt)
    if (code.drop(2) != "13") throw new RuntimeException(s"Attempting to deserialise yearly NiPeriod with invalid code: $code")
    YearlyEpayeTaxPeriod(taxYear)
  }
  private val mainFormat = Json.format[YearlyEpayeTaxPeriod]
  implicit val format: OFormat[YearlyEpayeTaxPeriod] = OFormat(mainFormat orElse legacyReads, mainFormat)
}

sealed trait SubYearlyEpayeTaxPeriod extends FixedLengthEpayeTaxPeriod {
  final def code: String = TaxYear(taxTo).endYear.toString.takeRight(2) + ("0" + TaxMonth(taxTo).intValue.toString).takeRight(2)
}

object SubYearlyEpayeTaxPeriod {
  private def reads: Reads[SubYearlyEpayeTaxPeriod] =
    MonthlyEpayeTaxPeriod.format.castUp[SubYearlyEpayeTaxPeriod] orElse QuarterlyEpayeTaxPeriod.format.castUp

  private def writes: OWrites[SubYearlyEpayeTaxPeriod] = OWrites {
    case period: MonthlyEpayeTaxPeriod   => MonthlyEpayeTaxPeriod.format.writes(period)
    case period: QuarterlyEpayeTaxPeriod => QuarterlyEpayeTaxPeriod.format.writes(period)
  }

  implicit val format: OFormat[SubYearlyEpayeTaxPeriod] = OFormat(reads, writes)
}

final case class QuarterlyEpayeTaxPeriod(taxQuarter: TaxQuarter, taxYear: TaxYear) extends SubYearlyEpayeTaxPeriod {
  override def taxFrom: LocalDate = taxQuarter.startDateInTaxYear(taxYear)
  override def taxTo: LocalDate = taxQuarter.endDateInTaxYear(taxYear)
  override def periodLength: TaxPeriodLength = TaxPeriodLengths.Quarter
}

object QuarterlyEpayeTaxPeriod {

  import cats.implicits.toBifunctorOps

  def fromCode(code: String): Either[String, QuarterlyEpayeTaxPeriod] = {
    lazy val taxQuarter: Either[String, TaxQuarter] =
      Try(TaxQuarter.taxQuarters.find(_.intValue == ((code.drop(2).toInt - 1) / 3) + 1)).toEither
        .leftMap(_ => s"Attempting to derive quarterly Epaye period from invalid code: $code")
        .flatMap(_.toRight(s"Attempting to derive quarterly Epaye period from invalid code: $code"))

    lazy val taxYear: Either[String, TaxYear] =
      Try(TaxYear(("20" + code.take(2)).toInt)).toEither
        .leftMap(_ => s"Attempting to derive tax year for Epaye period from invalid code: $code")

    if (code.length < 4) Left("Epaye quarterly tax period code too short")
    else if (code.length > 4) Left("Epaye quarterly code too long")
    else if (!code.forall(_.isDigit)) Left("Invalid character in Epaye quarterly code")
    else {
      (taxQuarter, taxYear) match {
        case (Right(validTaxQuarter), Right(validTaxYear)) => Right(QuarterlyEpayeTaxPeriod(validTaxQuarter, validTaxYear))
        case (Left(invalidTaxQuarter), _)                  => Left(invalidTaxQuarter)
        case (_, Left(invalidTaxYear))                     => Left(invalidTaxYear) // not feasibly possible due to check for digits above.
      }
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def previousNQuarterlyPeriods(currentPeriod: QuarterlyEpayeTaxPeriod, n: Int): List[QuarterlyEpayeTaxPeriod] =
    if (n > 1)
      currentPeriod :: previousNQuarterlyPeriods(
        currentPeriod.copy(
          taxQuarter = currentPeriod.taxQuarter.previousTaxQuarter,
          taxYear    = if (currentPeriod.taxQuarter.intValue == 1) currentPeriod.taxYear.previousTaxYear else currentPeriod.taxYear
        ),
        n - 1
      )
    else List(currentPeriod)

  def nextQuarterlyPeriod(clock: Clock): QuarterlyEpayeTaxPeriod =
    QuarterlyEpayeTaxPeriod(TaxQuarter(LocalDate.now(clock)).nextTaxQuarter, TaxYear(LocalDate.now(clock).plusMonths(3)))

  // Legacy reads for NiPeriod format. TODO: Delete after two months from release (written 04/09/19)
  val legacyReads: Reads[QuarterlyEpayeTaxPeriod] = (
    (__ \ "periodCode").read[String] and
      (__ \ "periodLength").read[TaxPeriodLength].filter(_ == TaxPeriodLengths.Quarter)
  ) { (code, _) =>
    val taxYear = TaxYear(("20" + code.take(2)).toInt)
    val taxQuarter: TaxQuarter = TaxQuarter.taxQuarters
      .find(_.intValue == ((code.drop(2).toInt - 1) / 3) + 1)
      .getOrElse(throw new RuntimeException(s"Attempting to deserialise quarterly NiPeriod with invalid code: $code"))
    QuarterlyEpayeTaxPeriod(taxQuarter, taxYear)
  }
  private val mainFormat = Json.format[QuarterlyEpayeTaxPeriod]
  implicit val format: OFormat[QuarterlyEpayeTaxPeriod] = OFormat(mainFormat orElse legacyReads, mainFormat)
}

final case class MonthlyEpayeTaxPeriod(taxMonth: TaxMonth, taxYear: TaxYear) extends SubYearlyEpayeTaxPeriod {
  override def taxFrom: LocalDate = taxMonth.startDateInTaxYear(taxYear)
  override def taxTo: LocalDate = taxMonth.endDateInTaxYear(taxYear)
  override def periodLength: TaxPeriodLength = TaxPeriodLengths.Month
}

object MonthlyEpayeTaxPeriod {
  // Legacy reads for NiPeriod format. TODO: Delete after two months from release (written 04/09/19)
  val legacyReads: Reads[MonthlyEpayeTaxPeriod] = (
    (__ \ "periodCode").read[String] and
      (__ \ "periodLength").read[TaxPeriodLength].filter(_ == TaxPeriodLengths.Month)
  ) { (code, _) =>
    val taxYear = TaxYear(("20" + code.take(2)).toInt)
    val taxMonth = TaxMonth.taxMonths
      .find(_.intValue == code.drop(2).toInt)
      .getOrElse(throw new RuntimeException(s"Attempting to deserialise monthly NiPeriod with invalid code: $code"))
    MonthlyEpayeTaxPeriod(taxMonth, taxYear)
  }
  private val mainFormat = Json.format[MonthlyEpayeTaxPeriod]
  implicit val format: OFormat[MonthlyEpayeTaxPeriod] = OFormat(mainFormat orElse legacyReads, mainFormat)

  def fromCode(code: String): Either[String, MonthlyEpayeTaxPeriod] = {

    import cats.implicits.toBifunctorOps

    lazy val taxMonth: Either[String, TaxMonth] =
      Try(TaxMonth.taxMonths.find(_.intValue == code.drop(2).toInt)).toEither
        .leftMap(_ => s"Attempting to derive monthly Epaye period from invalid code: $code")
        .flatMap(_.toRight(s"Attempting to derive monthly Epaye period from invalid code: $code"))

    lazy val taxYear: Either[String, TaxYear] =
      Try(TaxYear(("20" + code.take(2)).toInt)).toEither
        .leftMap(_ => s"Attempting to derive tax year for Epaye period from invalid code: $code")

    if (code.length < 4) Left("Epaye monthly tax period code too short")
    else if (code.length > 4) Left("Epaye monthly code too long")
    else if (!code.forall(_.isDigit)) Left("Invalid character in Epaye monthly code")
    else {
      (taxMonth, taxYear) match {
        case (Right(validTaxMonth), Right(validTaxYear)) => Right(MonthlyEpayeTaxPeriod(validTaxMonth, validTaxYear))
        case (Left(invalidTaxMonth), _)                  => Left(invalidTaxMonth)
        case (_, Left(invalidTaxYear))                   => Left(invalidTaxYear) // not feasibly possible due to check for digits above.
      }
    }
  }

  def nextMonthlyPeriod(implicit clock: Clock): MonthlyEpayeTaxPeriod =
    MonthlyEpayeTaxPeriod(TaxMonth(LocalDate.now(clock)).nextTaxMonth, TaxYear(LocalDate.now(clock).plusMonths(1)))

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def previousNMonthlyPeriods(currentPeriod: MonthlyEpayeTaxPeriod, n: Int): List[MonthlyEpayeTaxPeriod] =
    if (n > 1)
      currentPeriod :: previousNMonthlyPeriods(
        currentPeriod.copy(
          taxMonth = currentPeriod.taxMonth.previousTaxMonth,
          taxYear  = if (currentPeriod.taxMonth.intValue == 1) currentPeriod.taxYear.previousTaxYear else currentPeriod.taxYear
        ),
        n - 1
      )
    else List(currentPeriod)
}
