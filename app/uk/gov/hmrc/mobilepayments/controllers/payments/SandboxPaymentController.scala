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

package uk.gov.hmrc.mobilepayments.controllers.payments

import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import uk.gov.hmrc.api.controllers.HeaderValidator
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.mobilepayments.controllers.ControllerChecks
import uk.gov.hmrc.mobilepayments.controllers.errors.{ErrorHandling, JsonHandler}
import uk.gov.hmrc.mobilepayments.domain.dto.response.{LatestPaymentsResponse, PayByCardResponse, PaymentStatusResponse, UrlConsumedResponse}
import uk.gov.hmrc.mobilepayments.domain.types.JourneyId
import uk.gov.hmrc.mobilepayments.models.openBanking.response.InitiatePaymentResponse
import uk.gov.hmrc.mobilepayments.services.ShutteringService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class SandboxPaymentController @Inject() (
  @Named("sandboxOpenBankingPaymentUrl") sandboxOpenBankingPaymentUrl: String,
  cc: ControllerComponents,
  shutteringService: ShutteringService
)(implicit val executionContext: ExecutionContext)
    extends BackendController(cc)
    with PaymentController
    with ControllerChecks
    with HeaderValidator
    with ErrorHandling
    with JsonHandler
    with FileResource {

  override def parser: BodyParser[AnyContent] = controllerComponents.parsers.anyContent

  override def createPayment(
    sessionDataId: String,
    journeyId: JourneyId
  ): Action[AnyContent] = paymentUrl(journeyId)

  override def updatePayment(
    sessionDataId: String,
    journeyId: JourneyId
  ): Action[AnyContent] = paymentUrl(journeyId)

  override def urlConsumed(
    sessionDataId: String,
    journeyId: JourneyId
  ): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          Future successful Ok(toJson(UrlConsumedResponse(true)))
        }
      }
    }

  def getPaymentStatus(
    sessionDataId: String,
    journeyId: JourneyId
  ): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          withErrorWrapper {
            Future successful Ok(samplePaymentStatusJson)
          }
        }
      }
    }

  def latestPaymentsLegacy(
    utr: String,
    journeyId: JourneyId
  ): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      withErrorWrapper {
        Future successful Ok(sampleLatestPaymentsJson)
      }
    }

  def latestPayments(journeyId: JourneyId): Action[JsValue] =
    validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
      withErrorWrapper {
        Future successful Ok(sampleLatestPaymentsJson)
      }
    }

  override def getPayByCardURL(
    journeyId: JourneyId
  ): Action[JsValue] =
    validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
      withErrorWrapper {
        Future successful Ok(Json.toJson(PayByCardResponse("/")))
      }
    }

  private def paymentUrl(journeyId: JourneyId) =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      shutteringService.getShutteringStatus(journeyId).flatMap { shuttered =>
        withShuttering(shuttered) {
          Future.successful(Ok(toJson(InitiatePaymentResponse(paymentUrl = sandboxOpenBankingPaymentUrl))))
        }
      }
    }

  private def samplePaymentStatusJson: JsValue =
    toJson(
      Json
        .parse(
          findResource(path = s"/resources/mobilepayments/sandbox-payment-status-response.json")
            .getOrElse(throw new IllegalArgumentException("Resource not found!"))
        )
        .as[PaymentStatusResponse]
    )

  private def sampleLatestPaymentsJson: JsValue =
    toJson(
      Json
        .parse(
          findResource(path = s"/resources/mobilepayments/sandbox-latest-payments-response.json")
            .getOrElse(throw new IllegalArgumentException("Resource not found!"))
            .replace("<DATE1>", LocalDate.now().minusDays(10).toString)
            .replace("<DATE2>", LocalDate.now().minusDays(1).toString)
        )
        .as[LatestPaymentsResponse]
    )

  override val app: String = "Sandbox Payment Controller"

}
