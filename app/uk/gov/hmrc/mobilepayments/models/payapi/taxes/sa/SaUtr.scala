/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.sa

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

import scala.util.Try

/** Self Assessment (Sa) Unique Taxpayer Reference (Utr)
  */
final case class SaUtr(value: String) {

  /** Utr should have no 'K' character at the end.
    */
  def parseSaUtr: SaUtr = Try(SaUtr(SaUtr.parseSaUtr(value))).getOrElse(this)
}

object SaUtr {
  implicit val format: Format[SaUtr] = implicitly[Format[String]].inmap(SaUtr(_), _.value)

  private def parseSaUtr(utrString: String) = {
    val utrStringWithoutWithoutWhitespace = removeWhitespace(utrString)
    dropK(utrStringWithoutWithoutWhitespace)
  }

  private def removeWhitespace(utrString: String) = {
    utrString.replaceAll("\\s", "")
  }

  private def dropK(utrString: String) = {
    if (utrString.toLowerCase().startsWith("k")) { utrString.drop(1) }
    else if (utrString.toLowerCase().endsWith("k")) { utrString.dropRight(1) }
    else utrString

  }

  private val saRegex = """^\d{10}$"""

  private val weights = List(6, 7, 8, 9, 10, 5, 4, 3, 2)

  def modulusCheck(input: List[Int], head: Int): Boolean = {
    val total = input.zip(weights).map { case (a, b) => a * b }.sum
    val check = 11 - (total % 11)

    if (check > 9) (check - 9) == head
    else check == head
  }

  def createValid(input: String): Either[String, SaUtr] = {

    val utrFixed = parseSaUtr(input)

    utrFixed match {
      case s if s.matches(saRegex) && modulusCheck(s.tail.map(_.toInt - 48).toList, s.head.toInt - 48) =>
        Right(SaUtr(utrFixed))
      case "" =>
        Left("empty")
      case _ =>
        Left("invalid utr")
    }
  }

}
