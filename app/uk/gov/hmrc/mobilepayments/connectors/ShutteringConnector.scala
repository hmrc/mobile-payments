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

package uk.gov.hmrc.mobilepayments.connectors

import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import play.api.Logger
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.mobilepayments.domain.Shuttering
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ShutteringConnector @Inject() (
  http:                                   HttpClientV2,
  @Named("mobile-shuttering") serviceUrl: String) {

  val logger: Logger = Logger(this.getClass)

  def getShutteringStatus(
    journeyId:              JourneyId
  )(implicit headerCarrier: HeaderCarrier,
    ex:                     ExecutionContext
  ): Future[Shuttering] =
    http
      .get(url"$serviceUrl/mobile-shuttering/service/mobile-open-banking/shuttered-status?journeyId=$journeyId")
      .execute[Shuttering]
      .recover {
        case e: UpstreamErrorResponse =>
          logger.warn(s"Internal Server Error received from mobile-shuttering:\n $e \nAssuming unshuttered.")
          Shuttering.shutteringDisabled

        case e =>
          logger.warn(s"Call to mobile-shuttering failed:\n $e \nAssuming unshuttered.")
          Shuttering.shutteringDisabled
      }
}
