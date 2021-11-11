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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BodyParser, ControllerComponents}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilepayments.controllers.ControllerChecks
import uk.gov.hmrc.mobilepayments.controllers.action.AccessControl
import uk.gov.hmrc.mobilepayments.controllers.errors.{ErrorHandling, JsonHandler}
import uk.gov.hmrc.mobilepayments.domain.dto.request.CreatePaymentRequest
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId
import uk.gov.hmrc.mobilepayments.services.{AuditService, OpenBankingService, ShutteringService}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter.fromRequest

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class LivePaymentController @Inject() (
  override val authConnector:                                   AuthConnector,
  @Named("controllers.confidenceLevel") override val confLevel: Int,
  cc:                                                           ControllerComponents,
  openBankingService:                                           OpenBankingService,
  shutteringService:                                            ShutteringService,
  auditService:                                                 AuditService
)(implicit val executionContext:                                ExecutionContext)
    extends BackendController(cc)
    with PaymentController
    with AccessControl
    with ControllerChecks
    with ErrorHandling
    with JsonHandler {

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.anyContent

  override val app: String = "Payment-Controller"

  def createPayment(journeyId: JourneyId): Action[JsValue] =
    validateAcceptWithAuth(acceptHeaderValidationRules).async(parse.json) { implicit request =>
      implicit val hc: HeaderCarrier = fromRequest(request)
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          withErrorWrapper {
            withValidJson[CreatePaymentRequest] { createPaymentRequest =>
              openBankingService
                .initiatePayment(
                  createPaymentRequest.amount,
                  createPaymentRequest.bankId,
                  createPaymentRequest.saUtr,
                  journeyId
                )
                .map { response =>
                  auditService.sendPaymentEvent(
                    createPaymentRequest.amount,
                    createPaymentRequest.saUtr,
                    journeyId.toString()
                  )
                  Ok(Json.toJson(response))
                }
            }
          }
        }
      }
    }

  def getPaymentStatus(
    sessionDataId: String,
    journeyId:     JourneyId
  ): Action[AnyContent] =
    validateAcceptWithAuth(acceptHeaderValidationRules).async { implicit request =>
      implicit val hc: HeaderCarrier = fromRequest(request)
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          withErrorWrapper {
            openBankingService
              .getPaymentStatus(sessionDataId, journeyId)
              .map { response =>
                Ok(Json.toJson(response))
              }
          }
        }
      }
    }
}
