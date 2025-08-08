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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.ct

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

/**
 * Corporation Tax (Ct) Unique Taxpayer Reference (Utr)
 */
final case class CtUtr(value: String) {
  val canonicalizedValue: String = value.trim
}

object CtUtr {
  implicit val format: Format[CtUtr] = implicitly[Format[String]].inmap(CtUtr(_), _.value)

  private val weights = List(6, 7, 8, 9, 10, 5, 4, 3, 2)

  def modulusCheck(input: List[Int], head: Int): Boolean = {
    val total = input.zip(weights).map { case (a, b) => a * b }.sum
    val check = 11 - (total % 11)
    if (check > 9) (check - 9) == head
    else check == head
  }

  def createValid(input: String): Either[String, CtUtr] = {
    if (modulusCheck(input.tail.map(_.toInt - 48).toList, input.head.toInt - 48)) Right(CtUtr(input))
    else Left("invalid ctUtr")
  }
}
