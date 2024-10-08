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

package uk.gov.hmrc.mobilepayments.controllers.sessions

import openbanking.cor.model.response.CreateSessionDataResponse
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.api.controllers.HeaderValidator
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.mobilepayments.controllers.ControllerChecks
import uk.gov.hmrc.mobilepayments.controllers.errors.ErrorHandling
import uk.gov.hmrc.mobilepayments.domain.dto.response.SessionDataResponse
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.services.ShutteringService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SandboxSessionController @Inject() (
  cc:                            ControllerComponents,
  shutteringService:             ShutteringService
)(implicit val executionContext: ExecutionContext)
    extends BackendController(cc)
    with SessionController
    with HeaderValidator
    with ControllerChecks
    with ErrorHandling
    with FileResource {

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.anyContent
  override val app:    String                 = "Session-Controller"

  override def createSession(journeyId: JourneyId): Action[JsValue] =
    validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          Future successful Ok(sampleCreateSessionJson)
        }
      }
    }

  override def getSession(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          Future.successful(request.headers.get("SANDBOX-CONTROL") match {
            case Some("SUCCESS-PAYMENT") => Ok(sampleSessionJson("sandbox-session-completed-response"))
            case _                       => Ok(sampleSessionJson("sandbox-session-response"))
          })
        }
      }
    }

  private def sampleCreateSessionJson: JsValue =
    toJson(
      Json
        .parse(
          findResource(path = s"/resources/mobilepayments/sandbox-create-session-response.json")
            .getOrElse(throw new IllegalArgumentException("Resource not found!"))
        )
        .as[CreateSessionDataResponse]
    )

  private def sampleSessionJson(filename: String): JsValue =
    toJson(
      Json
        .parse(
          findResource(path = s"/resources/mobilepayments/$filename.json")
            .getOrElse(throw new IllegalArgumentException("Resource not found!"))
            .replace("<FUTURE_DATE>", LocalDate.now().plusMonths(6).toString)
        )
        .as[SessionDataResponse]
    )

  override def setEmail(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[JsValue] =
    validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
      Future successful Created
    }

  override def setFutureDate(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[JsValue] =
    validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
      Future successful Created
    }

  override def clearFutureDate(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      Future successful NoContent
    }

  override def clearEmail(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      Future successful NoContent
    }
}
