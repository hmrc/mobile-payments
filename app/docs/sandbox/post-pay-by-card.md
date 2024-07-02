Post Pay By Card
----

### Get a URL to complete payment journey on web with pre-populated amount

* **URL**

  `sandbox/payments/pay-by-card/:utr`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

  **Required:**

  `amountInPence`
  the amount to be pre-populated on the web

```json
{
  "amountInPence": 120000,
  "taxType": "appSelfAssessment",
  "reference": "123456789"
}
```

* **Success Responses:**

  * **Code:** 200 <br />
    **Content:** Web url

```json
{
  "payByCardUrl" : "/"
}
```

* **Error Responses:**

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`




