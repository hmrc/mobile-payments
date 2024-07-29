Get Banks
----

### Fetch the list of supported banks.

* **URL**

  `/banks`

* **Method:**

  `GET`

* **URL Params**

  **Required:**

  `journeyId=[String]`

  a string which is included for journey tracking purposes but has no functional impact

* **Success Responses:**

    * **Code:** 200 <br />
      **Content:** Bank data

```json
{
  "data": [
    {
      "bankGroupName": "Barclays",
      "bankGroupNameFormatted": "Barclays",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-barclays-business",
          "name": "Barclays Personal",
          "friendlyName": "Barclays Personal",
          "iconUrl": "https://logo.com",
          "group": "Barclays",
          "hasFdp": true
        },
        {
          "bankId": "obie-barclays-business",
          "name": "Barclays Business",
          "friendlyName": "Barclays Business",
          "iconUrl": "https://logo.com",
          "group": "Barclays",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "Lloyds",
      "bankGroupNameFormatted": "Lloyds",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-lloyds-personal",
          "name": "Lloyds Personal",
          "friendlyName": "Lloyds Personal",
          "iconUrl": "https://logo.com",
          "group": "Lloyds",
          "hasFdp": true
        },
        {
          "bankId": "obie-lloyds-business",
          "name": "Lloyds Business",
          "friendlyName": "Lloyds Business",
          "iconUrl": "https://logo.com",
          "group": "Lloyds",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "Monzo",
      "bankGroupNameFormatted": "Monzo",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-monzo-personal",
          "name": "Monzo Personal",
          "friendlyName": "Monzo Persnal",
          "iconUrl": "https://logo.com",
          "group": "Monzo",
          "hasFdp": true
        },
        {
          "bankId": "obie-monzo-business",
          "name": "Monzo Business",
          "friendlyName": "Monzo Business",
          "iconUrl": "https://logo.com",
          "group": "Monzo",
          "hasFdp": true
        },
        {
          "bankId": "obie-monzo-special",
          "name": "Monzo Special",
          "friendlyName": "Monzo Special",
          "iconUrl": "https://logo.com",
          "group": "Monzo",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "Natwest",
      "bankGroupNameFormatted": "Natwest",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-natwest-personal",
          "name": "Natwest Personal",
          "friendlyName": "Natwest Personal",
          "iconUrl": "https://logo.com",
          "group": "Natwest",
          "hasFdp": true
        },
        {
          "bankId": "obie-natwest-business",
          "name": "Natwest Business",
          "friendlyName": "Natwest Business",
          "iconUrl": "https://logo.com",
          "group": "Natwest",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "HSBC",
      "bankGroupNameFormatted": "HSBC",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-hsbc-personal",
          "name": "HSBC Personal",
          "friendlyName": "HSBC Personal",
          "iconUrl": "https://logo.com",
          "group": "HSBC",
          "hasFdp": true
        },
        {
          "bankId": "obie-hsbc-business",
          "name": "HSBC Business",
          "friendlyName": "HSBC Business",
          "iconUrl": "https://logo.com",
          "group": "HSBC",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "Santander",
      "bankGroupNameFormatted": "Santander",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-santander-personal",
          "name": "Santander Personal",
          "friendlyName": "Santander Personal",
          "iconUrl": "https://logo.com",
          "group": "Santander",
          "hasFdp": true
        },
        {
          "bankId": "obie-santander-business",
          "name": "Santander Business",
          "friendlyName": "Santander Business",
          "iconUrl": "https://logo.com",
          "group": "Santander",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "Chase Bank",
      "bankGroupNameFormatted": "Chase Bank",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-chase",
          "name": "Chase",
          "friendlyName": "Chase",
          "iconUrl": "https://logo.com",
          "group": "Chase Bank",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "RBS",
      "bankGroupNameFormatted": "RBS",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-rbs-personal",
          "name": "RBS Personal",
          "friendlyName": "RBS Personal",
          "iconUrl": "https://logo.com",
          "group": "RBS",
          "hasFdp": true
        },
        {
          "bankId": "obie-rbs-business",
          "name": "RBS Business",
          "friendlyName": "RBS Business",
          "iconUrl": "https://logo.com",
          "group": "RBS",
          "hasFdp": true
        },
        {
          "bankId": "obie-rbs-bankline",
          "name": "RBS Bankline",
          "friendlyName": "RBS Bankline",
          "iconUrl": "https://logo.com",
          "group": "RBS",
          "hasFdp": true
        }
      ]
    },
    {
      "bankGroupName": "M&S Bank",
      "bankGroupNameFormatted": "M&S Bank",
      "iconUrl": "https://logo.com",
      "banksInGroup": [
        {
          "bankId": "obie-m&s",
          "name": "M&S Bank",
          "friendlyName": "M&S Bank",
          "iconUrl": "https://logo.com",
          "group": "M&S Bank",
          "hasFdp": true
        }
      ]
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




