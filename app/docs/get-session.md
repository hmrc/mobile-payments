Get Banks
----
### Fetch a session for a given session ID.

* **URL**

  `/banks`

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
  "amount": 125.64,
  "state": "BankSelected",
  "bankId": "some-bank-id",
  "createdOn" : "2021-11-03T10:15:30",
  "saUtr": "CS700100A"
}
```

* **Error Responses:**

    * **Code:** 401 UNAUTHORIZED <br/>
      **Content:** `{"code":"UNAUTHORIZED","message":"Bearer token is missing or not authorized for access"}`

    * **Code:** 404 NOT_FOUND <br/>

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`

  OR when a user does not exist or server failure

    * **Code:** 500 INTERNAL_SERVER_ERROR <br/>




