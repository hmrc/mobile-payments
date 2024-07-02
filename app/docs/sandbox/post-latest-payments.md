Get Latest Payments
----

### Fetch self assessment payments made within the last 14 days for the given UTR.

* **URL**

  `sandbox/payments/latest-payments`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

  **Required:**

  `taxType`
  the type of tax to return payments for - Currently "appSelfAssessment" and "appSimpleAssessment" are supported

  `reference`
  the reference used for the payments

```json
{
  "taxType": "appSelfAssessment",
  "reference": "1122334455"
}
```

* **Success Responses:**

    * **Code:** 200 <br />
      **Content:** Latest Payments

```json
{
  "payments": [
    {
      "amountInPence": 12000,
      "date": "2024-06-18"
    },
    {
      "amountInPence": 74000,
      "date": "2024-06-27"
    }
  ]
}
```

* **Error Responses:**

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`




