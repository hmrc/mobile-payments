/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.mobilepayments.mocks

import play.api.libs.json.{Format, Json}

case class MockError(
  httpStatusCode: Int,
  error:          String)

object MockError {
  implicit val format: Format[MockError] = Json.format[MockError]
}
