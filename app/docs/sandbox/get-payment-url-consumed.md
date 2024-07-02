Get Payment URL consumed status
----

### Fetch the payment URL consumed status for a given session ID.

* **URL**

  `sandbox/payments/:sessionDataId/url-consumed`

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
      **Content:** Payment URL data

```json
{
  "consumed": true
}
```

`consumed`
a boolean status that indicates whether the payment URL for a given session is still valid.

* **Error Responses:**

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`




