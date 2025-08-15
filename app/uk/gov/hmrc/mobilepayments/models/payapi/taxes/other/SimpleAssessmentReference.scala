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
import uk.gov.hmrc.referencechecker.OtherTaxReferenceChecker

/**
 * 14 or 15 character reference starting with an X.
 *
 */
final case class SimpleAssessmentReference(value: String) {
  val canonicalizedValue: String = value.trim.toUpperCase()
}

object SimpleAssessmentReference {

  val simpleAssessmentRegex = """^X[A-Z](([0-9A-Z]{13})|([0-9A-Z]\d{11}))$"""

  def createValid(input: String): Either[String, SimpleAssessmentReference] = {
    if (input.matches(simpleAssessmentRegex))
      if (OtherTaxReferenceChecker.isValid(input)) Right(SimpleAssessmentReference(input))
      else Left(s"SimpleAssessmentReferenceXRef $input did not pass modulus check")
    else Left(s"SimpleAssessmentReference $input did not pass regEx check")
  }
  implicit val format: Format[SimpleAssessmentReference] = implicitly[Format[String]].inmap(SimpleAssessmentReference(_), _.value)

}

