# Travel Discovery — Backend API

Spring Boot 3 REST API for the Travel Discovery platform.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Security | Spring Security 6 + JWT (JJWT 0.12) |
| ORM | Spring Data JPA + Hibernate 6 |
| DB | PostgreSQL 15 |
| Migrations | Flyway 10 |
| Build | Maven 3 + Java 17 |
| Mapping | MapStruct 1.5 |
| Utilities | Lombok |

## Prerequisites

- Java 17 LTS
- Maven 3.9+
- PostgreSQL 15 running locally (or Supabase/Render)

## Quick Start

### 1. Create the database

```sql
CREATE DATABASE travel_discovery;
```

### 2. Configure environment

Copy the example env file and fill in your values:

```bash
cp .env.example .env
```

Or set environment variables directly:

```
DB_URL=jdbc:postgresql://localhost:5432/travel_discovery
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=<base64-encoded 256-bit secret>
FRONTEND_URL=http://localhost:5173
```

**Generate a JWT secret:**
```bash
openssl rand -base64 32
```

### 3. Run

```bash
mvn spring-boot:run
```

Flyway will automatically create all tables on first run.
API is available at: `http://localhost:8080/api/v1`

## Project Structure

```
src/main/java/com/travel.discovery/
├── config/          # SecurityConfig, JpaConfig
├── controller/      # REST controllers
├── dto/
│   ├── request/     # Inbound request bodies
│   └── response/    # Outbound response bodies
├── entity/          # JPA entities
│   └── enums/       # Enum types
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT service, filter, UserDetailsService
└── service/
    └── impl/        # Service implementations

src/main/resources/
├── application.yml          # Main config (uses env vars)
├── application-prod.yml     # Production overrides
└── db/migration/            # Flyway SQL scripts (V1–V5)
```

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /api/v1/auth/register | No | Register new user |
| POST | /api/v1/auth/login | No | Login → JWT tokens |
| GET | /api/v1/hotels | No | Search hotels |
| GET | /api/v1/hotels/:id | No | Hotel detail |
| GET | /api/v1/hotels/:id/reviews | No | Hotel reviews |
| POST | /api/v1/bookings | Yes | Create booking |
| GET | /api/v1/bookings/my | Yes | My bookings |
| PUT | /api/v1/bookings/:id/cancel | Yes | Cancel booking |
| GET | /api/v1/users/me | Yes | My profile |
| PUT | /api/v1/users/me | Yes | Update profile |
| GET | /api/v1/favourites | Yes | Get favourites |
| POST | /api/v1/favourites/:hotelId | Yes | Add to favourites |
| DELETE | /api/v1/favourites/:hotelId | Yes | Remove from favourites |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | jdbc:postgresql://localhost:5432/travel_discovery | PostgreSQL JDBC URL |
| `DB_USERNAME` | postgres | Database username |
| `DB_PASSWORD` | postgres | Database password |
| `JWT_SECRET` | (dev placeholder) | Base64 256-bit secret — **change in production** |
| `JWT_EXPIRATION_MS` | 900000 | Access token TTL (15 min) |
| `JWT_REFRESH_MS` | 604800000 | Refresh token TTL (7 days) |
| `FRONTEND_URL` | http://localhost:5173 | Allowed CORS origin |
| `SERVER_PORT` | 8080 | Server port |

## Development Notes

- `ddl-auto=validate` — Hibernate validates against DB schema but **never modifies it**
- All schema changes must be written as a new Flyway migration in `db/migration/`
- Never commit `.env` or `application-local.yml`
- BCrypt strength 12 — intentionally slow for security
