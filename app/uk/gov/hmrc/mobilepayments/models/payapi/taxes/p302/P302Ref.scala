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

package uk.gov.hmrc.mobilepayments.models.payapi.taxes.p302

import play.api.libs.functional.syntax.*
import play.api.libs.json.Format

final case class P302Ref(value: String) {
  val canonicalizedValue: String = value.replaceAll("\\s", "").toUpperCase
}

object P302Ref {

  private val p302Regex = "^((?!(BG|GB|KN|NK|NT|TN|ZZ)|(D|F|I|Q|U|V)[A-Z]|[A-Z](D|F|I|O|Q|U|V))[A-Z]{2})[0-9]{6}[A-D](P302)[0-9]{4}$"

  def isValid(input: String): Either[String, P302Ref] = {
    if (input.matches(p302Regex)) {
      Right(P302Ref(input))
    } else Left("P302Ref failed regex check")
  }

  implicit val format: Format[P302Ref] = implicitly[Format[String]].inmap(P302Ref(_), _.value)
}
