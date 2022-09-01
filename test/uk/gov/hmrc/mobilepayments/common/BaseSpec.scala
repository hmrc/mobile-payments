/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.mobilepayments.common

import akka.actor.ActorSystem
import eu.timepit.refined.auto._
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

import scala.concurrent.ExecutionContext

trait BaseSpec extends AnyWordSpec with MockFactory with Matchers with DefaultAwaitTimeout {
  implicit lazy val ec:     ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit lazy val hc:     HeaderCarrier    = HeaderCarrier(sessionId = Some(SessionId("13345a9d-0958-4931-ae83-5a36e4ccd979")))
  implicit lazy val system: ActorSystem      = ActorSystem()

  val journeyId: JourneyId = "13345a9d-0958-4931-ae83-5a36e4ccd979"

  protected val sandboxHeader:    (String, String) = "X-MOBILE-USER-ID" -> "208606423740"
  protected val contentHeader:    (String, String) = "Content-Type"     -> "application/json"
  protected val acceptJsonHeader: (String, String) = "Accept"           -> "application/vnd.hmrc.1.0+json"
}
