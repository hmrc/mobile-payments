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

package uk.gov.hmrc.mobilepayments.connectors

import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import play.api.test.Helpers.await
import uk.gov.hmrc.domain.SaUtr
import uk.gov.hmrc.http._
import uk.gov.hmrc.mobilepayments.MobilePaymentsTestData
import uk.gov.hmrc.mobilepayments.common.BaseSpec
import uk.gov.hmrc.mobilepayments.domain.dto.request.TaxTypeEnum
import uk.gov.hmrc.mobilepayments.mocks.ConnectorStub

import scala.concurrent.Future

class PaymentsConnectorSpec extends BaseSpec with ConnectorStub with MobilePaymentsTestData with ScalaFutures {
  val mockHttp:                   HttpClient    = mock[HttpClient]
  implicit val mockHeaderCarrier: HeaderCarrier = mock[HeaderCarrier]

  val sut  = new PaymentsConnector(mockHttp, "baseUrl", "returnUrl", "backUrl")
  val utr  = "12344566"
  val nino = "CS700100A"

  "getPayments" should {
    "return self assessment payments with utr if successful" in {
      performSuccessfulGET(Future successful HttpResponse(OK, paymentsResponseString()))(mockHttp)
      await(sut.getPayments(Some(utr), None, None, journeyId)).toOption.get.get.payments.size shouldBe 4
    }

    "return self assessment payments if successful" in {
      performSuccessfulGET(Future successful HttpResponse(OK, paymentsResponseString()))(mockHttp)
      await(sut.getPayments(None, Some(utr), Some(TaxTypeEnum.appSelfAssessment), journeyId)).toOption.get.get.payments.size shouldBe 4
    }

    "return simple assessment payments if successful" in {
      performSuccessfulGET(Future successful HttpResponse(OK, paymentsResponseString()))(mockHttp)
      await(sut.getPayments(None, Some("p302Ref"), Some(TaxTypeEnum.appSimpleAssessment), journeyId)).toOption.get.get.payments.size shouldBe 4
    }

    "return None on NotFoundException" in {
      performUnsuccessfulGET(new NotFoundException("not found"))(mockHttp)
      await(sut.getPayments(None, Some(utr), Some(TaxTypeEnum.appSelfAssessment), journeyId)).toOption.get shouldBe None
    }

    "return None on NOT_FOUND response" in {
      performSuccessfulGET(Future successful HttpResponse(NOT_FOUND, ""))(mockHttp)
      await(sut.getPayments(None, Some(utr), Some(TaxTypeEnum.appSelfAssessment), journeyId)).toOption.get shouldBe None
    }

    "return Error on BAD_REQUEST Response" in {
      performSuccessfulGET(Future successful HttpResponse(BAD_REQUEST, ""))(mockHttp)
      await(sut.getPayments(None, Some(utr), Some(TaxTypeEnum.appSelfAssessment), journeyId)).swap
        .getOrElse("") shouldBe "invalid request sent"
    }

    "return Error on Unknown response" in {
      performSuccessfulGET(Future successful HttpResponse(OK, "{unknownValue: \"\"}"))(mockHttp)
      await(sut.getPayments(None, Some(utr), Some(TaxTypeEnum.appSelfAssessment), journeyId)).swap
        .getOrElse("") shouldBe "unable to parse data from payment api"
    }

    "return Error on Exception" in {
      performUnsuccessfulGET(new InternalServerException("Internal Error"))(mockHttp)
      await(sut.getPayments(None, Some(utr), Some(TaxTypeEnum.appSelfAssessment), journeyId)).swap
        .getOrElse("") shouldBe "exception thrown from payment api"
    }

  }

  "getPayCardUrl" should {
    "return URL following successful response" in {
      performSuccessfulPOST(Future successful payApiPayByCardResponse)(mockHttp)
      val result = await(sut.getPayByCardUrl(2000, SaUtr("CS700100A"), journeyId))
      result.nextUrl shouldBe "https://www.staging.tax.service.gov.uk/pay/initiate-journey?traceId=83303543"
    }
    "return an error following error response" in {
      performUnsuccessfulPOST(new BadRequestException("Bad Request"))(mockHttp)
      intercept[BadRequestException] {
        await(sut.getPayByCardUrl(2000, SaUtr("CS700100A"), journeyId))
      }
    }
  }

  "getPayByCardUrlSimpleAssessment" should {
    "return URL following successful response" in {
      performSuccessfulPOST(Future successful payApiPayByCardResponse)(mockHttp)
      val result = await(sut.getPayByCardUrlSimpleAssessment(2000, nino, SaUtr("CS700100A").value, 2023, journeyId))
      result.nextUrl shouldBe "https://www.staging.tax.service.gov.uk/pay/initiate-journey?traceId=83303543"
    }
    "return an error following error response" in {
      performUnsuccessfulPOST(new BadRequestException("Bad Request"))(mockHttp)
      intercept[BadRequestException] {
        await(sut.getPayByCardUrlSimpleAssessment(2000, nino, SaUtr("CS700100A").value, 2023, journeyId))
      }
    }
  }
}
