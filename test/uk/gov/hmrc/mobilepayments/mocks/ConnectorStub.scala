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

package uk.gov.hmrc.mobilepayments.mocks

import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Writes
import uk.gov.hmrc.http._

import scala.concurrent.{ExecutionContext, Future}

trait ConnectorStub extends MockFactory {

  def performSuccessfulGET[T](response: Future[T])(implicit http: CoreGet): Unit =
    (
      http
        .GET[T](_: String, _: Seq[(String, String)], _: Seq[(String, String)])(
          _: HttpReads[T],
          _: HeaderCarrier,
          _: ExecutionContext
        )
      )
      .expects(*, *, *, *, *, *)
      .returns(response)

  def performUnsuccessfulGET(response: Exception)(implicit http: CoreGet): Unit =
    (
      http
        .GET[HttpResponse](_: String, _: Seq[(String, String)], _: Seq[(String, String)])(
          _: HttpReads[HttpResponse],
          _: HeaderCarrier,
          _: ExecutionContext
        )
      )
      .expects(*, *, *, *, *, *)
      .returns(Future failed response)
}
