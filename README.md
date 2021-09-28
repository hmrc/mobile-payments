
mobile-payments
=============================================

- Return the list of banks that are supported by open banking.

Requirements
------------

Please note it is mandatory to supply an `Accept` HTTP header to all below services with the value ```application/vnd.hmrc.1.0+json```.

API
---

| *Task* | *Supported Methods* | *Description* |
|--------|----|----|
| ```/mobile-payments/banks``` | GET | Fetch the list of supported banks. [More...](docs/banks.md)|

# Sandbox
To trigger the sandbox endpoints locally, use the "X-MOBILE-USER-ID" header with one of the following values:
208606423740 or 167927702220

To test different scenarios, add a header "SANDBOX-CONTROL" with one of the following values:

| *Value* | *Description* |
|--------|----|

# Definition
API definition for the service will be available under `/api/definition` endpoint.
See definition in `/conf/api-definition.json` for the format.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")