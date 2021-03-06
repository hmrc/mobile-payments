/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalatest.time.SpanSugar.convertDoubleToGrainOfTime
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.connectors.PaymentsConnector
import uk.gov.hmrc.mobilepayments.domain.dto.response.LatestPaymentsResponse
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.domain.{AmountInPence, PaymentRecordListFromApi}

import scala.concurrent.{Await, Future}

class PaymentsServiceSpec extends BaseSpec with MobilePaymentsTestData {

  private val mockConnector: PaymentsConnector = mock[PaymentsConnector]
  private val saUtr:         SaUtr             = SaUtr("CS700100A")

  private val sut = new PaymentsService(mockConnector)

  "when getLatestPayments invoked and connector returns success with payments then" should {
    "return successful payments made within the last 14 days" in {
      mockLatestPayments(Future successful Right(Some(paymentsResponse())))

      val result = Await.result(sut.getLatestPayments(saUtr.value, journeyId), 0.5.seconds)
      result.right.get.get.payments.size shouldBe 1
      result.right.get.get.payments.head.amountInPence shouldBe 11100
    }

    "return None if no successful payments made within the last 14 days" in {
      mockLatestPayments(Future successful Right(Some(paymentsResponse("2022-05-01"))))

      val result = Await.result(sut.getLatestPayments(saUtr.value, journeyId), 0.5.seconds)
      result.right.get shouldBe None
    }

    "return None if no payments made within the last 14 days" in {
      mockLatestPayments(Future successful Right(None))

      val result = Await.result(sut.getLatestPayments(saUtr.value, journeyId), 0.5.seconds)
      result.right.get shouldBe None
    }

    "return Error if exception returned from PaymentsConnector" in {
      mockLatestPayments(Future successful Left("Error while calling pay api"))

      val result = Await.result(sut.getLatestPayments(saUtr.value, journeyId), 0.5.seconds)
      result.left.get shouldBe "Error while calling pay api"
    }
  }

  private def mockLatestPayments(future: Future[Either[String, Option[PaymentRecordListFromApi]]]): Unit =
    (mockConnector
      .getSelfAssessmentPayments(_: String, _: JourneyId)(_: HeaderCarrier))
      .expects(*, journeyId, hc)
      .returning(future)

}
