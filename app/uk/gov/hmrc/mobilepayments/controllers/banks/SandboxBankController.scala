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

package uk.gov.hmrc.mobilepayments.controllers.banks

import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.api.controllers.HeaderValidator
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.mobilepayments.controllers.ControllerChecks
import uk.gov.hmrc.mobilepayments.controllers.errors.JsonHandler
import uk.gov.hmrc.mobilepayments.domain.dto.request.SelectBankRequest
import uk.gov.hmrc.mobilepayments.domain.dto.response.BanksResponse
import uk.gov.hmrc.mobilepayments.domain.types.JourneyId
import uk.gov.hmrc.mobilepayments.services.ShutteringService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SandboxBankController @Inject() (
  cc:                            ControllerComponents,
  shutteringService:             ShutteringService
)(implicit val executionContext: ExecutionContext)
    extends BackendController(cc)
    with BankController
    with ControllerChecks
    with HeaderValidator
    with FileResource
    with JsonHandler {

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.anyContent

  def getBanks(journeyId: JourneyId): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          Future successful Ok(readData(resource = "sandbox-banks-response.json"))
        }
      }
    }

  override def selectBank(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[JsValue] =
    validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          withValidJson[SelectBankRequest] { _ =>
            Future successful Created
          }
        }
      }
    }

  private def readData(resource: String): JsValue =
    toJson(
      Json
        .parse(
          findResource(s"/resources/mobilepayments/$resource")
            .getOrElse(throw new IllegalArgumentException("Resource not found!"))
        )
        .as[BanksResponse]
    )
}
