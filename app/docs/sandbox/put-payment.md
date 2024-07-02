Update Payment
----

### Update a payment URL for a given session ID.

* **URL**

  `sandbox/payments/:sessionDataId`

* **Method:**

  `PUT`

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
      **Content:** Payment URL data

```json
{
  "paymentUrl": "https://qa.tax.service.gov.uk/mobile-payments-frontend/sandbox/result/open-banking"
}
```

`paymentUrl`
a URL that the client app will navigate to in order to process the payment.

* **Error Responses:**

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`




