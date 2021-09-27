/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.mobilepayments.mocks

import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Writes
import uk.gov.hmrc.http._

import scala.concurrent.{ExecutionContext, Future}

trait ConnectorStub extends MockFactory {

  def performSuccessfulPOST[T](
    response:      Future[HttpResponse]
  )(implicit http: CorePost): Unit =
    (
      http
        .POST[T, HttpResponse](_: String, _: T, _: Seq[(String, String)])(
          _: Writes[T],
          _: HttpReads[HttpResponse],
          _: HeaderCarrier,
          _: ExecutionContext
        )
      )
      .expects(*, *, *, *, *, *, *)
      .returns(response)

  def performUnsuccessfulPOST[T](
    response:      Exception
  )(implicit http: CorePost): Unit =
    (
      http
        .POST[T, HttpResponse](_: String, _: T, _: Seq[(String, String)])(
          _: Writes[T],
          _: HttpReads[HttpResponse],
          _: HeaderCarrier,
          _: ExecutionContext
        )
      )
      .expects(*, *, *, *, *, *, *)
      .returns(Future failed response)
}
