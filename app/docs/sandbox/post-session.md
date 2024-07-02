Post Session
----

### Create a session.

* **URL**

  `sandbox/sessions`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

```json
{
  "amountInPence": 32275,
  "reference": "CS700100A",
  "taxType": "appSelfAssessment"
}
```

* **Success Responses:**

    * **Code:** 200

```json
{
  "sessionDataId": "51cc67d6-21da-11ec-9621-0242ac130002",
  "nextUrl": "www.url.com"
}
```

* **Error Responses:**

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`





