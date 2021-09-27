/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.mobilepayments.common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import eu.timepit.refined.auto._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import play.api.test.DefaultAwaitTimeout
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

import scala.concurrent.ExecutionContext

trait BaseSpec extends WordSpec with MockFactory with Matchers with DefaultAwaitTimeout {
  implicit lazy val ec:           ExecutionContext  = scala.concurrent.ExecutionContext.Implicits.global
  implicit lazy val hc:           HeaderCarrier     = HeaderCarrier()
  implicit lazy val system:       ActorSystem       = ActorSystem()
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()

  val journeyId: JourneyId = "13345a9d-0958-4931-ae83-5a36e4ccd979"
}
