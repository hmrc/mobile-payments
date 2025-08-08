mobile-payments
=============================================

- Fetch the list of supported banks.
- Select a bank for a given session ID.
- Fetch the payment status for a given session ID.
- Create a payment URL for a given session ID.
- Update a payment URL for a given session ID.
- Fetch the payment URL consumed status for a given session ID.
- Fetch self assessment payments made within the last 14 days for the given UTR.
- Get a Web URL to complete the payment journey online with a pre-populated amount
- Create a session.
- Fetch a session for a given session ID.
- Set the email for a session given a session ID.
- Clear the email for a session given a session ID.

Requirements
------------

Please note it is mandatory to supply an `Accept` HTTP header to all below services with the
value ```application/vnd.hmrc.1.0+json```.


## Development Setup
- Run locally: `sbt run` which runs on port `8262` by default
- Run with test endpoints: `sbt 'run -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes'`

##  Service Manager Profiles
The service can be run locally from Service Manager, using the following profiles:

| Profile Details                 | Command                                                                                                                                                                                     |
|---------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| MOBILE_PAYMENTS_ALL             | sm2 --start MOBILE_PAYMENTS_ALL                                                       |


## Run Tests
- Run Unit Tests:  `sbt test`
- Run Integration Tests: `sbt it:test`
- Run Unit and Integration Tests: `sbt test it:test`
- Run Unit and Integration Tests with coverage report: `sbt clean compile coverage test IntegrationTest/sbtsbttest coverageReport dependencyUpdates`

API
---

| *Task*                                           | *Supported Methods* | *Description*                                                                                                                 |
|--------------------------------------------------|---------------------|-------------------------------------------------------------------------------------------------------------------------------|
| ```/banks```                                     | GET                 | Fetch the list of supported banks. [More...](app/docs/get-banks.md)                                                           |
| ```/banks/:sessionDataId```                      | POST                | Select a bank for a given session ID. [More...](app/docs/post-bank.md)                                                        |
| ```/payments/pay-by-card```                      | POST                | Get a Web URL to complete the payment journey online with a pre-populated amount [More...](app/docs/post-pay-by-card.md)      |
| ```/payments/:sessionDataId```                   | GET                 | Fetch the payment status for a given session ID. [More...](app/docs/get-payment.md)                                           |
| ```/payments/:sessionDataId```                   | POST                | Create a payment URL for a given session ID. [More...](app/docs/post-payment.md)                                              |
| ```/payments/:sessionDataId```                   | PUT                 | Update a payment URL for a given session ID. [More...](app/docs/put-payment.md)                                               |
| ```/payments/:sessionDataId/url-consumed```      | GET                 | Fetch the payment URL consumed status for a given session ID. [More...](app/docs/get-payment-url-consumed.md)                 |
| ```/payments/latest-payments```                  | POST                | Fetch payments made within the last 14 days for the given tax type and reference. [More...](app/docs/post-latest-payments.md) |
| ```/payments/pay-by-card/:utr```                 | POST                | Get a Web URL to complete the payment journey online with a pre-populated amount. [More...](app/docs/post-pay-by-card.md)     |
| ```/sessions```                                  | POST                | Create a session. [More...](app/docs/post-session.md)                                                                         |
| ```/sessions/:sessionDataId```                   | GET                 | Fetch a session for a given session ID. [More...](app/docs/get-session.md)                                                    |
| ```/sessions/:sessionDataId/set-email```         | POST                | Set the email for a session given a session ID. [More...](app/docs/set-email.md)                                              |
| ```/sessions/:sessionDataId/set-future-date```   | POST                | Set the futureDate for a session given a session ID. [More...](app/docs/set-future-date.md)                                   |
| ```/sessions/:sessionDataId/clear-future-date``` | DELETE              | Clear the futureDate for a session given a session ID. [More...](app/docs/clear-future-date.md)                               |
| ```/sessions/:sessionDataId/clear-email```       | DELETE              | Clear the email for a session given a session ID. [More...](app/docs/clear-email.md)                                          |

Shuttered
---------
Shuttering of this service is handled by [mobile-shuttering](https://github.com/hmrc/mobile-shuttering)

Sandbox
---------
Most of the above endpoints are accessible on sandbox with `/sandbox` prefix on each endpoint, e.g.:

```json
    GET /sandbox/banks
```

To trigger the sandbox endpoints locally, use the "X-MOBILE-USER-ID" header with one of the following values:
208606423740 or 167927702220

To test different scenarios, add a header "SANDBOX-CONTROL" to specify the appropriate status code and return payload. See each linked file for details:

| *Task*                                           | *Supported Methods* | *Description*                                                                                         |
|--------------------------------------------------|---------------------|-------------------------------------------------------------------------------------------------------|
| ```sandbox/banks```                                     | GET                 | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/get-banks.md)                |
| ```sandbox/banks/:sessionDataId```                      | POST                | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/post-bank.md)                |
| ```sandbox/payments/pay-by-card```                      | POST                | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/post-pay-by-card.md)         |
| ```sandbox/payments/:sessionDataId```                   | GET                 | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/get-payment.md)              |
| ```sandbox/payments/:sessionDataId```                   | POST                | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/post-payment.md)             |
| ```sandbox/payments/:sessionDataId```                   | PUT                 | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/put-payment.md)              |
| ```sandbox/payments/:sessionDataId/url-consumed```      | GET                 | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/get-payment-url-consumed.md) |
| ```sandbox/payments/latest-payments```                  | POST                | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/post-latest-payments.md)     |
| ```sandbox/payments/pay-by-card/:utr```                 | POST                | No sandbox endpoint                                                                                   |
| ```sandbox/sessions```                                  | POST                | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/post-session.md)             |
| ```sandbox/sessions/:sessionDataId```                   | GET                 | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/get-session.md)              |
| ```sandbox/sessions/:sessionDataId/set-email```         | POST                | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/set-email.md)                |
| ```sandbox/sessions/:sessionDataId/set-future-date```   | POST                | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/set-future-date.md)          |
| ```sandbox/sessions/:sessionDataId/clear-future-date``` | DELETE              | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/clear-future-date.md)        |
| ```sandbox/sessions/:sessionDataId/clear-email```       | DELETE              | Acts as a stub for the related live endpoint. [More...](app/docs/sandbox/clear-email.md)              |


# Definition

API definition for the service will be available under `/api/definition` endpoint. See definition
in `/conf/api-definition.json` for the format.

### License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")