/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.mobilepayments.mocks.AuditStub
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

class AuditServiceSpec extends BaseSpec with AuditStub {

  private implicit val mockConnector: AuditConnector = mock[AuditConnector]
  private val amount:                 BigDecimal     = 154.63
  private val amountInPence:          Long           = 15463L
  private val saUtr:                  SaUtr          = SaUtr("CS700100A")

  private val sut = new AuditService(mockConnector, "mobile-payments")

  "when event triggered and stubbed it" should {
    "receive audit event" in {
      stubPaymentEvent(amountInPence, saUtr, journeyId.toString())
      sut.sendPaymentEvent(amount, saUtr, journeyId.toString())
    }
  }
}
