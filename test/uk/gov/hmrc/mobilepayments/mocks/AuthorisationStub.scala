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

package uk.gov.hmrc.mobilepayments.mocks

import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthConnector, BearerTokenExpired, ConfidenceLevel, InsufficientEnrolments}
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse}

import scala.concurrent.{ExecutionContext, Future}

trait AuthorisationStub extends MockFactory {

  type GrantAccess     = ConfidenceLevel ~ Option[String] ~ Option[Credentials]
  type GrantAccessNino = Option[String]

  def stubAuthorisationFetchNino(response: GrantAccessNino)(implicit authConnector: AuthConnector) =
    (authConnector
      .authorise(_: Predicate, _: Retrieval[GrantAccessNino])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(Future successful response)

  def stubAuthorisationFetchNinoFailure()(implicit authConnector: AuthConnector) =
    (authConnector
      .authorise(_: Predicate, _: Retrieval[GrantAccessNino])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(Future failed Upstream4xxResponse("503", 503, 503))

  def stubAuthorisationGrantAccess(response: GrantAccess)(implicit authConnector: AuthConnector) =
    (authConnector
      .authorise(_: Predicate, _: Retrieval[GrantAccess])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(Future successful response)

  def stubAuthorisationWithNoActiveSessionException(response: GrantAccessNino)(implicit authConnector: AuthConnector) =
    (authConnector
      .authorise(_: Predicate, _: Retrieval[GrantAccess])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(Future failed BearerTokenExpired())

  def stubAuthorisationWithAuthorisationException(response: GrantAccessNino)(implicit authConnector: AuthConnector) =
    (authConnector
      .authorise(_: Predicate, _: Retrieval[GrantAccess])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returning(Future failed InsufficientEnrolments())
}
