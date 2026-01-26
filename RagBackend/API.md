---
title: RAG Backend API v1.0
language_tabs:
  - python: Python
  - javascript: JavaScript
language_clients:
  - python: ""
  - javascript: ""
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="rag-backend-api">RAG Backend API v1.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

API documentation with JWT authentication

Base URLs:

* <a href="http://localhost:8080">http://localhost:8080</a>

# Authentication

- HTTP Authentication, scheme: bearer 

<h1 id="rag-backend-api-rag-query-controller">rag-query-controller</h1>

## createQuery

<a id="opIdcreateQuery"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://localhost:8080/rag/collections/{collectionId}/queries/ask', headers = headers)

print(r.json())

```

```javascript
const inputBody = '{
  "backendId": "string",
  "userId": "string",
  "collectionName": "string",
  "llmModel": "string",
  "embedModel": "string",
  "question": "string",
  "options": {
    "property1": {},
    "property2": {}
  }
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collections/{collectionId}/queries/ask',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /rag/collections/{collectionId}/queries/ask`

> Body parameter

```json
{
  "backendId": "string",
  "userId": "string",
  "collectionName": "string",
  "llmModel": "string",
  "embedModel": "string",
  "question": "string",
  "options": {
    "property1": {},
    "property2": {}
  }
}
```

<h3 id="createquery-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|string|true|none|
|body|body|[AskRabbitmqRequestDto](#schemaaskrabbitmqrequestdto)|true|none|

> Example responses

> 200 Response

<h3 id="createquery-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="createquery-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getChat

<a id="opIdgetChat"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://localhost:8080/rag/collections/{collectionId}/queries/chat-history', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collections/{collectionId}/queries/chat-history',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /rag/collections/{collectionId}/queries/chat-history`

<h3 id="getchat-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|string|true|none|

> Example responses

> 200 Response

<h3 id="getchat-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getchat-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="rag-backend-api-documents-controller">documents-controller</h1>

## getAllDocuments

<a id="opIdgetAllDocuments"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://localhost:8080/rag/collection/{collectionId}/documents/', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/{collectionId}/documents/',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /rag/collection/{collectionId}/documents/`

<h3 id="getalldocuments-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getalldocuments-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getalldocuments-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## addDocument

<a id="opIdaddDocument"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'multipart/form-data',
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://localhost:8080/rag/collection/{collectionId}/documents/', headers = headers)

print(r.json())

```

```javascript
const inputBody = '{
  "file": "string"
}';
const headers = {
  'Content-Type':'multipart/form-data',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/{collectionId}/documents/',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /rag/collection/{collectionId}/documents/`

> Body parameter

```yaml
file: string

```

<h3 id="adddocument-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|integer(int64)|true|none|
|body|body|object|false|none|
|» file|body|string(binary)|true|Document file to upload|

> Example responses

> 200 Response

<h3 id="adddocument-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="adddocument-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getAllDocuments_1

<a id="opIdgetAllDocuments_1"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://localhost:8080/rag/collection/{collectionId}/documents', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/{collectionId}/documents',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /rag/collection/{collectionId}/documents`

<h3 id="getalldocuments_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getalldocuments_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getalldocuments_1-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## addDocument_1

<a id="opIdaddDocument_1"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'multipart/form-data',
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://localhost:8080/rag/collection/{collectionId}/documents', headers = headers)

print(r.json())

```

```javascript
const inputBody = '{
  "file": "string"
}';
const headers = {
  'Content-Type':'multipart/form-data',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/{collectionId}/documents',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /rag/collection/{collectionId}/documents`

> Body parameter

```yaml
file: string

```

<h3 id="adddocument_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|integer(int64)|true|none|
|body|body|object|false|none|
|» file|body|string(binary)|true|Document file to upload|

> Example responses

> 200 Response

<h3 id="adddocument_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="adddocument_1-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getDocument

<a id="opIdgetDocument"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://localhost:8080/rag/collection/{collectionId}/documents/{documentId}', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/{collectionId}/documents/{documentId}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /rag/collection/{collectionId}/documents/{documentId}`

<h3 id="getdocument-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|integer(int64)|true|none|
|documentId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getdocument-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getdocument-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="rag-backend-api-collection-controller">collection-controller</h1>

## getAllUserCollections

<a id="opIdgetAllUserCollections"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://localhost:8080/rag/collection/', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /rag/collection/`

<h3 id="getallusercollections-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|limit|query|integer(int32)|false|none|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallusercollections-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getallusercollections-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[CollectionDto](#schemacollectiondto)]|false|none|none|
|» id|integer(int64)|false|none|none|
|» collectionName|string|false|none|none|
|» numberOfDocs|integer(int32)|false|none|none|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## addCollection

<a id="opIdaddCollection"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://localhost:8080/rag/collection/', headers = headers)

print(r.json())

```

```javascript
const inputBody = '{
  "collectionName": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /rag/collection/`

> Body parameter

```json
{
  "collectionName": "string"
}
```

<h3 id="addcollection-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateCollectionRequestDto](#schemacreatecollectionrequestdto)|true|none|

> Example responses

> 200 Response

<h3 id="addcollection-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="addcollection-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getAllUserCollections_1

<a id="opIdgetAllUserCollections_1"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://localhost:8080/rag/collection', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /rag/collection`

<h3 id="getallusercollections_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|limit|query|integer(int32)|false|none|
|page|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="getallusercollections_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getallusercollections_1-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[CollectionDto](#schemacollectiondto)]|false|none|none|
|» id|integer(int64)|false|none|none|
|» collectionName|string|false|none|none|
|» numberOfDocs|integer(int32)|false|none|none|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## addCollection_1

<a id="opIdaddCollection_1"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://localhost:8080/rag/collection', headers = headers)

print(r.json())

```

```javascript
const inputBody = '{
  "collectionName": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /rag/collection`

> Body parameter

```json
{
  "collectionName": "string"
}
```

<h3 id="addcollection_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateCollectionRequestDto](#schemacreatecollectionrequestdto)|true|none|

> Example responses

> 200 Response

<h3 id="addcollection_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="addcollection_1-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## getUserCollections

<a id="opIdgetUserCollections"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.get('http://localhost:8080/rag/collection/{collectionId}', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/{collectionId}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /rag/collection/{collectionId}`

<h3 id="getusercollections-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="getusercollections-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getusercollections-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## deleteCollection

<a id="opIddeleteCollection"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.delete('http://localhost:8080/rag/collection/{collectionId}', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/rag/collection/{collectionId}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /rag/collection/{collectionId}`

<h3 id="deletecollection-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collectionId|path|string|true|none|

> Example responses

> 200 Response

<h3 id="deletecollection-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

<h1 id="rag-backend-api-auth-controller">auth-controller</h1>

## signup

<a id="opIdsignup"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': '*/*'
}

r = requests.post('http://localhost:8080/auth/signup', headers = headers)

print(r.json())

```

```javascript
const inputBody = '{
  "name": "string",
  "email": "string",
  "password": "string",
  "phoneNumber": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*'
};

fetch('http://localhost:8080/auth/signup',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /auth/signup`

> Body parameter

```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "phoneNumber": "string"
}
```

<h3 id="signup-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UserSignupRequest](#schemausersignuprequest)|true|none|

> Example responses

> 200 Response

<h3 id="signup-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="signup-responseschema">Response Schema</h3>

<aside class="success">
This operation does not require authentication
</aside>

## refreshToken

<a id="opIdrefreshToken"></a>

> Code samples

```python
import requests
headers = {
  'Accept': '*/*',
  'Authorization': 'Bearer {access-token}'
}

r = requests.post('http://localhost:8080/auth/refreshtoken', headers = headers)

print(r.json())

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/auth/refreshtoken',
{
  method: 'POST',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /auth/refreshtoken`

> Example responses

> 200 Response

<h3 id="refreshtoken-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="refreshtoken-responseschema">Response Schema</h3>

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearerAuth
</aside>

## login

<a id="opIdlogin"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': '*/*'
}

r = requests.post('http://localhost:8080/auth/login', headers = headers)

print(r.json())

```

```javascript
const inputBody = '{
  "email": "string",
  "password": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*'
};

fetch('http://localhost:8080/auth/login',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /auth/login`

> Body parameter

```json
{
  "email": "string",
  "password": "string"
}
```

<h3 id="login-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[UserLoginRequest](#schemauserloginrequest)|true|none|

> Example responses

> 200 Response

<h3 id="login-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="login-responseschema">Response Schema</h3>

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_AskRabbitmqRequestDto">AskRabbitmqRequestDto</h2>
<!-- backwards compatibility -->
<a id="schemaaskrabbitmqrequestdto"></a>
<a id="schema_AskRabbitmqRequestDto"></a>
<a id="tocSaskrabbitmqrequestdto"></a>
<a id="tocsaskrabbitmqrequestdto"></a>

```json
{
  "backendId": "string",
  "userId": "string",
  "collectionName": "string",
  "llmModel": "string",
  "embedModel": "string",
  "question": "string",
  "options": {
    "property1": {},
    "property2": {}
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|backendId|string|false|none|none|
|userId|string|false|none|none|
|collectionName|string|false|none|none|
|llmModel|string|false|none|none|
|embedModel|string|false|none|none|
|question|string|false|none|none|
|options|object|false|none|none|
|» **additionalProperties**|object|false|none|none|

<h2 id="tocS_CreateCollectionRequestDto">CreateCollectionRequestDto</h2>
<!-- backwards compatibility -->
<a id="schemacreatecollectionrequestdto"></a>
<a id="schema_CreateCollectionRequestDto"></a>
<a id="tocScreatecollectionrequestdto"></a>
<a id="tocscreatecollectionrequestdto"></a>

```json
{
  "collectionName": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|collectionName|string|false|none|none|

<h2 id="tocS_UserSignupRequest">UserSignupRequest</h2>
<!-- backwards compatibility -->
<a id="schemausersignuprequest"></a>
<a id="schema_UserSignupRequest"></a>
<a id="tocSusersignuprequest"></a>
<a id="tocsusersignuprequest"></a>

```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "phoneNumber": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|false|none|none|
|email|string|false|none|none|
|password|string|false|none|none|
|phoneNumber|string|false|none|none|

<h2 id="tocS_UserLoginRequest">UserLoginRequest</h2>
<!-- backwards compatibility -->
<a id="schemauserloginrequest"></a>
<a id="schema_UserLoginRequest"></a>
<a id="tocSuserloginrequest"></a>
<a id="tocsuserloginrequest"></a>

```json
{
  "email": "string",
  "password": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|email|string|false|none|none|
|password|string|false|none|none|

<h2 id="tocS_CollectionDto">CollectionDto</h2>
<!-- backwards compatibility -->
<a id="schemacollectiondto"></a>
<a id="schema_CollectionDto"></a>
<a id="tocScollectiondto"></a>
<a id="tocscollectiondto"></a>

```json
{
  "id": 0,
  "collectionName": "string",
  "numberOfDocs": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|none|
|collectionName|string|false|none|none|
|numberOfDocs|integer(int32)|false|none|none|

