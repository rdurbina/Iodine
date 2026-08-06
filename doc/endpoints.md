# IODINE API Endpoints

The API runs on port `8080` by default. All request bodies use
`Content-Type: application/json`.

## Endpoint summary

| Method | Path | Authentication | Description |
| --- | --- | --- | --- |
| `POST` | `/user` | Public | Create a user and issue a JWT. |
| `POST` | `/login` | Public | Authenticate a user and issue a JWT. |
| `PATCH` | `/user` | Bearer JWT | Update the authenticated user's profile. |

## Authentication

Send the JWT returned by `POST /user` or `POST /login` in the
`Authorization` header when calling a protected endpoint:

```http
Authorization: Bearer <jwt>
```

## Create a user

`POST /user`

Creates a user. Usernames and email addresses must be unique.

### Request

```json
{
  "username": "johndoe",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "Password!"
}
```

| Field | Rules |
| --- | --- |
| `username` | Required; 2-20 characters. |
| `fullName` | Required; at most 30 characters. |
| `email` | Required; valid email address. |
| `password` | Required; at least 8 non-whitespace characters, including an uppercase letter and a special character. |

### Success response

Status: `201 Created`

```json
{
  "id": 1,
  "fullName": "John Doe",
  "username": "johndoe",
  "email": "john.doe@example.com",
  "jwt": "<jwt>"
}
```

Possible error statuses are `400 Bad Request` for invalid or malformed input and
`409 Conflict` when the username or email address is already in use.

## Log in

`POST /login`

Authenticates a user with their username and password.

### Request

```json
{
  "username": "johndoe",
  "password": "Password!"
}
```

Both fields are required and must not be blank.

### Success response

Status: `200 OK`

The response body is the JWT as plain text:

```text
<jwt>
```

Invalid credentials return `401 Unauthorized`.

## Update a user

`PATCH /user`

Updates the profile of the user identified by the bearer JWT.

### Request

```json
{
  "fullName": "Jane Doe"
}
```

`fullName` is optional. When present, it is trimmed, must contain a
non-whitespace character, and must be at most 30 characters. An empty object
leaves the current name unchanged. Username, email, and password cannot be
updated through this endpoint.

### Success response

Status: `200 OK`

```json
{
  "id": 1,
  "fullName": "Jane Doe",
  "username": "johndoe",
  "email": "john.doe@example.com"
}
```

Invalid input returns `400 Bad Request`. A missing or invalid bearer JWT is
rejected by Spring Security; unauthenticated requests currently return
`403 Forbidden`.

## Error response

Handled API errors use the following shape:

```json
{
  "errorType": "ValidationError",
  "message": "ValidationFailed",
  "url": "/user",
  "timestamp": "2026-08-05T12:00:00",
  "details": [
    {
      "resource": null,
      "field": "Email",
      "code": "InvalidFormat"
    }
  ]
}
```

Depending on the failure, `details` can be empty or contain one or more field
errors. Current field error codes include `Required`, `TooShort`, `TooLong`,
`InvalidFormat`, and `AlreadyInUse`.
