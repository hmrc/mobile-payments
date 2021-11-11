Create Payment
----
Returns the InitiatePaymentResponse object.

* **URL**

  `/payments`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

  **Required:**

  `amount`
  a long that represents how much the payment will be for.

  `bankId`
  the identifier of the chosen bank.

```json
{
  "amount": 1234, 
  "bankId": "asd-123",
  "saUtr": "CS700100A"
}
```

* **Success Responses:**

    * **Code:** 200 <br />
      **Content:** Payment URL data

```json
{
  "paymentUrl": "https://some-bank.com?param=dosomething",
  "sessionDataId": "51cc67d6-21da-11ec-9621-0242ac130002"
}
```

`paymentUrl`
a URL that the client app will navigate to in order to process the payment.

`sessionDataId`
the session identifier that needs to be persisted by the client throughout the payment journey.

* **Error Responses:**

    * **Code:** 401 UNAUTHORIZED <br/>
      **Content:** `{"code":"UNAUTHORIZED","message":"Bearer token is missing or not authorized for access"}`

    * **Code:** 404 NOT_FOUND <br/>

    * **Code:** 406 NOT_ACCEPTABLE <br/>
      **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`

  OR when a user does not exist or server failure

    * **Code:** 500 INTERNAL_SERVER_ERROR <br/>




