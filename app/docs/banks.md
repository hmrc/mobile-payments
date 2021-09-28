The Get Banks response
----
  Fetch the BanksResponse object.
  
* **URL**

  `/mobile-payments/banks` 

* **Method:**
  
  `GET`
  
*  **URL Params**

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
      "bank_id": "obie-mettle-production",
      "name": "Mettle",
      "friendly_name": "Mettle",
      "is_sandbox": false,
      "logo": "https://public.ecospend.com/images/banks/Mettle.svg",
      "icon": "https://public.ecospend.com/images/banks/Mettle_icon.svg",
      "standard": "obie",
      "country_iso_code": "GB",
      "group": "Mettle",
      "order": 100000,
      "service_status": true,
      "refund_supported": true,
      "abilities": {
        "domestic_payment": true,
        "domestic_scheduled_payment": true,
        "domestic_standing_order": false,
        "domestic_standing_order_installment": false,
        "international_payment": false,
        "international_scheduled_payment": false,
        "international_standing_order": false
      }
    }
  ],
  "meta": {
    "total_count": 68,
    "total_pages": 1,
    "current_page": 1
  }
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




