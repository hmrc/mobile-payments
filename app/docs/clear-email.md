Clear Email
----

### Clear the email for a given session ID.

* **URL**

  `/sessions/:sessionDataId/clear-email`

* **Method:**

  `DELETE`

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

    * **Code:** 204 <br />

* **Error Responses:**

    * **Code:** 401 UNAUTHORIZED <br/>
      **Content:** `{"code":"UNAUTHORIZED","message":"Bearer token is missing or not authorized for access"}`

    * **Code:** 404 NOT_FOUND <br/>

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`

  OR when a user does not exist or server failure

    * **Code:** 500 INTERNAL_SERVER_ERROR <br/>




