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

package uk.gov.hmrc.mobilepayments.mocks

import izumi.reflect.Tag
import org.scalamock.scalatest.MockFactory
import play.api.libs.ws.BodyWritable
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

trait ConnectorStub extends MockFactory {

  def performGET[O](
    response:           Future[O]
  )(implicit http:      HttpClientV2,
    mockRequestBuilder: RequestBuilder
  ): Unit = {
    (http
      .get(_: URL)(_: HeaderCarrier))
      .expects(*, *)
      .returns(mockRequestBuilder)

    (mockRequestBuilder
      .execute[O](_: HttpReads[O], _: ExecutionContext))
      .expects(*, *)
      .returns(response)
  }

  def performSuccessfulPOST[T](
    response:           Future[T]
  )(implicit http:      HttpClientV2,
    mockRequestBuilder: RequestBuilder
  ): Unit = {
    (http
      .post(_: URL)(_: HeaderCarrier))
      .expects(*, *)
      .returns(mockRequestBuilder)

    (mockRequestBuilder
      .withBody(_: T)(_: BodyWritable[T], _: Tag[T], _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(mockRequestBuilder)

    (mockRequestBuilder
      .setHeader(_: (String, String)))
      .expects(*)
      .returns(mockRequestBuilder)

    (mockRequestBuilder
      .execute[T](_: HttpReads[T], _: ExecutionContext))
      .expects(*, *)
      .returns(response)
  }

  def performPOST[T](
    response:           Future[T]
  )(implicit http:      HttpClientV2,
    mockRequestBuilder: RequestBuilder
  ): Unit = {
    (http
      .post(_: URL)(_: HeaderCarrier))
      .expects(*, *)
      .returns(mockRequestBuilder)

    (mockRequestBuilder
      .withBody(_: T)(_: BodyWritable[T], _: Tag[T], _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(mockRequestBuilder)

    (mockRequestBuilder
      .execute[T](_: HttpReads[T], _: ExecutionContext))
      .expects(*, *)
      .returns(response)
  }

  def performDELETE[O](
    response:           Future[O]
  )(implicit http:      HttpClientV2,
    mockRequestBuilder: RequestBuilder
  ): Unit = {
    (http
      .delete(_: URL)(_: HeaderCarrier))
      .expects(*, *)
      .returns(mockRequestBuilder)
    (mockRequestBuilder
      .execute[O](_: HttpReads[O], _: ExecutionContext))
      .expects(*, *)
      .returns(response)
  }

}
