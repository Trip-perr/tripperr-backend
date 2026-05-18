# Running Tripperr Locally

## Prerequisites

- Java 21 (e.g. via [SDKMAN](https://sdkman.io/): `sdk install java 21`)
- Docker & Docker Compose (for MongoDB)
- Maven wrapper included — no separate Maven install needed

---

## Option A: Local Dev (recommended)

### 0. Go to the backend folder

From `~/Documents/Projects`:

```bash
cd tripperr-be/tripperr-backend
```

If your prompt already ends with `tripperr-be`, use:

```bash
cd tripperr-backend
```

### 1. Start MongoDB via Docker

```bash
docker compose up -d mongodb
```

This starts MongoDB on `localhost:27017` with a `tripperr` database.

If `docker compose` prints `unknown command: docker compose`, Docker Compose is not installed/enabled in your Docker CLI. Install or enable Docker Compose v2, then re-run the command above.

If you have the older standalone Compose binary instead, use:

```bash
docker-compose up -d mongodb
```

### 2. Run the Spring Boot app

Make sure Maven uses Java 21 or newer:

```bash
java -version
./mvnw -v
```

If Maven shows Java 17 on your machine, point `JAVA_HOME` at your installed Java 23:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 23)
export PATH="$JAVA_HOME/bin:$PATH"
```

```bash
./mvnw spring-boot:run
```

The `dev` profile is active by default. The API starts on **http://localhost:8080**.

---

## Option B: Full Docker Compose (API + MongoDB)

```bash
# From ~/Documents/Projects:
cd tripperr-be/tripperr-backend

# If you are already in tripperr-be:
# cd tripperr-backend

# Create a .env file with required secrets
echo "JWT_SECRET=a-dev-secret-that-is-at-least-32-characters-long" > .env

docker compose up --build
```

The API starts on **http://localhost:8080**.

---

## Verify the App is Running

```bash
curl http://localhost:8080/actuator/health
```

Expected response: `{"status":"UP"}`

---

## Explore the API

Swagger UI: **http://localhost:8080/swagger-ui.html**

API docs (OpenAPI JSON): **http://localhost:8080/v3/api-docs**

Postman collection: `tripperr-be/tripperr-backend/Tripperr.postman_collection.json`

---

## Environment Variables (Option A overrides)

All have sensible defaults for local dev. Override via env or a `.env` file if needed:

| Variable | Default | Description |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/tripperr` | MongoDB connection string |
| `JWT_SECRET` | `CHANGE_ME_DEV_ONLY_...` | JWT signing secret (≥32 chars) |
| `JWT_ACCESS_TTL` | `15` | Access token TTL in minutes |
| `JWT_REFRESH_TTL` | `30` | Refresh token TTL in days |
| `SERVER_PORT` | `8080` | HTTP port |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Allowed CORS origins |

---

## Stop the App

```bash
# Stop MongoDB container
docker compose down

# Or stop everything (Option B)
docker compose down -v   # -v also removes the mongo_data volume
```
