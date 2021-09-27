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

import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.syntax.retrieved._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.auth.core.{AuthConnector, ConfidenceLevel}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.mocks.{AuthorisationStub, MockResponses}
import uk.gov.hmrc.mobilepayments.services.OpenBankingService
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.{ExecutionContext, Future}

class LiveBankControllerSpec extends BaseSpec with AuthorisationStub with MockResponses {

  val confidenceLevel: ConfidenceLevel     = ConfidenceLevel.L200
  val internalId:      Option[String]      = Some("auth1234")
  val credentials:     Option[Credentials] = Some(Credentials("123456789", "type"))
  val mockService:     OpenBankingService  = mock[OpenBankingService]

  implicit val mockAuditConnector: AuditConnector = mock[AuditConnector]
  implicit val mockAuthConnector:  AuthConnector  = mock[AuthConnector]

  private val sut = new LiveBankController(
    mockAuthConnector,
    ConfidenceLevel.L200.level,
    Helpers.stubControllerComponents(),
    mockService
  )

  "when get banks invoked and service returns success then" should {
    "return 200 with data" in {
      stubAuthorisationGrantAccess(confidenceLevel and internalId and credentials)
      val request = FakeRequest("GET", "/banks")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")

      (mockService
        .getBanks(_: JourneyId)(_: ExecutionContext, _: HeaderCarrier))
        .expects(journeyId, ec, hc)
        .returning(Future.successful(Right(banksResponse)))
      val result = sut.getBanks(journeyId)(request)
      status(result)        shouldBe 200
      contentAsJson(result) shouldBe banksJson
    }
  }
}
