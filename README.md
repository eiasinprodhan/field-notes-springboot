# Field Notes — REST Backend

Offline-first backend for the **Field Notes** mobile app (ATI Limited take-home).
Field workers record notes during site visits; data is created online or offline
and synced automatically.

## Stack
- Java 21 + Spring Boot 4.0.8
- Spring Data JPA (Hibernate ORM 7)
- Spring Security 7 + JWT (stateless auth)
- MySQL (default) or H2 (dev profile, zero setup)
- Maven

## Run it

**With H2 (no DB install — recommended for first run / demo):**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
# or if you built the jar:
java -jar target/field-notes-backend-1.0.0.jar --spring.profiles.active=h2
```

**With MySQL:**
```sql
CREATE DATABASE fieldnotes CHARACTER SET utf8mb4;
```
Then edit the `spring.datasource.*` values in `application.yml` and run:
```bash
./mvnw spring-boot:run
```
The schema is created automatically (`ddl-auto: update`).

The H2 console (dev profile) is at http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:fieldnotes`).

## Data model (relational, no duplication)
```
User 1 ── * Customer 1 ── * Site 1 ── * FieldNote
```
Every row carries a client-generated `uuid`, an `updatedAt` version timestamp,
a soft-delete `deleted` flag, and a JPA `@Version` for optimistic locking. These
three fields (`uuid`, `updatedAt`, `deleted`) are the backbone of offline sync.

## Auth
| Method | Endpoint            | Body                              |
|--------|---------------------|-----------------------------------|
| POST   | /api/auth/register  | {email, password, name}           |
| POST   | /api/auth/login     | {email, password}                 |

Both return `{ token, email, name }`. Send the token as:
`Authorization: Bearer <token>` on every other request.

## REST API
All data is scoped to the authenticated user (data isolation).

### Customers
- `GET    /api/customers`            — list
- `POST   /api/customers`            — create {name, contactInfo}
- `GET    /api/customers/{uuid}`     — get
- `PUT    /api/customers/{uuid}`     — update
- `DELETE /api/customers/{uuid}`     — soft delete
- `GET    /api/customers/{uuid}/sites` — sites of a customer

### Sites
- `GET  /api/sites` · `POST /api/sites` {customerUuid, siteName, address}
- `GET /api/sites/{uuid}` · `PUT /api/sites/{uuid}` · `DELETE /api/sites/{uuid}`

### Field Notes
- `GET /api/notes` — list. Query params (combinable):
  - `search=text`  → matches title, description, site name, **or customer name**
  - `status=OPEN|IN_PROGRESS|RESOLVED|CLOSED`
  - `siteUuid=...`
- `POST /api/notes` {siteUuid, title, description, location, dateTime, status, photoUrl}
- `GET /api/notes/{uuid}` · `PUT /api/notes/{uuid}` · `DELETE /api/notes/{uuid}`

### Photos
- `POST /api/photos` (multipart `file`) → `{ "url": "/uploads/..." }`
  Store the returned URL in `FieldNote.photoUrl`.

### Sync (offline core)
- `POST /api/sync/push` — body is a `SyncPushRequest` containing batched
  `customers`, `sites`, `notes` (each with client `uuid` + `updatedAt` +
  `deleted`). Server upserts by uuid with **last-write-wins** on `updatedAt`.
  Returns `{ "serverTime": "<instant>" }`.
- `GET  /api/sync/pull?since=<ISO-8601-instant>` — returns everything changed
  after `since` (including soft-deleted rows). **Omit `since` for a full
  restore** (used after reinstall / cleared local data).

## Offline sync strategy (summary)
1. App stores everything locally (SQLite) with the same `uuid`/`updatedAt` model.
2. While offline: create/edit/delete locally, tracking a per-row "dirty" flag.
3. On reconnect: `POST /api/sync/push` the dirty batch, then
   `GET /api/sync/pull?since=<lastSync>` to fetch others' changes.
4. Merge by uuid; resolve conflicts by newer `updatedAt` (last-write-wins);
   apply soft-deletes.
5. After reinstall: log in → `GET /api/sync/pull` (no `since`) → full restore.

## Build
```bash
./mvnw clean package      # produces target/field-notes-backend-1.0.0.jar
```
