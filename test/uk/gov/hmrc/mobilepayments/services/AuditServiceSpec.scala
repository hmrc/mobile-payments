/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.services

import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.dto.response.Origins.{AppSa, AppSimpleAssessment}
import uk.gov.hmrc.mobilepayments.domain.dto.response.SessionDataResponse
import uk.gov.hmrc.mobilepayments.mocks.AuditStub
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import java.time.LocalDateTime
import java.util.UUID

class AuditServiceSpec extends BaseSpec with AuditStub {

  private implicit val mockConnector: AuditConnector = mock[AuditConnector]
  private val amountInPence: Long = 15463L
  private val saUtr: SaUtr = SaUtr("CS700100A")
  private val p302ChargeRef: String = "5544332211"
  private val bank: String = "barclays-personal"

  private val appSaSessionData: SessionDataResponse = SessionDataResponse(
    sessionDataId   = UUID.randomUUID().toString,
    amountInPence   = amountInPence,
    bankId          = Some("barclays-personal"),
    state           = "",
    createdOn       = LocalDateTime.now(),
    paymentDate     = None,
    reference       = saUtr.utr,
    email           = None,
    emailSent       = None,
    origin          = AppSa,
    maybeFutureDate = None
  )

  private val appSimpleAssessmentSessionData =
    appSaSessionData.copy(reference = p302ChargeRef, origin = AppSimpleAssessment)

  private val sut = new AuditService(mockConnector, "mobile-payments")

  "when event triggered and stubbed it" should {
    "receive audit event for Self Assessment Payment" in {
      stubSAPaymentEvent(amountInPence, saUtr, journeyId.value, bank)
      sut.sendPaymentEvent(appSaSessionData, journeyId.value)
    }

    "receive audit event for Simple Assessment Payment" in {
      stubSimpleAssessmentPaymentEvent(amountInPence, p302ChargeRef, journeyId.value, bank)
      sut.sendPaymentEvent(appSimpleAssessmentSessionData, journeyId.value)
    }
  }
}
