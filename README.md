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

API
---

| *Task*                                           | *Supported Methods* | *Description*                                                                                                                |
|--------------------------------------------------|---------------------|------------------------------------------------------------------------------------------------------------------------------|
| ```/banks```                                     | GET                 | Fetch the list of supported banks. [More...](app/docs/get-banks.md)                                                          |
| ```/banks/:sessionDataId```                      | POST                | Select a bank for a given session ID. [More...](app/docs/post-bank.md)                                                       |
| ```/payments/pay-by-card```                      | POST                | Get a Web URL to complete the payment journey online with a pre-populated amount [More...](app/docs/post-pay-by-card.md)     |
| ```/payments/:sessionDataId```                   | GET                 | Fetch the payment status for a given session ID. [More...](app/docs/get-payment.md)                                          |
| ```/payments/:sessionDataId```                   | POST                | Create a payment URL for a given session ID. [More...](app/docs/post-payment.md)                                             |
| ```/payments/:sessionDataId```                   | PUT                 | Update a payment URL for a given session ID. [More...](app/docs/put-payment.md)                                              |
| ```/payments/:sessionDataId/url-consumed```      | GET                 | Fetch the payment URL consumed status for a given session ID. [More...](app/docs/get-payment-url-consumed.md)                |
| ```/payments/latest-payments```                  | POST                | Fetch payments made within the last 14 days for the given tax ty[e and reference. [More...](app/docs/post-latest-payments.md) |
| ```/payments/pay-by-card/:utr```                 | POST                | Get a Web URL to complete the payment journey online with a pre-populated amount. [More...](app/docs/post-pay-by-card.md)    |
| ```/sessions```                                  | POST                | Create a session. [More...](app/docs/post-session.md)                                                                        |
| ```/sessions/:sessionDataId```                   | GET                 | Fetch a session for a given session ID. [More...](app/docs/get-session.md)                                                   |
| ```/sessions/:sessionDataId/set-email```         | POST                | Set the email for a session given a session ID. [More...](app/docs/set-email.md)                                             |
| ```/sessions/:sessionDataId/set-future-date```   | POST                | Set the futureDate for a session given a session ID. [More...](app/docs/set-future-date.md)                                  |
| ```/sessions/:sessionDataId/clear-future-date``` | DELETE              | Clear the futureDate for a session given a session ID. [More...](app/docs/clear-future-date.md)                              |
| ```/sessions/:sessionDataId/clear-email```       | DELETE              | Clear the email for a session given a session ID. [More...](app/docs/clear-email.md)                                         |

Shuttered
---------
Shuttering of this service is handled by [mobile-shuttering](https://github.com/hmrc/mobile-shuttering)

Sandbox
---------
To trigger the sandbox endpoints locally, use the "X-MOBILE-USER-ID" header with one of the following values:
208606423740 or 167927702220

To test different scenarios, add a header "SANDBOX-CONTROL" with one of the following values:

| *Value*         | *Description*                                                                                    |
|-----------------|--------------------------------------------------------------------------------------------------|
| SUCCESS-PAYMENT | Responds with a session in the "PaymentFinished" state when calling GET /payments/:sessionDataId |

# Definition

API definition for the service will be available under `/api/definition` endpoint. See definition
in `/conf/api-definition.json` for the format.

### License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")