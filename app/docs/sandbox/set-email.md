Post Email
----

### Set an email for a given session ID.

* **URL**

  `sandbox/sessions/:sessionDataId/set-email`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

  **Required:**

  `email`
  the email to assign to the session.

```json
{
  "email": "test@test.com"
}
```

* **Success Responses:**

    * **Code:** 201

* **Error Responses:**
    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`




