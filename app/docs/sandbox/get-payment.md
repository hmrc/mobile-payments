Get Payment Status
----

### Fetch the payment status for a given session ID.

* **URL**

  `sandbox/payments/:sessionDataId`

* **Method:**

  `GET`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Path Variables**

  **Required:**

  `/:sessionDataId`

  the ID of the current session

* **Success Responses:**

  * **Code:** 200 <br />
    **Content:** Payment status data

  To test different scenarios, add a header "SANDBOX-CONTROL" with one of the following values:

| *Value*         | *Description*                                                                                    |
|-----------------|--------------------------------------------------------------------------------------------------|
| SUCCESS-PAYMENT | Responds with a session in the "PaymentFinished" state when calling GET /payments/:sessionDataId |

```json
{
  "status": "Completed"
}
```

* **Error Responses:**

  * **Code:** 406 NOT_ACCEPTABLE <br/>
    **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`
