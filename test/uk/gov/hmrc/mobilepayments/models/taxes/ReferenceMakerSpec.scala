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

package uk.gov.hmrc.mobilepayments.models.taxes

import org.scalatest.Assertions.withClue
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.models.payapi.Reference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.ReferenceMaker.*
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.epaye.PsaNumber
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.mib.MibReference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.other.{BeerDutyRef, XRef}
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.p800.P800Ref
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.parcels.ParcelsChargeReference
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sa.SaUtr
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.sdlt.Utrn
import uk.gov.hmrc.mobilepayments.models.payapi.taxes.vat.{CalendarPeriod, Vrn}

class ReferenceMakerSpec extends BaseSpec {
  "make reference" in {
    makeVatReturnReference(Vrn("968501144"), CalendarPeriod(10, 2019)) shouldBe Reference("9685011441019")
    makeVatReturnReference(Vrn("968501144"), CalendarPeriod(2, 2019))  shouldBe Reference("9685011440219")
    makeVatReturnReference(Vrn("968501144"), CalendarPeriod(10, 19))   shouldBe Reference("9685011441019")
    makeVatReturnReference(Vrn("968501144"), CalendarPeriod(2, 19))    shouldBe Reference("9685011440219")

    withClue("add a K at the end if not present in the tax reference") {
      makeSaReference(SaUtr("1097172564")) shouldBe Reference("1097172564K")
    }
    withClue("don't add a K if already present") {
      makeSaReference(SaUtr("1097172564K")) shouldBe Reference("1097172564K")
    }
    withClue("uppercase and don't add a K if already present") {
      makeSaReference(SaUtr("1097172564k")) shouldBe Reference("1097172564K")
    }
    withClue("uppercases it") {
      makeSdltReference(Utrn("123456789Ma")) shouldBe Reference("123456789MA")
    }
    withClue("just uppercase") {
      makeXReference(XRef("XP00CATINTHEhat")) shouldBe Reference("XP00CATINTHEHAT")
    }

    makeXReference(XRef("xe123456789012")) shouldBe Reference("XE123456789012")

    makeP800Reference(P800Ref("ma000003AP3022016")) shouldBe Reference("MA000003AP3022016")

    makeSetaReference(PsaNumber("xe123456789012")) shouldBe Reference("XE123456789012")

    makeMibReference(MibReference("MIBI1234567890")) shouldBe Reference("MIBI1234567890")

    makeParcelsReference(ParcelsChargeReference("123456789X")) shouldBe Reference("123456789X")

    makeBeerDutyRef(BeerDutyRef("beer1234")) shouldBe Reference("BEER1234")

  }
}
