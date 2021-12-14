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

package uk.gov.hmrc.mobilepayments.controllers.payments

import openbanking.cor.model.response.InitiatePaymentResponse
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.mobilepayments.controllers.ControllerChecks
import uk.gov.hmrc.mobilepayments.controllers.action.AccessControl
import uk.gov.hmrc.mobilepayments.controllers.errors.{ErrorHandling, JsonHandler}
import uk.gov.hmrc.mobilepayments.domain.dto.response.{PaymentStatusResponse, UrlConsumedResponse}
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.services.ShutteringService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SandboxPaymentController @Inject() (
  override val authConnector:                                          AuthConnector,
  @Named("controllers.confidenceLevel") override val confLevel:        Int,
  @Named("sandboxOpenBankingPaymentUrl") sandboxOpenBankingPaymentUrl: String,
  cc:                                                                  ControllerComponents,
  shutteringService:                                                   ShutteringService
)(implicit val executionContext:                                       ExecutionContext)
    extends BackendController(cc)
    with PaymentController
    with ControllerChecks
    with AccessControl
    with ErrorHandling
    with JsonHandler
    with FileResource {

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.anyContent

  override def createPayment(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] = paymentUrl(journeyId)

  override def updatePayment(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] = paymentUrl(journeyId)

  override def urlConsumed(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] =
    validateAcceptWithAuth(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          Future successful Ok(toJson(UrlConsumedResponse(true)))
        }
      }
    }

  def getPaymentStatus(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] =
    validateAcceptWithAuth(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          withErrorWrapper {
            Future successful Ok(samplePaymentStatusJson(resource = "sandbox-payment-status-response.json"))
          }
        }
      }
    }

  private def paymentUrl(journeyId: JourneyId) =
    validateAcceptWithAuth(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          Future successful Ok(toJson(InitiatePaymentResponse(paymentUrl = sandboxOpenBankingPaymentUrl)))
        }
      }
    }

  private def samplePaymentStatusJson(resource: String): JsValue =
    toJson(
      Json
        .parse(
          findResource(path = s"/resources/mobilepayments/$resource")
            .getOrElse(throw new IllegalArgumentException("Resource not found!"))
        )
        .as[PaymentStatusResponse]
    )

  override val app: String = "Sandbox Payment Controller"
}
