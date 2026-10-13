# ☕ Field Notes — Enterprise REST Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.8-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-Stateless%20JWT-6DB33F)](https://spring.io/projects/spring-security)
[![Hibernate](https://img.shields.io/badge/ORM-Hibernate%207%20%2F%20JPA-59666C?logo=hibernate&logoColor=white)](https://hibernate.org)
[![Database](https://img.shields.io/badge/Databases-MySQL%20%7C%20PostgreSQL%20%7C%20H2-4479A1)](https://www.mysql.com)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org)

The **Field Notes REST Backend** is a high-performance, offline-first service built for the Field Notes mobile and web inspection platform. It orchestrates user authentication, inspection records, customer and site catalogs, multipart photo uploads, and bi-directional delta synchronization with conflict resolution.

---

## 🚀 Key Architectural Features

- 🔄 **Bi-Directional Delta Synchronization Engine**:
  - **Batch Push (`POST /api/sync/push`)**: Upserts client-generated records in a single database transaction.
  - **Delta Pull (`GET /api/sync/pull?since=...`)**: Returns only records modified since the requested UTC timestamp.
  - **Conflict Resolution**: Implements deterministic **Last-Write-Wins (LWW)** using entity-level `updatedAt` ISO-8601 timestamps.
  - **Soft-Delete Propagation**: Marks records as `deleted = true` so remote clients clean up locally deleted entities.
- 🛡️ **Multi-Tenant Data Isolation**: Every customer, site, and field note is strictly scoped to the authenticated user. User records cannot be accessed, modified, or synchronized across accounts.
- 🔐 **Stateless Security with JWT**: HMAC-SHA256 signed JWT tokens (24-hour expiration) validate every request via Spring Security filters.
- 🗄️ **Multi-Database Support**: Out-of-the-box profiles for:
  - **H2** (in-memory zero-setup for testing & local development)
  - **MySQL 8+** (default production database with Hikari connection pooling)
  - **PostgreSQL 15+** (cloud production profile)
- 📸 **Built-in Media Storage**: Multipart photo upload endpoint (`/api/photos`) with static file serving (`/uploads/**`) and file type validation.
- 🌱 **Automated Seed Data**: Initializes a demo technician account on boot if not already present.

---

## 🏛 Relational Data Model

Every core entity implements an offline-first contract with `uuid` (client-generated UUID), `updatedAt` (instant of modification), `deleted` (soft-delete flag), and `@Version` (optimistic locking):

```
┌──────────────┐         1 : N         ┌──────────────────┐
│     User     │ ────────────────────> │     Customer     │
│  (Auth & ID) │                       │ (uuid, name, ..) │
└──────────────┘                       └──────────────────┘
                                                │
                                                │ 1 : N
                                                ▼
┌───────────────────┐        N : 1     ┌──────────────────┐
│     FieldNote     │ <─────────────── │       Site       │
│  (GPS, Photo,     │                  │ (uuid, address)  │
│   Status, Date)   │                  └──────────────────┘
└───────────────────┘
```

---

## ⚡ Running Process (Step-by-Step)

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher ([Download OpenJDK 21](https://adoptium.net/))
- **Maven**: Pre-configured via the included `./mvnw` (Linux/macOS) and `.\mvnw.cmd` (Windows) wrappers.

---

### Option 1: In-Memory H2 Database (Recommended for Fast Local Demo)
*Zero database installation required. Creates tables, runs in RAM, and seeds demo data automatically.*

```bash
# Linux / macOS
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2

# Windows PowerShell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

- **Server Port**: `http://localhost:8080`
- **H2 Web Console**: `http://localhost:8080/h2-console`
  - **JDBC URL**: `jdbc:h2:mem:fieldnotes`
  - **User**: `sa` | **Password**: *(leave blank)*

---

### Option 2: MySQL (Default Configuration)

1. **Create the MySQL Database**:
   ```sql
   CREATE DATABASE field_notes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Verify Credentials in `src/main/resources/application.yml`**:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/field_notes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
       username: root
       password: your_mysql_password
   ```

3. **Start the Service**:
   ```bash
   # Linux / macOS
   ./mvnw spring-boot:run

   # Windows PowerShell
   .\mvnw.cmd spring-boot:run
   ```

---

### Option 3: PostgreSQL

1. **Create the PostgreSQL Database**:
   ```sql
   CREATE DATABASE field_notes;
   ```

2. **Run with the Postgres Profile**:
   ```bash
   # Pass database credentials via environment variables or CLI:
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres \
     -Dspring.datasource.url=jdbc:postgresql://localhost:5432/field_notes \
     -Dspring.datasource.username=postgres \
     -Dspring.datasource.password=postgres
   ```

---

### Option 4: Build and Run Standalone JAR

```bash
# Package the executable JAR
./mvnw clean package -DskipTests

# Run the JAR
java -jar target/field-notes-backend-1.0.0.jar --spring.profiles.active=h2
```

---

## 👤 Default Seed Credentials

On startup, `DataInitializer` automatically inserts a demo user if the database is clean:

| Field | Value |
| :--- | :--- |
| **Email** | `worker@fieldnotes.com` |
| **Password** | `Password@123` |
| **Technician Name** | `Alex Morgan` |
| **Pre-loaded Customer** | `Apex Industrial Power` |
| **Pre-loaded Site** | `West Valley Transformer Station #4` |

---

## 📡 REST API Reference

All requests other than `/api/auth/**` require the following header:
```http
Authorization: Bearer <your_jwt_token>
```

### 1. Authentication
| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user | `{"email": "...", "password": "...", "name": "..."}` |
| `POST` | `/api/auth/login` | Authenticate user | `{"email": "...", "password": "..."}` |

**Response Format:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "email": "worker@fieldnotes.com",
  "name": "Alex Morgan"
}
```

---

### 2. Customers
| Method | Endpoint | Description | Request Body / Query |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/customers` | List all active customers for user | — |
| `POST` | `/api/customers` | Create new customer | `{"name": "...", "contactInfo": "..."}` |
| `GET` | `/api/customers/{uuid}` | Fetch customer by UUID | — |
| `PUT` | `/api/customers/{uuid}` | Update customer details | `{"name": "...", "contactInfo": "..."}` |
| `DELETE` | `/api/customers/{uuid}` | Soft-delete customer | — |
| `GET` | `/api/customers/{uuid}/sites`| List all sites for customer | — |

---

### 3. Sites
| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/sites` | List all active sites for user | — |
| `POST` | `/api/sites` | Create site linked to customer | `{"customerUuid": "...", "siteName": "...", "address": "..."}` |
| `GET` | `/api/sites/{uuid}` | Fetch site by UUID | — |
| `PUT` | `/api/sites/{uuid}` | Update site details | `{"customerUuid": "...", "siteName": "...", "address": "..."}` |
| `DELETE` | `/api/sites/{uuid}` | Soft-delete site | — |

---

### 4. Field Notes
| Method | Endpoint | Description | Request Body / Query Params |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/notes` | Search and filter notes | Query Params: `search`, `status`, `siteUuid` |
| `POST` | `/api/notes` | Create a new field note | `{"siteUuid": "...", "title": "...", "description": "...", "location": "...", "dateTime": "...", "status": "OPEN", "photoUrl": "..."}` |
| `GET` | `/api/notes/{uuid}` | Fetch field note by UUID | — |
| `PUT` | `/api/notes/{uuid}` | Update existing field note | Same as POST body |
| `DELETE` | `/api/notes/{uuid}` | Soft-delete field note | — |

**Note Status Options:** `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`.

---

### 5. Photos & Media Storage
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/photos` | Upload image (multipart/form-data with key `file`). Returns `{"url": "/uploads/<stored_name>.jpg"}` |
| `GET` | `/uploads/{filename}` | Statically serves uploaded photo |

---

### 6. Offline Synchronization Core
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/sync/push` | Batch pushes modified client records (`customers`, `sites`, `notes`). Returns `{"serverTime": "<ISO-8601>"}` |
| `GET` | `/api/sync/pull` | Fetches delta updates modified after `?since=<ISO-8601>`. Omit `since` for full state recovery. |

---

## 🧪 Testing with cURL

### 1. Authenticate and Save Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"worker@fieldnotes.com","password":"Password@123"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"
```

### 2. List Customers
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Create a Field Note
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "siteUuid": "<SITE_UUID_HERE>",
    "title": "HVAC Unit Pressure Leak Detected",
    "description": "Found high vibration and minor refrigerant drop at Compressor 2.",
    "location": "37.7749,-122.4194",
    "dateTime": "2026-10-13T10:30:00Z",
    "status": "OPEN"
  }'
```

### 4. Trigger Sync Pull
```bash
curl -X GET "http://localhost:8080/api/sync/pull?since=2026-10-01T00:00:00Z" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📦 Project Directory Layout

```
src/
├── main/
│   ├── java/com/atilimited/fieldnotes/
│   │   ├── config/          # WebMvc (uploads static handler), DataInitializer
│   │   ├── controller/      # Auth, Customer, Site, FieldNote, File, Sync
│   │   ├── dto/             # Request & Response Data Transfer Objects
│   │   ├── exception/       # Global exception handling & API error responses
│   │   ├── model/           # JPA Entities: User, Customer, Site, FieldNote, NoteStatus
│   │   ├── repository/      # Spring Data JPA repositories with user-isolated queries
│   │   ├── security/        # JWT utility, UserDetailsService, AuthFilter
│   │   └── service/         # Business logic & Sync engine reconciliation
│   └── resources/
│       ├── application.yml           # Default MySQL configuration
│       └── application-postgres.yml  # PostgreSQL production profile
└── test/                             # Unit and integration test suites
```
