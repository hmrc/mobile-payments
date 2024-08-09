Post Future Date
----

### Set a future date for a given session ID.

* **URL**

  `sandbox/sessions/:sessionDataId/set-future-date`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

  **Required:**

  `maybeFutureDate`
  the future date to assign to the session.

```json
{
  "maybeFutureDate": "2030-01-01"
}
```

* **Success Responses:**

    * **Code:** 201

* **Error Responses:**
    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`
