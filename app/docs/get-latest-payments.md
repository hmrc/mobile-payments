Get Latest Payments
----

### Fetch self assessment payments made within the last 14 days for the given UTR.

* **URL**

  `/payments/:utr/latest-payments`

* **Method:**

  `GET`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact
* 
* **Path Variables**

  **Required:**

  `/:utr`

  the UTR of the user

* **Success Responses:**

    * **Code:** 200 <br />
      **Content:** Latest Payments

```json
{
  "payments": [
    {
      "amountInPence": 12000,
      "date": "2022-06-01"
    },
    {
      "amountInPence": 74000,
      "date": "2022-06-07"
    }
  ]
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




