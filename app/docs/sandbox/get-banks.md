Get Banks
----

### Fetch the list of supported banks.

* **URL**

  `sandbox/banks`

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
      "banksInGroup": [
        {
          "bankId": "obie-barclays-personal",
          "name": "Barclays Personal",
          "friendlyName": "Barclays Personal",
          "iconUrl": "https://public.ecospend.com/images/banks/Barclays_icon.svg",
          "group": "Barclays",
          "hasFdp": false
        },
        {
          "bankId": "obie-barclays-business",
          "name": "Barclays Business",
          "friendlyName": "Barclays Business",
          "iconUrl": "https://public.ecospend.com/images/banks/Barclays_icon.svg",
          "group": "Barclays",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/Barclays_icon.svg"
    },
    {
      "bankGroupName": "Lloyds",
      "bankGroupNameFormatted": "Lloyds",
      "banksInGroup": [
        {
          "bankId": "obie-lloyds-personal",
          "name": "Lloyds Personal",
          "friendlyName": "Lloyds Personal",
          "iconUrl": "https://public.ecospend.com/images/banks/Lloyds_icon.svg",
          "group": "Lloyds",
          "hasFdp": false
        },
        {
          "bankId": "obie-lloyds-business",
          "name": "Lloyds Business",
          "friendlyName": "Lloyds Business",
          "iconUrl": "https://public.ecospend.com/images/banks/Lloyds_icon.svg",
          "group": "Lloyds",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/Lloyds_icon.svg"
    },
    {
      "bankGroupName": "Monzo",
      "bankGroupNameFormatted": "Monzo",
      "banksInGroup": [
        {
          "bankId": "obie-monzo-personal",
          "name": "Monzo Personal",
          "friendlyName": "Monzo Persnal",
          "iconUrl": "https://public.ecospend.com/images/banks/Monzo_icon.svg",
          "group": "Monzo",
          "hasFdp": false
        },
        {
          "bankId": "obie-monzo-business",
          "name": "Monzo Business",
          "friendlyName": "Monzo Business",
          "iconUrl": "https://public.ecospend.com/images/banks/Monzo_icon.svg",
          "group": "Monzo",
          "hasFdp": false
        },
        {
          "bankId": "obie-monzo-special",
          "name": "Monzo Special",
          "friendlyName": "Monzo Special",
          "iconUrl": "https://public.ecospend.com/images/banks/Monzo_icon.svg",
          "group": "Monzo",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/Monzo_icon.svg"
    },
    {
      "bankGroupName": "Natwest",
      "bankGroupNameFormatted": "Natwest",
      "banksInGroup": [
        {
          "bankId": "obie-natwest-personal",
          "name": "Natwest Personal",
          "friendlyName": "Natwest Personal",
          "iconUrl": "https://public.ecospend.com/images/banks/NatWest_icon.svg",
          "group": "Natwest",
          "hasFdp": false
        },
        {
          "bankId": "obie-natwest-business",
          "name": "Natwest Business",
          "friendlyName": "Natwest Business",
          "iconUrl": "https://public.ecospend.com/images/banks/NatWest_icon.svg",
          "group": "Natwest",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/NatWest_icon.svg"
    },
    {
      "bankGroupName": "HSBC",
      "bankGroupNameFormatted": "HSBC",
      "banksInGroup": [
        {
          "bankId": "obie-hsbc-personal",
          "name": "HSBC Personal",
          "friendlyName": "HSBC Personal",
          "iconUrl": "https://public.ecospend.com/images/banks/HSBC_icon.svg",
          "group": "HSBC",
          "hasFdp": false
        },
        {
          "bankId": "obie-hsbc-business",
          "name": "HSBC Business",
          "friendlyName": "HSBC Business",
          "iconUrl": "https://public.ecospend.com/images/banks/HSBC_icon.svg",
          "group": "HSBC",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/HSBC_icon.svg"
    },
    {
      "bankGroupName": "Santander",
      "bankGroupNameFormatted": "Santander",
      "banksInGroup": [
        {
          "bankId": "obie-santander-personal",
          "name": "Santander Personal",
          "friendlyName": "Santander Personal",
          "iconUrl": "https://public.ecospend.com/images/banks/Santander_icon.svg",
          "group": "Santander",
          "hasFdp": false
        },
        {
          "bankId": "obie-santander-business",
          "name": "Santander Business",
          "friendlyName": "Santander Business",
          "iconUrl": "https://public.ecospend.com/images/banks/Santander_icon.svg",
          "group": "Santander",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/Santander_icon.svg"
    },
    {
      "bankGroupName": "Chase Bank",
      "bankGroupNameFormatted": "Chase Bank",
      "banksInGroup": [
        {
          "bankId": "obie-chase",
          "name": "Chase",
          "friendlyName": "Chase",
          "iconUrl": "https://public.ecospend.com/images/banks/Chase-Bank_icon.svg",
          "group": "Chase Bank",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/Chase-Bank_icon.svg"
    },
    {
      "bankGroupName": "RBS",
      "bankGroupNameFormatted": "RBS",
      "banksInGroup": [
        {
          "bankId": "obie-rbs-personal",
          "name": "RBS Personal",
          "friendlyName": "RBS Personal",
          "iconUrl": "https://public.ecospend.com/images/banks/RBS_icon.svg",
          "group": "RBS",
          "hasFdp": false
        },
        {
          "bankId": "obie-rbs-business",
          "name": "RBS Business",
          "friendlyName": "RBS Business",
          "iconUrl": "https://public.ecospend.com/images/banks/RBS_icon.svg",
          "group": "RBS",
          "hasFdp": false
        },
        {
          "bankId": "obie-rbs-bankline",
          "name": "RBS Bankline",
          "friendlyName": "RBS Bankline",
          "iconUrl": "https://public.ecospend.com/images/banks/RBS_icon.svg",
          "group": "RBS",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/RBS_icon.svg"
    },
    {
      "bankGroupName": "M&S Bank",
      "bankGroupNameFormatted": "M&S Bank",
      "banksInGroup": [
        {
          "bankId": "obie-m&s",
          "name": "M&S Bank",
          "friendlyName": "M&S Bank",
          "iconUrl": "https://public.ecospend.com/images/banks/M&S_icon.svg",
          "group": "M&S Bank",
          "hasFdp": false
        }
      ],
      "iconUrl": "https://public.ecospend.com/images/banks/M&S_icon.svg"
    }
  ]
}
```

* **Error Responses:**

  * **Code:** 406 NOT_ACCEPTABLE <br/>
    **Content:** `{"code":"NOT_ACCEPTABLE","message":Missing Accept Header"}`
