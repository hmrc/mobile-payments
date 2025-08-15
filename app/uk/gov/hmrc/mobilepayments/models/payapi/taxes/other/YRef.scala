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

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

/**
 * Y followed by a alpha character followed by 12 digits
 * e.g. YA123456789123
 */

final case class YRef(value: String) {
  val canonicalisedValue: String = value.trim.toUpperCase()
}

object YRef {
  val yRefRegex = """^Y[A-Z][0-9]{12}$"""
  implicit val format: Format[YRef] = implicitly[Format[String]].inmap(YRef(_), _.value)

  def createValid(input: String): Either[String, YRef] =
    if (input.matches(YRef.yRefRegex)) Right(YRef(input)) else Left(s"YRef $input did not pass regEx check")
}
