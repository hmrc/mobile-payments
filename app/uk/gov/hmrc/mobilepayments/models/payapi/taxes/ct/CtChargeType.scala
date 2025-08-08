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

import enumeratum.*
import play.api.libs.json.Format
import uk.gov.hmrc.mobilepayments.domain.dto.response.jsonext.EnumFormat

import scala.collection.immutable

sealed trait CtChargeType extends EnumEntry {
  def referenceSuffixForm: String
}

object CtChargeType {
  implicit val format: Format[CtChargeType] = EnumFormat(CtChargeTypes)
}

object CtChargeTypes extends Enum[CtChargeType] {
  /**
   * CT Charge types accoring to this document: https://docs.google.com/document/d/1-6zwOR4fbXfJnklKLqxdWbjcTsunjZmEECu1JI1kgzE/edit
   */

  //TODO: Give the charge types more semantically rich names
  case object A extends CtChargeType { def referenceSuffixForm: String = "A" }

  case object C extends CtChargeType { def referenceSuffixForm: String = "C" }

  case object N extends CtChargeType { def referenceSuffixForm: String = "N" }

  case object P extends CtChargeType { def referenceSuffixForm: String = "P" }

  case object T extends CtChargeType { def referenceSuffixForm: String = "T" }

  case object W extends CtChargeType { def referenceSuffixForm: String = "W" }

  case object X extends CtChargeType { def referenceSuffixForm: String = "X" }

  case object Y extends CtChargeType { def referenceSuffixForm: String = "Y" }

  case object Z extends CtChargeType { def referenceSuffixForm: String = "Z" }

  override def values: immutable.IndexedSeq[CtChargeType] = findValues
}
