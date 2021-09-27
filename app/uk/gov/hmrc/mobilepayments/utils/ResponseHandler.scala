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

package uk.gov.hmrc.mobilepayments.utils

import play.api.Logger
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.mobilepayments.controllers.errors.{GenericError, UpstreamError}
import uk.gov.hmrc.serviceResponse.ServiceResponse

import scala.concurrent.{ExecutionContext, Future}

trait ResponseHandler {
  private val logger: Logger = Logger(this.getClass)

  def withHandledResponse[T](
    response:    Future[T],
    serviceName: String
  )(implicit ex: ExecutionContext
  ): ServiceResponse[T] =
    response
      .map { response =>
        Right(response)
      }
      .recover {
        case error: UpstreamErrorResponse =>
          logger.error(s"Upstream error received from $serviceName:\n ${error.message}")
          Left(UpstreamError(error.message))

        case error =>
          logger.error(s"Generic error received from $serviceName:\n ${error.getMessage}")
          Left(GenericError(error.getMessage))
      }
}
