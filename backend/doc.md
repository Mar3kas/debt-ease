<h1 id="debt-management-system-api">Debt Management System API v1.0</h1>

> Scroll down for code samples, example requests and responses.

Debt Management System API

Base URLs:

* <a href="http://localhost:8080">http://localhost:8080</a>

# Authentication

- HTTP Authentication, scheme: bearer<br/>JWT Authorization header using Bearer scheme

<h1 id="debt-management-system-api-debtor-controller">Debtor Controller</h1>

## getDebtorById

<a id="opIdgetDebtorById"></a>

> Code samples

`GET /api/debtors/{id}`

<h3 id="getdebtorbyid-parameters">Parameters</h3>

| Name | In   | Type           | Required | Description |
|------|------|----------------|----------|-------------|
| id   | path | integer(int32) | true     | Debtor id   |

> Example responses
 
> 200 Response
```json
{
  "id": 0,
  "name": "string",
  "surname": "string",
  "email": "string",
  "phoneNumber": "string",
  "user": {
    "username": "string",
    "role": {
      "id": 0,
      "name": "string"
    }
  }
}
```

<h3 id="getdebtorbyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns Debtor by id  | [Debtor](#schemadebtor)     |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## editDebtorById

<a id="opIdeditDebtorById"></a>

> Code samples

`PUT /api/debtors/{id}`

> Body parameter

```json
{
  "name": "string",
  "surname": "string",
  "email": "string",
  "phoneNumber": "string"
}
```

<h3 id="editdebtorbyid-parameters">Parameters</h3>

| Name | In   | Type                          | Required | Description        |
|------|------|-------------------------------|----------|--------------------|
| id   | path | integer(int32)                | true     | Debtor id          |
| body | body | [DebtorDTO](#schemadebtordto) | true     | Debtor edited data |

> Example responses

> 200 Response
```json
{
  "id": 0, 
  "name": "string", 
  "surname": "string", 
  "email": "string", 
  "phoneNumber": "string", 
  "user": {
    "username": "string", 
    "role": {
      "id": 0, 
      "name": "string"
    }
  }
}
```

<h3 id="editdebtorbyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns edited Debtor | [Debtor](#schemadebtor)     |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## deleteDebtorById

<a id="opIddeleteDebtorById"></a>

> Code samples

`DELETE /api/debtors/{id}`

<h3 id="deletedebtorbyid-parameters">Parameters</h3>

| Name | In   | Type           | Required | Description |
|------|------|----------------|----------|-------------|
| id   | path | integer(int32) | true     | Debtor id   |

> Example responses

> 204 Response

<h3 id="deletedebtorbyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 204    | [No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)            | No Content            |                             |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |


<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## editDebtorById From Debt Case

<a id="opIdeditDebtorById_1"></a>

> Code samples

`PUT /api/creditor/{creditorId}/debtcase/{debtcaseId}/debtors/{id}`

> Body parameter

```json
{
  "name": "string",
  "surname": "string",
  "email": "string",
  "phoneNumber": "string"
}
```

<h3 id="editdebtorbyid_1-parameters">Parameters</h3>

| Name       | In   | Type                          | Required | Description   |
|------------|------|-------------------------------|----------|---------------|
| id         | path | integer(int32)                | true     | Debtor id     |
| debtcaseId | path | integer(int32)                | true     | Debt Case id  |
| creditorId | path | integer(int32)                | true     | Creditor id   |
| body       | body | [DebtorDTO](#schemadebtordto) | true     | Edited Debtor |

> Example responses

> 200 Response
```json
{
  "id": 0,
  "name": "string",
  "surname": "string",
  "email": "string",
  "phoneNumber": "string",
  "user": {
    "username": "string",
    "role": {
      "id": 0,
      "name": "string"
    }
  }
}
```

<h3 id="editdebtorbyid_1-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns edited Debtor | [Debtor](#schemadebtor)     |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## deleteDebtorById From Debt Case

<a id="opIddeleteDebtorById_1"></a>

> Code samples

`DELETE /api/creditor/{creditorId}/debtcase/{debtcaseId}/debtors/{id}`

<h3 id="deletedebtorbyid_1-parameters">Parameters</h3>

| Name       | In   | Type           | Required | Description  |
|------------|------|----------------|----------|--------------|
| id         | path | integer(int32) | true     | Debtor id    |
| debtcaseId | path | integer(int32) | true     | Debt Case id |
| creditorId | path | integer(int32) | true     | Creditor id  |

> Example responses

> 204 Response

<h3 id="deletedebtorbyid_1-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 204    | [No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)            | No Content            |                             |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## getAllDebtors

<a id="opIdgetAllDebtors"></a>

> Code samples

`GET /api/debtors`

> Example responses

> 200 Response
```json
[
  {
    "id": 0,
    "name": "string",
    "surname": "string",
    "email": "string",
    "phoneNumber": "string",
    "user": {
      "username": "string",
      "role": {
        "id": 0,
        "name": "string"
      }
    }
  }
]
```

<h3 id="getalldebtors-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List of Debtors       | [Debtor](#schemadebtor)     |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

<h1 id="debt-management-system-api-creditor-controller">Creditor Controller</h1>

## getCreditorById

<a id="opIdgetCreditorById"></a>

> Code samples

`GET /api/creditors/{id}`

<h3 id="getcreditorbyid-parameters">Parameters</h3>

| Name | In   | Type           | Required | Description |
|------|------|----------------|----------|-------------|
| id   | path | integer(int32) | true     | Creditor id |

> Example responses

> 200 Response

```json
{
  "id": 0,
  "name": "string",
  "address": "string",
  "phoneNumber": "string",
  "email": "string",
  "accountNumber": "string",
  "user": {
    "username": "string",
    "role": {
      "id": 0,
      "name": "string"
    }
  }
}
```

<h3 id="getcreditorbyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description            | Schema                      |
|--------|----------------------------------------------------------------------------|------------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns Creditor by id | [Creditor](#schemacreditor) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request            | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found              | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict               | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity   | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error  | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## editCreditorById

<a id="opIdeditCreditorById"></a>

> Code samples

`PUT /api/creditors/{id}`

> Body parameter

```json
{
  "name": "string",
  "address": "string",
  "phoneNumber": "string",
  "email": "string",
  "accountNumber": "string",
  "username": "string"
}
```

<h3 id="editcreditorbyid-parameters">Parameters</h3>

| Name | In   | Type                              | Required | Description     |
|------|------|-----------------------------------|----------|-----------------|
| id   | path | integer(int32)                    | true     | Creditor id     |
| body | body | [CreditorDTO](#schemacreditordto) | true     | Edited Creditor |

> Example responses

> 200 Response

<h3 id="editcreditorbyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description             | Schema                      |
|--------|----------------------------------------------------------------------------|-------------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns edited Creditor | [Creditor](#schemacreditor) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request             | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found               | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict                | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity    | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error   | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## deleteCreditorById

<a id="opIddeleteCreditorById"></a>

> Code samples

`DELETE /api/creditors/{id}`

<h3 id="deletecreditorbyid-parameters">Parameters</h3>

| Name | In   | Type           | Required | Description |
|------|------|----------------|----------|-------------|
| id   | path | integer(int32) | true     | Creditor id |

> Example responses

> 204 Response

<h3 id="deletecreditorbyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 204    | [No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)            | No Content            |                             |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## getAllCreditors

<a id="opIdgetAllCreditors"></a>

> Code samples

`GET /api/creditors`

> Example responses

> 200 Response
```json
[
  {
    "id": 0,
    "name": "string",
    "address": "string",
    "phoneNumber": "string",
    "email": "string",
    "accountNumber": "string",
    "user": {
      "username": "string",
      "role": {
        "id": 0,
        "name": "string"
      }
    }
  }
]
```

<h3 id="getallcreditors-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List of Creditors     | [Creditor](#schemacreditor) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## createCreditor

<a id="opIdcreateCreditor"></a>

> Code samples

`POST /api/creditors`

> Body parameter

```json
{
  "name": "string",
  "address": "string",
  "phoneNumber": "string",
  "email": "string",
  "accountNumber": "string",
  "username": "string"
}
```

<h3 id="createcreditor-parameters">Parameters</h3>

| Name | In   | Type                              | Required | Description          |
|------|------|-----------------------------------|----------|----------------------|
| body | body | [CreditorDTO](#schemacreditordto) | true     | Creditor create data |

> Example responses

> 200 Response

```json
{
  "name": "string",
  "address": "string",
  "phoneNumber": "(47-972-2725",
  "email": "string",
  "accountNumber": "string",
  "username": "string"
}
```

<h3 id="createcreditor-responses">Responses</h3>

| Status | Meaning                                                                    | Description              | Schema                      |
|--------|----------------------------------------------------------------------------|--------------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns created Creditor | [Creditor](#schemacreditor) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request              | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found                | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict                 | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity     | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error    | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

<h1 id="debt-management-system-api-debt-case-controller">Debt Case Controller</h1>

## editDebtCaseById
Edit Debt Case of certain Creditor

<a id="opIdeditDebtCaseById"></a>

> Code samples

`PUT /api/creditor/{creditorId}/debtcases/{id}`

> Body parameter

```json
{
  "amountOwed": 0,
  "dueDate": "2019-08-24 14:15:22",
  "typeId": 0
}
```

<h3 id="editdebtcasebyid-parameters">Parameters</h3>

| Name       | In   | Type                              | Required | Description      |
|------------|------|-----------------------------------|----------|------------------|
| id         | path | integer(int32)                    | true     | Debt Case id     |
| creditorId | path | integer(int32)                    | true     | Creditor id      |
| body       | body | [DebtCaseDTO](#schemadebtcasedto) | true     | Edited Debt Case |

> Example responses

> 200 Response
```json
{
  "id": 0,
  "amountOwed": 0,
  "dueDate": "2023-12-07 07:15:06",
  "debtCaseType": {
    "id": 0,
    "type": "string"
  },
  "debtCaseStatus": {
    "id": 0,
    "status": "string"
  },
  "creditor": {
    "id": 0,
    "name": "string",
    "address": "string",
    "phoneNumber": "string",
    "email": "string",
    "accountNumber": "string",
    "user": {
      "username": "string",
      "role": {
        "id": 0,
        "name": "string"
      }
    }
  },
  "debtors": [
    {
      "id": 0,
      "name": "string",
      "surname": "string",
      "email": "string",
      "phoneNumber": "string",
      "user": {
        "username": "string",
        "role": {
          "id": 0,
          "name": "string"
        }
      }
    }
  ],
  "isSent": 0
}
```

<h3 id="editdebtcasebyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Edited Debt Case      | [DebtCase](#schemadebtcase) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## deleteDebtCaseById

<a id="opIddeleteDebtCaseById"></a>

> Code samples

`DELETE /api/creditor/{creditorId}/debtcases/{id}`

<h3 id="deletedebtcasebyid-parameters">Parameters</h3>

| Name       | In   | Type           | Required | Description  |
|------------|------|----------------|----------|--------------|
| creditorId | path | integer(int32) | true     | Creditor id  |
| id         | path | integer(int32) | true     | Debt Case id |

> Example responses

> 204 Response

<h3 id="deletedebtcasebyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 204    | [No Content](https://tools.ietf.org/html/rfc7231#section-6.3.5)            | No Content            |                             |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## createDebtCase
Creates Debt Cases by uploading CSV file

<a id="opIdcreateDebtCase"></a>

> Code samples

`POST /api/creditor/{id}/debtcases/file`

> Body parameter

```yaml
file: string
```

<h3 id="createdebtcase-parameters">Parameters</h3>

| Name   | In   | Type           | Required | Description         |
|--------|------|----------------|----------|---------------------|
| id     | path | integer(int32) | true     | Creditor id         |
| body   | body | object         | false    | multipart/form-data |
| » file | body | string(binary) | true     | CSV file            |

> Example responses

> 200 Response
```json
[
  {
    "id": 0,
    "amountOwed": 0,
    "dueDate": "2023-12-07 07:19:31",
    "debtCaseType": {
      "id": 0,
      "type": "string"
    },
    "debtCaseStatus": {
      "id": 0,
      "status": "string"
    },
    "creditor": {
      "id": 0,
      "name": "string",
      "address": "string",
      "phoneNumber": "string",
      "email": "string",
      "accountNumber": "string",
      "user": {
        "username": "string",
        "role": {
          "id": 0,
          "name": "string"
        }
      }
    },
    "debtors": [
      {
        "id": 0,
        "name": "string",
        "surname": "string",
        "email": "string",
        "phoneNumber": "string",
        "user": {
          "username": "string",
          "role": {
            "id": 0,
            "name": "string"
          }
        }
      }
    ],
    "isSent": 0
  }
]
```

<h3 id="createdebtcase-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List of Debt Cases    | [DebtCase](#schemadebtcase) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## getAllDebtCases

<a id="opIdgetAllDebtCases"></a>

> Code samples

`GET /api/debtcases`

> Example responses

> 200 Response

```json
[
  {
    "id": 0,
    "amountOwed": 0,
    "dueDate": "2023-12-07 07:19:31",
    "debtCaseType": {
      "id": 0,
      "type": "string"
    },
    "debtCaseStatus": {
      "id": 0,
      "status": "string"
    },
    "creditor": {
      "id": 0,
      "name": "string",
      "address": "string",
      "phoneNumber": "string",
      "email": "string",
      "accountNumber": "string",
      "user": {
        "username": "string",
        "role": {
          "id": 0,
          "name": "string"
        }
      }
    },
    "debtors": [
      {
        "id": 0,
        "name": "string",
        "surname": "string",
        "email": "string",
        "phoneNumber": "string",
        "user": {
          "username": "string",
          "role": {
            "id": 0,
            "name": "string"
          }
        }
      }
    ],
    "isSent": 0
  }
]
```

<h3 id="getalldebtcases-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List of Debt Cases    | [DebtCase](#schemadebtcase) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## getDebtCaseById

<a id="opIdgetDebtCaseById"></a>

> Code samples

`GET /api/debtcases/{id}`

<h3 id="getdebtcasebyid-parameters">Parameters</h3>

| Name | In   | Type           | Required | Description  |
|------|------|----------------|----------|--------------|
| id   | path | integer(int32) | true     | Debt Case id |

> Example responses

> 200 Response

```json
{
  "id": 0,
  "amountOwed": 0,
  "dueDate": "2023-12-07 07:15:06",
  "debtCaseType": {
    "id": 0,
    "type": "string"
  },
  "debtCaseStatus": {
    "id": 0,
    "status": "string"
  },
  "creditor": {
    "id": 0,
    "name": "string",
    "address": "string",
    "phoneNumber": "string",
    "email": "string",
    "accountNumber": "string",
    "user": {
      "username": "string",
      "role": {
        "id": 0,
        "name": "string"
      }
    }
  },
  "debtors": [
    {
      "id": 0,
      "name": "string",
      "surname": "string",
      "email": "string",
      "phoneNumber": "string",
      "user": {
        "username": "string",
        "role": {
          "id": 0,
          "name": "string"
        }
      }
    }
  ],
  "isSent": 0
}
```

<h3 id="getdebtcasebyid-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns Debt Case     | [DebtCase](#schemadebtcase) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## getDebtCasesByCreditorUsername
Gets certain Creditor Debt Cases

<a id="opIdgetDebtCasesByCreditorUsername"></a>

> Code samples

`GET /api/creditor/{username}/debtcases`

<h3 id="getdebtcasesbycreditorusername-parameters">Parameters</h3>

| Name     | In   | Type   | Required | Description       |
|----------|------|--------|----------|-------------------|
| username | path | string | true     | Creditor username |

> Example responses

> 200 Response
```json
[
  {
    "id": 0,
    "amountOwed": 0,
    "dueDate": "2023-12-07 07:19:31",
    "debtCaseType": {
      "id": 0,
      "type": "string"
    },
    "debtCaseStatus": {
      "id": 0,
      "status": "string"
    },
    "creditor": {
      "id": 0,
      "name": "string",
      "address": "string",
      "phoneNumber": "string",
      "email": "string",
      "accountNumber": "string",
      "user": {
        "username": "string",
        "role": {
          "id": 0,
          "name": "string"
        }
      }
    },
    "debtors": [
      {
        "id": 0,
        "name": "string",
        "surname": "string",
        "email": "string",
        "phoneNumber": "string",
        "user": {
          "username": "string",
          "role": {
            "id": 0,
            "name": "string"
          }
        }
      }
    ],
    "isSent": 0
  }
]
```

<h3 id="getdebtcasesbycreditorusername-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | List of Debt Cases    | [DebtCase](#schemadebtcase) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## getDebtCasesByDebtorUsername
Gets certain Debtor Debt Cases

<a id="opIdgetDebtCasesByDebtorUsername"></a>

> Code samples

`GET /api/creditor/debtcases/debtor/{username}`

<h3 id="getdebtcasesbydebtorusername-parameters">Parameters</h3>

|Name|In|Type|Required| Description     |
|---|---|---|---|-----------------|
|username|path|string|true| Debtor username |

> Example responses

> 200 Response
```json
[
  {
    "id": 0,
    "amountOwed": 0,
    "dueDate": "2023-12-07 07:19:31",
    "debtCaseType": {
      "id": 0,
      "type": "string"
    },
    "debtCaseStatus": {
      "id": 0,
      "status": "string"
    },
    "creditor": {
      "id": 0,
      "name": "string",
      "address": "string",
      "phoneNumber": "string",
      "email": "string",
      "accountNumber": "string"
    },
    "isSent": 0
  }
]
```

<h3 id="getdebtcasesbydebtorusername-responses">Responses</h3>

| Status | Meaning                                                                    | Description                 | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Modified list of Debt Cases | [DebtCase](#schemadebtcase) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request                 | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found                   | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict                    | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity        | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error       | [APIError](#schemaapierror) |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

<h1 id="debt-management-system-api-user-controller">User Controller</h1>

## refreshAccessToken
Using refresh token generates new JWT token if it is expired
<a id="opIdrefreshAccessToken"></a>

> Code samples

`POST /api/refresh`

> Body parameter

```json
{
  "refreshToken": "string"
}
```

<h3 id="refreshaccesstoken-parameters">Parameters</h3>

| Name | In   | Type                                              | Required | Description   |
|------|------|---------------------------------------------------|----------|---------------|
| body | body | [RefreshTokenRequest](#schemarefreshtokenrequest) | true     | Refresh token |

> Example responses

> 200 Response
```json
{
  "accessToken": "string"
}
```

<h3 id="refreshaccesstoken-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns JWT token     | Inline                      |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<h3 id="refreshaccesstoken-responseschema">Response Schema</h3>

Status Code **200**

| Name              | Type   | Required | Restrictions | Description |
|-------------------|--------|----------|--------------|-------------|
| » **accessToken** | string | false    | none         | JWT token   |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## logout

<a id="opIdlogout"></a>

> Code samples

`POST /api/logout`

> Example responses

> 200 Response
```json
{
  "message": "Logout successful"
}
```

<h3 id="logout-responses">Responses</h3>

| Status | Meaning                                                                    | Description           | Schema                      |
|--------|----------------------------------------------------------------------------|-----------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Logout user           | Inline                      |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request           | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found             | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict              | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity  | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error | [APIError](#schemaapierror) |

<h3 id="logout-responseschema">Response Schema</h3>

Status Code **200**

| Name          | Type   | Required | Restrictions | Description |
|---------------|--------|----------|--------------|-------------|
| » **message** | string | false    | none         | Logout info |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

## authenticateUser

<a id="opIdauthenticateUser"></a>

> Code samples

`POST /api/login`

> Body parameter

```json
{
  "username": "string",
  "password": "string"
}
```

<h3 id="authenticateuser-parameters">Parameters</h3>

| Name | In   | Type                      | Required | Description        |
|------|------|---------------------------|----------|--------------------|
| body | body | [UserDTO](#schemauserdto) | true     | User login details |

> Example responses

> 200 Response
```json
{
  "accessToken": "accessToken",
  "refreshToken": "refreshToken"
}
```

<h3 id="authenticateuser-responses">Responses</h3>

| Status | Meaning                                                                    | Description                    | Schema                      |
|--------|----------------------------------------------------------------------------|--------------------------------|-----------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns JWT and Refresh tokens | Inline                      |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request                    | [APIError](#schemaapierror) |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found                      | [APIError](#schemaapierror) |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict                       | [APIError](#schemaapierror) |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity           | [APIError](#schemaapierror) |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error          | [APIError](#schemaapierror) |

<h3 id="authenticateuser-responseschema">Response Schema</h3>

Status Code **200**

| Name               | Type   | Required | Restrictions | Description   |
|--------------------|--------|----------|--------------|---------------|
| » **accessToken**  | string | false    | none         | JWT token     |
| » **refreshToken** | string | false    | none         | Refresh token |

## getUserByUsername

<a id="opIdgetUserByUsername"></a>

> Code samples

`GET /api/users/{username}`

<h3 id="getuserbyusername-parameters">Parameters</h3>

| Name     | In   | Type   | Required | Description   |
|----------|------|--------|----------|---------------|
| username | path | string | true     | User username |

> Example responses

> 200 Response

> Debtor, Admin or Creditor json 

<h3 id="getuserbyusername-responses">Responses</h3>

| Status | Meaning                                                                    | Description                            | Schema                                                                      |
|--------|----------------------------------------------------------------------------|----------------------------------------|-----------------------------------------------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns Debtor, Admin or Creditor data | [Debtor](#schemadebtor), [Creditor](#schemacreditor), [Admin](#schemaadmin) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request                            | [APIError](#schemaapierror)                                                 |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found                              | [APIError](#schemaapierror)                                                 |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict                               | [APIError](#schemaapierror)                                                 |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity                   | [APIError](#schemaapierror)                                                 |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error                  | [APIError](#schemaapierror)                                                 |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

<h1 id="debt-management-system-api-debt-case-type-controller">Debt Case Type Controller</h1>

## getAllDebtCaseTypes

<a id="opIdgetAllDebtCaseTypes"></a>

> Code samples

`GET /api/debtcase/types`

> Example responses

> 200 Response
```json
[
  {
    "id": 0,
    "type": "string"
  }
]
```

<h3 id="getalldebtcasetypes-responses">Responses</h3>

| Status | Meaning                                                                    | Description                     | Schema                              |
|--------|----------------------------------------------------------------------------|---------------------------------|-------------------------------------|
| 200    | [OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)                    | Returns list of Debt Case Types | [DebtCaseType](#schemadebtcasetype) |
| 400    | [Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)           | Bad Request                     | [APIError](#schemaapierror)         |
| 404    | [Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)             | Not Found                       | [APIError](#schemaapierror)         |
| 409    | [Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)              | Conflict                        | [APIError](#schemaapierror)         |
| 422    | [Unprocessable Entity](https://tools.ietf.org/html/rfc2518#section-10.3)   | Unprocessable Entity            | [APIError](#schemaapierror)         |
| 500    | [Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1) | Internal Server Error           | [APIError](#schemaapierror)         |

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods: JWT
</aside>

# Schemas

<h2 id="tocS_APIError">APIError</h2>

<a id="schemaapierror"></a>
<a id="schema_APIError"></a>
<a id="tocSapierror"></a>
<a id="tocsapierror"></a>

```json
{
  "statusCode": 0,
  "time": "2019-08-24 14:15:22",
  "message": "string",
  "description": "string"
}

```

### Properties

| Name        | Type              | Required | Restrictions | Description                |
|-------------|-------------------|----------|--------------|----------------------------|
| statusCode  | integer(int32)    | true     | none         | none                       |
| time        | string(date-time) | true     | none         | When error happened        |
| message     | string            | true     | none         | What error                 |
| description | string            | true     | none         | Detailed error description |

<h2 id="tocS_DebtorDTO">DebtorDTO</h2>

<a id="schemadebtordto"></a>
<a id="schema_DebtorDTO"></a>
<a id="tocSdebtordto"></a>
<a id="tocsdebtordto"></a>

```json
{
  "name": "string",
  "surname": "string",
  "email": "string",
  "phoneNumber": "string"
}

```

### Properties

| Name        | Type   | Required | Restrictions                 | Description |
|-------------|--------|----------|------------------------------|-------------|
| name        | string | true     | none                         | none        |
| surname     | string | true     | none                         | none        |
| email       | string | true     | Should be valid email        | none        |
| phoneNumber | string | true     | Should be valid phone number | none        |

<h2 id="tocS_CustomUser">CustomUser</h2>

<a id="schemacustomuser"></a>
<a id="schema_CustomUser"></a>
<a id="tocScustomuser"></a>
<a id="tocscustomuser"></a>

```json
{
  "username": "string",
  "role": {
    "id": 0,
    "name": "string"
  }
}

```

### Properties

| Name     | Type                | Required | Restrictions     | Description |
|----------|---------------------|----------|------------------|-------------|
| username | string              | true     | Should be unique | none        |
| role     | [Role](#schemarole) | false    | none             | none        |

<h2 id="tocS_Debtor">Debtor</h2>

<a id="schemadebtor"></a>
<a id="schema_Debtor"></a>
<a id="tocSdebtor"></a>
<a id="tocsdebtor"></a>

```json
{
  "id": 0,
  "name": "string",
  "surname": "string",
  "email": "string",
  "phoneNumber": "string",
  "user": {
    "username": "string",
    "role": {
      "id": 0,
      "name": "string"
    }
  }
}

```

### Properties

| Name        | Type                            | Required | Restrictions                 | Description |
|-------------|---------------------------------|----------|------------------------------|-------------|
| id          | integer(int32)                  | true     | none                         | none        |
| name        | string                          | true     | none                         | none        |
| surname     | string                          | true     | none                         | none        |
| email       | string                          | false    | Should be valid email        | none        |
| phoneNumber | string                          | false    | Should be valid phone number | none        |
| user        | [CustomUser](#schemacustomuser) | false    | Should be unique             | none        |

<h2 id="tocS_Role">Role</h2>

<a id="schemarole"></a>
<a id="schema_Role"></a>
<a id="tocSrole"></a>
<a id="tocsrole"></a>

```json
{
  "id": 0,
  "name": "string"
}

```

### Properties

|Name|Type| Required |Restrictions|Description|
|---|---|----------|---|---|
|id|integer(int32)| true     |none|none|
|name|string| true     |none|none|

<h2 id="tocS_CreditorDTO">CreditorDTO</h2>

<a id="schemacreditordto"></a>
<a id="schema_CreditorDTO"></a>
<a id="tocScreditordto"></a>
<a id="tocscreditordto"></a>

```json
{
  "name": "string",
  "address": "string",
  "phoneNumber": "string",
  "email": "string",
  "accountNumber": "string",
  "username": "string"
}

```

### Properties

| Name          | Type   | Required | Restrictions | Description |
|---------------|--------|----------|--------------|-------------|
| name          | string | true     | none         | none        |
| address       | string | true     | none         | none        |
| phoneNumber   | string | true     | none         | none        |
| email         | string | true     | none         | none        |
| accountNumber | string | true     | none         | none        |
| username      | string | false    | none         | none        |

<h2 id="tocS_Creditor">Creditor</h2>

<a id="schemacreditor"></a>
<a id="schema_Creditor"></a>
<a id="tocScreditor"></a>
<a id="tocscreditor"></a>

```json
{
  "id": 0,
  "name": "string",
  "address": "string",
  "phoneNumber": "string",
  "email": "string",
  "accountNumber": "string",
  "user": {
    "username": "string",
    "role": {
      "id": 0,
      "name": "string"
    }
  }
}

```

### Properties

| Name          | Type                            | Required | Restrictions | Description |
|---------------|---------------------------------|----------|--------------|-------------|
| id            | integer(int32)                  | true     | none         | none        |
| name          | string                          | true     | none         | none        |
| address       | string                          | true     | none         | none        |
| phoneNumber   | string                          | true     | none         | none        |
| email         | string                          | true     | none         | none        |
| accountNumber | string                          | true     | none         | none        |
| user          | [CustomUser](#schemacustomuser) | false    | none         | none        |

<h2 id="tocS_DebtCaseDTO">DebtCaseDTO</h2>

<a id="schemadebtcasedto"></a>
<a id="schema_DebtCaseDTO"></a>
<a id="tocSdebtcasedto"></a>
<a id="tocsdebtcasedto"></a>

```json
{
  "amountOwed": 0,
  "dueDate": "2019-08-24T14:15:22Z",
  "typeId": 0
}

```

### Properties

| Name       | Type              | Required | Restrictions                        | Description       |
|------------|-------------------|----------|-------------------------------------|-------------------|
| amountOwed | number            | true     | Must be positive and can't be empty | none              |
| dueDate    | string(date-time) | false    | none                                | none              |
| typeId     | integer(int32)    | false    | none                                | Debt Case Type id |

<h2 id="tocS_DebtCase">DebtCase</h2>

<a id="schemadebtcase"></a>
<a id="schema_DebtCase"></a>
<a id="tocSdebtcase"></a>
<a id="tocsdebtcase"></a>

```json
{
  "id": 0,
  "amountOwed": 0,
  "dueDate": "2019-08-24T14:15:22Z",
  "debtCaseType": {
    "id": 0,
    "type": "string"
  },
  "debtCaseStatus": {
    "id": 0,
    "status": "string"
  },
  "creditor": {
    "id": 0,
    "name": "string",
    "address": "string",
    "phoneNumber": "string",
    "email": "string",
    "accountNumber": "string",
    "user": {
      "username": "string",
      "role": {
        "id": 0,
        "name": "string"
      }
    }
  },
  "debtors": [
    {
      "id": 0,
      "name": "string",
      "surname": "string",
      "email": "string",
      "phoneNumber": "string",
      "user": {
        "username": "string",
        "role": {
          "id": 0,
          "name": "string"
        }
      }
    }
  ],
  "isSent": 0
}

```

### Properties

| Name           | Type                                    | Required | Restrictions | Description     |
|----------------|-----------------------------------------|----------|--------------|-----------------|
| id             | integer(int32)                          | true     | none         | none            |
| amountOwed     | number                                  | true     | none         | none            |
| dueDate        | string(date-time)                       | true     | none         | none            |
| debtCaseType   | [DebtCaseType](#schemadebtcasetype)     | true     | none         | none            |
| debtCaseStatus | [DebtCaseStatus](#schemadebtcasestatus) | true     | none         | none            |
| creditor       | [Creditor](#schemacreditor)             | true     | none         | none            |
| debtors        | [[Debtor](#schemadebtor)]               | false    | none         | List of Debtors |
| isSent         | integer(int32)                          | true     | none         | none            |

<h2 id="tocS_DebtCaseStatus">DebtCaseStatus</h2>

<a id="schemadebtcasestatus"></a>
<a id="schema_DebtCaseStatus"></a>
<a id="tocSdebtcasestatus"></a>
<a id="tocsdebtcasestatus"></a>

```json
{
  "id": 0,
  "status": "string"
}

```

### Properties

| Name   | Type           | Required | Restrictions | Description |
|--------|----------------|----------|--------------|-------------|
| id     | integer(int32) | true     | none         | none        |
| status | string         | true     | none         | none        |

<h2 id="tocS_DebtCaseType">DebtCaseType</h2>

<a id="schemadebtcasetype"></a>
<a id="schema_DebtCaseType"></a>
<a id="tocSdebtcasetype"></a>
<a id="tocsdebtcasetype"></a>

```json
{
  "id": 0,
  "type": "string"
}

```

### Properties

| Name | Type           | Required | Restrictions | Description |
|------|----------------|----------|--------------|-------------|
| id   | integer(int32) | true     | none         | none        |
| type | string         | true     | none         | none        |

<h2 id="tocS_RefreshTokenRequest">RefreshTokenRequest</h2>

<a id="schemarefreshtokenrequest"></a>
<a id="schema_RefreshTokenRequest"></a>
<a id="tocSrefreshtokenrequest"></a>
<a id="tocsrefreshtokenrequest"></a>

```json
{
  "refreshToken": "string"
}

```

### Properties

| Name         | Type   | Required | Restrictions | Description |
|--------------|--------|----------|--------------|-------------|
| refreshToken | string | true     | none         | none        |

<h2 id="tocS_UserDTO">UserDTO</h2>

<a id="schemauserdto"></a>
<a id="schema_UserDTO"></a>
<a id="tocSuserdto"></a>
<a id="tocsuserdto"></a>

```json
{
  "username": "string",
  "password": "string"
}

```

### Properties

| Name     | Type   | Required | Restrictions | Description |
|----------|--------|----------|--------------|-------------|
| username | string | true     | none         | none        |
| password | string | true     | none         | none        |