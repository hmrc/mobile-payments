Post Session
----

### Create a session.

* **URL**

  `/sessions`

* **Method:**

  `POST`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Body**

Self Assessment valid payload
```json
{
  "amountInPence": 32275,
  "reference": "CS700100A",
  "taxType": "appSelfAssessment"
}
```
Simple Assessment valid payload
```json
{
  "amountInPence": 32275,
  "reference": "CS700100A",
  "taxType": "appSimpleAssessment"
}
```
* **Success Responses:**

    * **Code:** 200

```json
{
  "sessionDataId": "51cc67d6-21da-11ec-9621-0242ac130002"
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




