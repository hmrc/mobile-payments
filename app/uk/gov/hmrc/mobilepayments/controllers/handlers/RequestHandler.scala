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
import play.api.libs.json.{JsValue, Reads}
import play.api.mvc.{Request, Result}
import uk.gov.hmrc.serviceResponse.ServiceResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RequestHandler extends ExceptionHandler with ErrorHandler with JsonHandler {

  override val logger: Logger = Logger(this.getClass)

  def withHandledRequest[T](
    f:                T => ServiceResponse[Result]
  )(implicit request: Request[JsValue],
    r:                Reads[T]
  ): Future[Result] =
    exceptionHandler {
      errorHandler {
        withValidJson[T] { request =>
          f(request)
        }
      }
    }
}
