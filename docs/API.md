# Travel Discovery — Frontend API Guide

Everything a frontend needs to talk to the backend: base URL, auth, error
shapes, pagination, and a reference for every endpoint with request/response
bodies.

> **Versioning:** all endpoints are prefixed with `/api/v1`.
> **Content type:** JSON in/out (`Content-Type: application/json`), except the
> avatar upload which is `multipart/form-data`.

---

## 1. Base URL & environments

| Environment          | Base URL                                                     |
|----------------------|--------------------------------------------------------------|
| Production (Railway) | `https://travel-discovery-backend-production.up.railway.app` |

All paths below are relative to the base URL, e.g.
`POST https://travel-discovery-backend-production.up.railway.app/api/v1/auth/login`.

### Interactive docs (Swagger UI)

The backend ships with OpenAPI/Swagger UI (public):

- **Swagger UI:** `<base-url>/swagger-ui.html`
- **OpenAPI JSON:** `<base-url>/v3/api-docs`

Use it to try requests live. This guide is the curated companion — read it for
auth flow, conventions, and example payloads.

### CORS

The backend allows exactly **one** origin, configured by the `FRONTEND_URL`
env var (defaults to `http://localhost:5173`). Requests are made with
credentials enabled, so:

- Your deployed frontend origin must match `FRONTEND_URL` **exactly** (scheme +
  host + port, no trailing slash).
- Allowed methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS`.

If you get a CORS error in the browser, the origin almost certainly doesn't
match `FRONTEND_URL` on the server.

---

## 2. Authentication

JWT bearer tokens. Register or log in to get an **access token**, then send it
on protected requests:

```
Authorization: Bearer <accessToken>
```

- **Access token TTL:** 30 minutes (`jwt.expiration-ms`, default `1800000`).
- **Refresh token TTL:** 7 days (`jwt.refresh-expiration-ms`, default `604800000`).
- A `refreshToken` is returned by register/login, **but there is currently no
  refresh endpoint**. When the access token expires you'll start getting
  `401`s — handle this by sending the user back through `/auth/login`. (Store
  the refresh token for when a refresh endpoint is added.)
- **Logout** blacklists the current access token server-side (via Redis) until
  it would have expired, so a logged-out token can't be reused.

### Which endpoints need auth?

| Area                                                                              | Auth required? |
|-----------------------------------------------------------------------------------|----------------|
| `POST/DELETE /hotels/{id}/reviews` (write/delete a review)                        | ✅ Yes          |
| `/users/**`                                                                       | ✅ Yes          |
| `/favourites/**`                                                                  | ✅ Yes          |
| `/bookings/**`                                                                    | ✅ Yes          |
| Everything else (hotel search/listing/detail, reading reviews, locations, health) | ❌ Public       |

The app is intentionally usable **without** logging in — browsing hotels,
reading reviews, and locations are all public.

---

## 3. Standard response shapes

### Error response

Every handled error returns this shape:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Hotel not found",
  "path": "/api/v1/hotels/999"
}
```

### Validation error (HTTP 400)

When request-body validation fails, a `fieldErrors` map is included:

```json
{
  "status": 400,
  "error": "Validation failed",
  "message": "One or more fields are invalid",
  "path": "/api/v1/auth/register",
  "fieldErrors": {
    "email": "Email must be valid",
    "password": "Password must be at least 8 characters"
  }
}
```

### Common status codes

| Code                        | Meaning                                                              |
|-----------------------------|----------------------------------------------------------------------|
| `200 OK`                    | Success                                                              |
| `201 Created`               | Resource created (register, booking, review, add favourite)          |
| `204 No Content`            | Success with no body (logout, remove favourite, delete review)       |
| `400 Bad Request`           | Validation failed / bad input (see `fieldErrors`)                    |
| `401 Unauthorized`          | Missing/invalid/expired token, or wrong login credentials            |
| `403 Forbidden`             | Authenticated but not allowed (e.g. editing someone else's resource) |
| `404 Not Found`             | Resource doesn't exist                                               |
| `409 Conflict`              | e.g. email already registered                                        |
| `500 Internal Server Error` | Unexpected — message is generic, check server logs                   |

### Pagination

Paginated endpoints return:

```json
{
  "content": [
    /* array of items */
  ],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5,
  "last": false
}
```

Pass `?page=<0-based>&size=<n>` as query params.

---

## 4. Endpoint reference

### 4.1 Auth — `/api/v1/auth`

#### `POST /auth/register` → `201`

```json
// request
{
  "fullName": "Ada Lovelace",
  "email": "ada@example.com",
  "password": "secret12"
}
```

Validation: `fullName` 2–100 chars, `email` valid, `password` ≥ 8 chars.

```json
// response (AuthResponse)
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "user": {
    "id": 1,
    "fullName": "Ada Lovelace",
    "email": "ada@example.com",
    "avatarUrl": null,
    "createdAt": "2026-06-01T18:00:00Z"
  }
}
```

`409` if the email is already registered.

#### `POST /auth/login` → `200`

```json
// request
{
  "email": "ada@example.com",
  "password": "secret12"
}
```

Returns the same `AuthResponse` shape. `401` ("Invalid email or password") on bad credentials.

#### `POST /auth/logout` → `204`

Send the access token in the `Authorization` header. The token is blacklisted
server-side. No body. (Safe to call even without a header — it just no-ops.)

---

### 4.2 Users — `/api/v1/users` 🔒 (all require auth)

#### `GET /users/me` → `200`

```json
// response (UserResponse)
{
  "id": 1,
  "fullName": "Ada Lovelace",
  "email": "ada@example.com",
  "avatarUrl": "https://.../uploads/avatars/abc",
  "createdAt": "2026-06-01T18:00:00Z"
}
```

#### `PUT /users/me` → `200`

Update profile name and/or confirm a staged avatar.

```json
// request (UpdateProfileRequest) — both fields optional
{
  "fullName": "Ada L.",
  "avatarTempId": "f2c9...-staged-id"
}
```

- `fullName`: 2–100 chars if provided.
- `avatarTempId`: an id from the avatar staging call (below). When present, the
  staged image is promoted to the user's avatar and the previous one is deleted.

Returns the updated `UserResponse`.

#### `POST /users/me/avatar/temp` → `200`  *(multipart)*

Stage a profile image. This does **not** attach it to the user yet.

```
Content-Type: multipart/form-data
form field: file=<image file>
```

```json
// response (AvatarUploadResponse)
{
  "tempId": "f2c9...-staged-id",
  "previewUrl": "https://.../uploads/temp/f2c9..."
}
```

Max file size **5 MB** (returns `400 "Uploaded file is too large"` if exceeded).

**Two-step avatar flow:**

1. `POST /users/me/avatar/temp` with the file → get `tempId` + `previewUrl` (show preview).
2. `PUT /users/me` with `{ "avatarTempId": tempId }` → saves it as the avatar.

---

### 4.3 Hotels — `/api/v1/hotels` (public)

#### `GET /hotels/search` → `200`

Live search that ingests fresh results (within API quota) then returns
DB-backed hotels. Use this for the main search box.

| Query param | Required | Notes                                       |
|-------------|----------|---------------------------------------------|
| `country`   | ✅        | e.g. `Spain` — must be a known location     |
| `city`      | ✅        | e.g. `Barcelona` — must be a known location |
| `checkIn`   | ➖        | `YYYY-MM-DD`                                |
| `checkOut`  | ➖        | `YYYY-MM-DD`                                |
| `adults`    | ➖        | default `1`                                 |

```
GET /api/v1/hotels/search?country=Spain&city=Barcelona&checkIn=2026-08-07&checkOut=2026-08-13&adults=2
```

Returns a **plain array** of `HotelResponse` (not paginated). Unknown
country/city → `400`.

#### `GET /hotels` → `200` (paginated)

Catalog listing with filters, for the hotel list page.

| Query param  | Required | Default                                |
|--------------|----------|----------------------------------------|
| `country`    | ✅        | —                                      |
| `city`       | ✅        | —                                      |
| `starRating` | ➖        | (none) — filter to an exact rating 1–5 |
| `page`       | ➖        | `0`                                    |
| `size`       | ➖        | `12`                                   |

Returns `PageResponse<HotelResponse>`.

#### `GET /hotels/{id}` → `200`

Single hotel by its (Long) id. `404` if not found.

**`HotelResponse` shape:**

```json
{
  "id": 12,
  "name": "Hotel Barcelona Center",
  "description": "...",
  "country": "Spain",
  "city": "Barcelona",
  "address": "...",
  "latitude": 41.3874,
  "longitude": 2.1686,
  "pricePerNight": 180.00,
  "starRating": 4,
  "mainImageUrl": "https://...",
  "isFeatured": false,
  "amenities": [
    "WIFI",
    "POOL",
    "GYM"
  ],
  "imageUrls": [
    "https://...",
    "https://..."
  ],
  "averageRating": 4.3,
  "reviewCount": 27
}
```

---

### 4.4 Reviews — `/api/v1/hotels/{id}/reviews`

#### `GET /hotels/{id}/reviews` → `200` (public, paginated)

Query params: `page` (default `0`), `size` (default `10`). Returns
`PageResponse<ReviewResponse>`.

```json
// ReviewResponse
{
  "id": 5,
  "userId": 1,
  "reviewerName": "Ada Lovelace",
  "reviewerAvatarUrl": "https://...",
  "rating": 5,
  "comment": "Great stay!",
  "createdAt": "2026-06-01T18:00:00Z"
}
```

#### `POST /hotels/{id}/reviews` → `201` 🔒

Create **or update** the caller's review for this hotel (one review per user per hotel).

```json
// request (ReviewRequest)
{
  "rating": 5,
  "comment": "Great stay!"
}
```

`rating` is required, 1–5. `comment` optional, ≤ 2000 chars. Returns `ReviewResponse`.

#### `DELETE /hotels/{hotelId}/reviews/{id}` → `204` 🔒

Delete the caller's own review. `403` if it isn't yours.

---

### 4.5 Bookings — `/api/v1/bookings` 🔒 (all require auth)

#### `POST /bookings` → `201`

```json
// request (BookingRequest)
{
  "hotelId": 12,
  "checkIn": "2026-08-07",
  "checkOut": "2026-08-13",
  "guestCount": 2,
  "specialRequests": "High floor"
}
```

- `checkIn` / `checkOut`: required, must be in the **future** (`YYYY-MM-DD`).
- `guestCount`: ≥ 1.
- `specialRequests`: optional.

```json
// response (BookingResponse)
{
  "id": 100,
  "hotelId": 12,
  "hotelName": "Hotel Barcelona Center",
  "hotelCity": "Barcelona",
  "hotelCountry": "Spain",
  "hotelImageUrl": "https://...",
  "hotelLatitude": 41.3874,
  "hotelLongitude": 2.1686,
  "checkIn": "2026-08-07",
  "checkOut": "2026-08-13",
  "guestCount": 2,
  "totalPrice": 1080.00,
  "status": "CONFIRMED",
  "createdAt": "2026-06-01T18:00:00Z"
}
```

`totalPrice` is computed server-side (nights × price per night).

#### `GET /bookings` → `200` (paginated)

The caller's bookings. Query params: `page` (default `0`), `size` (default
`10`). Returns `PageResponse<BookingResponse>`, newest first.

#### `DELETE /bookings/{id}` → `200`

Cancel a booking. Returns the updated `BookingResponse` with `status: "CANCELLED"`.
`404`/`403` if it isn't the caller's booking.

**`status` values:** `PENDING`, `CONFIRMED`, `CANCELLED`.

---

### 4.6 Favourites — `/api/v1/favourites` 🔒 (all require auth)

#### `GET /favourites` → `200`

Returns a **plain array** of `HotelResponse` (the caller's favourited hotels).

#### `POST /favourites/{hotelId}` → `201`

Add a hotel to favourites. No body.

#### `DELETE /favourites/{hotelId}` → `204`

Remove a hotel from favourites. No body.

---

### 4.7 Locations — `/api/v1/locations` (public)

Use these to populate country/city pickers. Results are cached server-side.

#### `GET /locations/countries` → `200`

```json
[
  {
    "id": "1",
    "name": "Spain"
  },
  {
    "id": "2",
    "name": "France"
  }
]
```

#### `GET /locations/countries/cities` → `200`

Every country with its cities nested:

```json
[
  {
    "id": "1",
    "name": "Spain",
    "cities": [
      {
        "id": "10",
        "name": "Barcelona"
      },
      {
        "id": "11",
        "name": "Madrid"
      }
    ]
  }
]
```

#### `GET /locations/countries/{countryId}/cities` → `200`

```json
[
  {
    "id": "10",
    "name": "Barcelona"
  },
  {
    "id": "11",
    "name": "Madrid"
  }
]
```

> Note: hotel search validates `country`/`city` against this reference data, so
> drive your search inputs from these endpoints to avoid `400`s.

---

### 4.8 Health — `GET /health` → `200` (public)

Liveness probe. Returns `{ "status": "UP", "timestamp": "..." }`.

### 4.9 Admin — `POST /api/v1/admin/cache/reload`

Operational endpoint (not for normal frontend use). Requires a fixed token in
the `jwt-token` header matching the server's `ADMIN_RELOAD_TOKEN`. `401` otherwise.

---

## 5. Reference data (enums)

**Amenities** (`HotelResponse.amenities` / search filter):
`WIFI`, `POOL`, `GYM`, `SPA`, `RESTAURANT`, `PARKING`, `PET_FRIENDLY`, `KIDS_ACTIVITIES`

**Booking status:** `PENDING`, `CONFIRMED`, `CANCELLED`

**Star rating:** integer `1`–`5`.

---

## 6. Typical flows

### Guest browsing (no auth)

1. `GET /locations/countries/cities` → build the location picker.
2. `GET /hotels/search?country=Spain&city=Barcelona&...` → show results.
3. `GET /hotels/{id}` + `GET /hotels/{id}/reviews` → hotel detail page.

### Authenticated user

1. `POST /auth/register` or `POST /auth/login` → store `accessToken`.
2. Send `Authorization: Bearer <accessToken>` on all 🔒 calls.
3. `POST /favourites/{hotelId}`, `POST /bookings`, `POST /hotels/{id}/reviews`, etc.
4. On `401`, redirect to login (no refresh endpoint yet).
5. `POST /auth/logout` to invalidate the token.

### Updating avatar

1. `POST /users/me/avatar/temp` (multipart `file`) → `{ tempId, previewUrl }`.
2. Show `previewUrl` to the user.
3. `PUT /users/me` with `{ "avatarTempId": tempId }` to save.

---

## 7. Quick reference table

| Method | Path                                      |  Auth  | Body                 | Returns                         |
|--------|-------------------------------------------|:------:|----------------------|---------------------------------|
| POST   | `/auth/register`                          |   –    | RegisterRequest      | AuthResponse (201)              |
| POST   | `/auth/login`                             |   –    | LoginRequest         | AuthResponse                    |
| POST   | `/auth/logout`                            | bearer | –                    | 204                             |
| GET    | `/users/me`                               |   🔒   | –                    | UserResponse                    |
| PUT    | `/users/me`                               |   🔒   | UpdateProfileRequest | UserResponse                    |
| POST   | `/users/me/avatar/temp`                   |   🔒   | multipart `file`     | AvatarUploadResponse            |
| GET    | `/hotels/search`                          |   –    | query params         | HotelResponse[]                 |
| GET    | `/hotels`                                 |   –    | query params         | PageResponse\<HotelResponse>    |
| GET    | `/hotels/{id}`                            |   –    | –                    | HotelResponse                   |
| GET    | `/hotels/{id}/reviews`                    |   –    | query params         | PageResponse\<ReviewResponse>   |
| POST   | `/hotels/{id}/reviews`                    |   🔒   | ReviewRequest        | ReviewResponse (201)            |
| DELETE | `/hotels/{hotelId}/reviews/{id}`          |   🔒   | –                    | 204                             |
| POST   | `/bookings`                               |   🔒   | BookingRequest       | BookingResponse (201)           |
| GET    | `/bookings`                               |   🔒   | query params         | PageResponse\<BookingResponse>  |
| DELETE | `/bookings/{id}`                          |   🔒   | –                    | BookingResponse                 |
| GET    | `/favourites`                             |   🔒   | –                    | HotelResponse[]                 |
| POST   | `/favourites/{hotelId}`                   |   🔒   | –                    | 201                             |
| DELETE | `/favourites/{hotelId}`                   |   🔒   | –                    | 204                             |
| GET    | `/locations/countries`                    |   –    | –                    | CountryResponse[]               |
| GET    | `/locations/countries/cities`             |   –    | –                    | CountryResponse[] (with cities) |
| GET    | `/locations/countries/{countryId}/cities` |   –    | –                    | CityResponse[]                  |
| GET    | `/health`                                 |   –    | –                    | status object                   |
