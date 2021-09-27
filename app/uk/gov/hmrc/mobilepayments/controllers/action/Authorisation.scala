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

package uk.gov.hmrc.mobilepayments.controllers.action

import play.api.Logger
import play.api.http.Status.{NOT_ACCEPTABLE, UNAUTHORIZED}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.api.controllers.{ErrorAcceptHeaderInvalid, HeaderValidator}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{confidenceLevel, credentials, internalId}
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthorisedFunctions, ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.mobilepayments.controllers.errors.{AccountWithLowCL, ErrorResponse}
import uk.gov.hmrc.play.http.HeaderCarrierConverter.fromRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

case class Authority(
  internalId: String,
  ggId:       String)

final case class AuthenticatedRequest[A](
  authority: Option[Authority],
  request:   Request[A])
    extends WrappedRequest(request)

trait Authorisation extends Results with AuthorisedFunctions {

  lazy val requiresAuth:        Boolean               = true
  lazy val lowConfidenceLevel:  AccountWithLowCL      = AccountWithLowCL("Account with insufficient confidence")
  lazy val authIdNotFound:      UpstreamErrorResponse = UpstreamErrorResponse("Account not found!", 401, 401)
  lazy val credentialsNotFound: UpstreamErrorResponse = UpstreamErrorResponse("Credentials not found!", 401, 401)
  val logger:                   Logger                = Logger(this.getClass)

  def invokeAuthBlock[A](
    request: Request[A],
    block:   AuthenticatedRequest[A] => Future[Result]
  ): Future[Result] = {
    implicit val hc: HeaderCarrier = fromRequest(request)

    grantAccess()
      .flatMap { authority =>
        block(AuthenticatedRequest(Some(authority), request))
      }
      .recover {
        case _: uk.gov.hmrc.http.Upstream4xxResponse =>
          logger.info("Unauthorized! Failed to grant access since 4xx response!")
          Unauthorized(Json.toJson(ErrorResponse(UNAUTHORIZED, "UNAUTHORIZED")))

        case _: AccountWithLowCL =>
          logger.info("Unauthorized! Account with low CL!")
          Unauthorized(Json.toJson(ErrorResponse(UNAUTHORIZED, "LOW_CONFIDENCE_LEVEL")))
      }
  }

  def grantAccess()(implicit hc: HeaderCarrier): Future[Authority] =
    authorised(CredentialStrength("strong") and ConfidenceLevel.L200)
      .retrieve(confidenceLevel and internalId and credentials) {
        case _ ~ None ~ _ => throw authIdNotFound
        case _ ~ _ ~ None => throw credentialsNotFound
        case foundConfidenceLevel ~ Some(userId) ~ Some(credentials) =>
          if (foundConfidenceLevel.level < 200) throw lowConfidenceLevel
          if (userId.isEmpty) throw authIdNotFound
          if (credentials.providerId.isEmpty) throw credentialsNotFound
          Future(Authority(userId, credentials.providerId))
      }
}

trait AccessControl extends HeaderValidator with Authorisation {
  outer =>
  def parser: BodyParser[AnyContent]

  def validateAcceptWithAuth(rules: Option[String] => Boolean): ActionBuilder[AuthenticatedRequest, AnyContent] =
    new ActionBuilder[AuthenticatedRequest, AnyContent] {

      override def parser: BodyParser[AnyContent] = outer.parser

      override protected def executionContext: ExecutionContext = outer.executionContext

      def invokeBlock[A](
        request: Request[A],
        block:   AuthenticatedRequest[A] => Future[Result]
      ): Future[Result] =
        if (rules(request.headers.get("Accept"))) {
          if (requiresAuth) invokeAuthBlock(request, block)
          else block(AuthenticatedRequest(None, request))
        } else {
          Future.successful(
            Status(ErrorAcceptHeaderInvalid.httpStatusCode)(
              Json.toJson(ErrorResponse(NOT_ACCEPTABLE, "ACCEPT_HEADER_INVALID"))
            )
          )
        }
    }
}
