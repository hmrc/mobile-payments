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

package uk.gov.hmrc.mobilepayments.controllers

import play.api.Logger
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.BanksResponse
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, MockResponses}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

class SandboxBankControllerSpec extends BaseSpec with AuthorisationStub with MockResponses {
  private val logger:          Logger          = Logger(this.getClass)
  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200

  implicit val mockAuditConnector: AuditConnector = mock[AuditConnector]
  implicit val mockAuthConnector:  AuthConnector  = mock[AuthConnector]

  private val sut = new SandboxBankController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    Helpers.stubControllerComponents()
  )

  "when get banks invoked and service returns success then" should {
    "return 200" in {
      stubAuthorisationGrantAccess(confidenceLevel)

      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 200
      val response = contentAsJson(result).as[BanksResponse]
      response.data.size shouldBe 68
    }
  }

  "when get banks invoked and auth fails then" should {
    "return 401" in {
      stubAuthorisationWithAuthorisationException()

      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

      val result = sut.getBanks(journeyId)(request)
      status(result) shouldBe 401
    }
  }
}
