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

package uk.gov.hmrc.mobilepayments.controllers.handlers

import play.api.Logger
import play.api.http.Status._
import play.api.libs.json._
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.mobilepayments.controllers.errors.{ErrorResponse, MalformedRequest, ServiceResponseError}
import uk.gov.hmrc.serviceResponse.ServiceResponse

import scala.concurrent.{ExecutionContext, Future}

trait ErrorHandler {

  val logger: Logger = Logger(this.getClass)

  def errorHandler(serviceResult: ServiceResponse[Result])(implicit ec: ExecutionContext): Future[Result] =
    serviceResult.map {
      case Right(result)              => result
      case Left(serviceResponseError) => toResult(serviceResponseError)
    }

  def toResult(error: ServiceResponseError): Result =
    error match {
      case e: MalformedRequest =>
        logger.error(e.message)
        BadRequest(
          Json.toJson(
            ErrorResponse(BAD_REQUEST, "The request is incomplete or malformed")
          )
        )
      case e =>
        logger.error(e.message)
        InternalServerError(
          Json.toJson(ErrorResponse(INTERNAL_SERVER_ERROR, "The request is incomplete"))
        )
    }
}
