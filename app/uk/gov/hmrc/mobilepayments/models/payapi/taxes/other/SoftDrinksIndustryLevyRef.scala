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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.other

import play.api.libs.functional.syntax.toInvariantFunctorOps
import play.api.libs.json.Format

final case class SoftDrinksIndustryLevyRef(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object SoftDrinksIndustryLevyRef {

  val softDrinksIndustryLevyRefRegex = """^[X|x]([0-9a-zA-Z]{14})$"""
  val softDrinksIndustryLevyPenaltyRefRegex = """^[X|x]([0-9a-zA-Z]{13})$"""

  def createValid(input: String): Either[String, SoftDrinksIndustryLevyRef] = {
    createValidRef(input) match {
      case Left(_) => createValidPenaltyRef(input) match {
        case Left(_)      => Left(s"[SoftDrinksIndustryLevyRef: $input] did not pass standard or penalty regEx check")
        case Right(value) => Right(value)
      }
      case Right(value) => Right(value)
    }
  }

  def createValidRef(input: String): Either[String, SoftDrinksIndustryLevyRef] = {
    val inputWithoutSpaces = input.replaceAll("\\s", "")
    if (inputWithoutSpaces.matches(softDrinksIndustryLevyRefRegex))
      Right(SoftDrinksIndustryLevyRef(inputWithoutSpaces.toUpperCase))
    else
      Left(s"[SoftDrinksIndustryLevyRef: $input] did not pass regEx check")
  }

  def createValidPenaltyRef(input: String): Either[String, SoftDrinksIndustryLevyRef] = {
    val inputWithoutSpaces = input.replaceAll("\\s", "")
    if (inputWithoutSpaces.matches(softDrinksIndustryLevyPenaltyRefRegex))
      Right(SoftDrinksIndustryLevyRef(inputWithoutSpaces.toUpperCase))
    else
      Left(s"[SoftDrinksIndustryLevyPenaltyRef: $input] did not pass regEx check")
  }
  implicit val format: Format[SoftDrinksIndustryLevyRef] = implicitly[Format[String]].inmap(SoftDrinksIndustryLevyRef(_), _.value)
}

