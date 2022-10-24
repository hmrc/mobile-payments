Post Pay By Card
----

### Get a URL to complete payment journey on web with pre-populated amount

* **URL**

  `/payments/pay-by-card/:utr`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Path Variables**

  **Required:**

  `/:utr`

  the UTR of the user

* **Body**

  **Required:**

  `amountInPence`
  the amount to be pre-populated on the web

```json
{
  "amountInPence": 120000
}
```

* **Success Responses:**

  * **Code:** 200 <br />
    **Content:** Web url

```json
{
  "payByCardUrl" : "/pay/choose-a-way-to-pay?traceId=12345678"
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




