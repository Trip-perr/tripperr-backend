# Tripperr API

Spring Boot 3.4 / Java 21 backend for the **Tripperr** travel planning platform.

See [`ARCHITECTURE.md`](./ARCHITECTURE.md) for the full design notes and migration history.

## Quick start

### Option A — Docker Compose (recommended)

```bash
cp .env.example .env
# Edit JWT_SECRET to a 32+ char value before running

docker compose up --build
```

API:        http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html
Health:     http://localhost:8080/actuator/health

### Option B — Local JDK + Mongo

Requires Java 21, Maven, and a local Mongo on `localhost:27017`.

```bash
# macOS
brew services start mongodb-community@8.0

export SPRING_PROFILES_ACTIVE=dev
export JWT_SECRET=please-change-me-to-a-strong-secret-of-at-least-32-chars

./mvnw spring-boot:run
```

## Common endpoints

| Method | Path                | Description                       |
|--------|---------------------|-----------------------------------|
| POST   | `/api/v1/auth/register` | Register a new user             |
| POST   | `/api/v1/auth/login`    | Login → access + refresh tokens |
| POST   | `/api/v1/auth/refresh`  | Rotate refresh token            |
| GET    | `/api/v1/users/me`      | Current user (requires JWT)     |
| GET    | `/api/v1/places`        | List places (public)            |
| GET    | `/api/v1/places/search` | Free‑text place search (public) |
| GET    | `/api/v1/trips`         | Trips visible to current user   |
| POST   | `/api/v1/trips`         | Create a trip                   |

All authenticated endpoints expect `Authorization: Bearer <accessToken>`.

## Configuration

Configuration is YAML + env‑var overrides. See `application.yml` and the profile files; key vars:

| Var                    | Default                 | Notes                                |
|------------------------|-------------------------|--------------------------------------|
| `SPRING_PROFILES_ACTIVE` | `dev`                 | `dev` / `staging` / `prod`           |
| `MONGODB_URI`          | `mongodb://localhost:27017/tripperr` | full URI; required in prod |
| `JWT_SECRET`           | (dev fallback)          | **must** be ≥32 chars in prod        |
| `JWT_ACCESS_TTL`       | `15` (minutes)          |                                      |
| `JWT_REFRESH_TTL`      | `30` (days)             |                                      |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | comma‑separated |

## Build & test

```bash
./mvnw -B clean verify          # unit + integration tests (Testcontainers)
./mvnw spring-boot:run          # local run
docker build -t tripperr-api .  # container build
```

CI runs on every push and PR — see `.github/workflows/maven.yml`.
