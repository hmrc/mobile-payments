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

package uk.gov.hmrc.mobilepayments.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mobilepayments.connectors.OpenBankingConnector
import uk.gov.hmrc.mobilepayments.domain.BanksResponse
import uk.gov.hmrc.mobilepayments.domain.types.ModelTypes.JourneyId

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenBankingService @Inject() (connector: OpenBankingConnector) {

  def getBanks(
    journeyId:                 JourneyId
  )(implicit executionContext: ExecutionContext,
    headerCarrier:             HeaderCarrier
  ): Future[BanksResponse] = connector.getBanks(journeyId)
}
