Get Payment Status
----

### Fetch the payment status for a given session ID.

* **URL**

  `/payments/:sessionDataId`

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
      **Content:** Payment status data

```json
{
  "status": "Authorised"
}
```

### Possible Status Values
* Initial 
* AwaitingAuthorization
* Authorised
* Verified
* Completed
* Canceled
* Failed
* Rejected
* Abandoned
<br /><br />
* **Error Responses:**

    * **Code:** 401 UNAUTHORIZED <br/>
      **Content:** `{"code":"UNAUTHORIZED","message":"Bearer token is missing or not authorized for access"}`

    * **Code:** 404 NOT_FOUND <br/>

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`

  OR when a user does not exist or server failure

    * **Code:** 500 INTERNAL_SERVER_ERROR <br/>




