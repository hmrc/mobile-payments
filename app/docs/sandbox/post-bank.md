Post Bank
----

### Select a bank for a given session ID.

* **URL**

  `sandbox/banks/:sessionDataId`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

  **Required:**

  `bankId`
  the identifier of the chosen bank.

```json
{
  "bankId": "asd-123"
}
```

* **Success Responses:**

    * **Code:** 201

* **Error Responses:**

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`
