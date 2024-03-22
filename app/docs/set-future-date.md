Post Future Date
----

### Set a future date for a given session ID.

* **URL**

  `/sessions/:sessionDataId/set-future-date`

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
  "maybeFutureDate": "2024-02-28"
}
```

* **Success Responses:**

    * **Code:** 201

* **Error Responses:**

    * **Code:** 401 UNAUTHORIZED <br/>
      **Content:** `{"code":"UNAUTHORIZED","message":"Bearer token is missing or not authorized for access"}`

    * **Code:** 404 NOT_FOUND <br/>

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`

  OR when a user does not exist or server failure

    * **Code:** 500 INTERNAL_SERVER_ERROR <br/>




