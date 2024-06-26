/*
 * Copyright 2017 HM Revenue & Customs
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

package stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object AuthStub {

  def grantAccess(confidenceLevel: Int = 200): StubMapping =
    stubFor(
      post(urlEqualTo("/auth/authorise"))
        .atPriority(0)
        .withRequestBody(
          equalToJson(
            s"""
               |{
               |  "authorise": [
               |  {
               |      "credentialStrength": "strong"
               |      }, {
               |      "confidenceLevel": $confidenceLevel
               |    }
               |    ],
               |  "retrieve": [
               |    "confidenceLevel",
               |    "allEnrolments"
               |  ]
               |}
          """.stripMargin,
            true,
            false
          )
        )
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(s"""
                         |{
                         |  "allEnrolments": [{
                         |      "key": "IR-SA",
                         |      "identifiers": [{
                         |        "key": "UTR",
                         |        "value": "1122334455"
                         |      }],
                         |      "state": "Activated"
                         |  }],
                         |  "confidenceLevel": $confidenceLevel
                         |}
          """.stripMargin)
        )
    )

  def authorisationRejected(confidenceLevel: Int = 200): StubMapping =
    stubFor(
      post(urlEqualTo("/auth/authorise"))
        .atPriority(0)
        .withRequestBody(
          equalToJson(
            s"""
               |{
               |  "authorise": [
               |  {
               |      "credentialStrength": "strong"
               |      }, {
               |      "confidenceLevel": $confidenceLevel
               |    }
               |    ],
               |  "retrieve": [
               |    "confidenceLevel",
               |    "allEnrolments"
               |  ]
               |}
          """.stripMargin,
            true,
            false
          )
        )
        .willReturn(
          aResponse()
            .withStatus(401)
            .withBody(s"""
                         |{
                         |  "error": "unauthorized"
                         |}
          """.stripMargin)
        )
    )

  def getNinoFromAuth(nino: String = "CS700100A"): StubMapping =
    stubFor(
      post(urlEqualTo("/auth/authorise"))
        .atPriority(0)
        .withRequestBody(
          equalToJson(
            s"""
               |{
               |  "authorise": [],
               |  "retrieve": [
               |    "nino"
               |  ]
               |}
          """.stripMargin,
            true,
            false
          )
        )
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(s"""
                         |{
                         |  "nino": "$nino"
                         |}
          """.stripMargin)
        )
    )
}
