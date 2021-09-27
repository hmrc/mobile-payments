/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.mobilepayments.mocks

import uk.gov.hmrc.http.HttpResponse

trait MockResponses {

  def successfulMockResponse(code: Int): HttpResponse = HttpResponse(code, "success")

  def failedMockResponse(code: Int): HttpResponse = HttpResponse(code, "An error has occurred")

}
