Get Session
----

### Fetch a session for a given session ID.

* **URL**

  `sandbox/sessions/:sessionDataId`

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
*
* **Success Responses:**

    * **Code:** 200 <br />
      **Content:** Bank data

```json
{
  "sessionDataId": "51cc67d6-21da-11ec-9621-0242ac130002",
  "amountInPence": 12564,
  "bankId": "obie-barclays-personal",
  "state": "BankSelected",
  "createdOn": "2021-11-03T10:15:30",
  "reference": "1555369056K",
  "origin": "AppSa",
  "maybeFutureDate": "2024-02-28"
}
```

<br />

* **Error Responses:**

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`





