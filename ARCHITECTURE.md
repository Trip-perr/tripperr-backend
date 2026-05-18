# Tripperr Backend — Architecture

## 1. Goals

Tripperr is a travel planning platform. The backend is intentionally a **modular monolith**: one deployable Spring Boot service, but internally organized as feature modules with strict package boundaries so individual modules can be peeled off into microservices later without rewrites.

Optimization order:

1. Maintainability and developer velocity
2. Security and correctness
3. Scalability (read‑heavy, social discovery, mobile clients)
4. Cost efficiency (one Mongo, one JVM, until traffic justifies more)

## 2. Tech stack

| Concern        | Choice                                   |
|---------------|------------------------------------------|
| Language       | Java 21 (records, virtual threads ready) |
| Framework      | Spring Boot 3.4.x                        |
| Persistence    | MongoDB (Spring Data, auditing, indexes) |
| Security       | Spring Security 6 + JWT (HS256, jjwt 0.12) |
| Validation     | `jakarta.validation` (Hibernate Validator)|
| API docs       | springdoc-openapi (Swagger UI)           |
| Observability  | Spring Boot Actuator, Prometheus endpoint|
| Tests          | JUnit 5, Mockito, Testcontainers (Mongo) |
| Build/CI       | Maven, GitHub Actions                    |
| Container      | Multi‑stage Docker, JRE 21 Alpine        |

## 3. Package structure

```
com.tripperr.api
├── TripperrApplication             (boot entry, @EnableMongoAuditing)
├── common/                         (ApiResponse, ApiError, PageResponse, BaseDocument)
├── config/                         (SecurityConfig, OpenApiConfig)
├── security/                       (JwtService, JwtAuthenticationFilter,
│                                    AppUserDetailsService, AppUserPrincipal,
│                                    SecurityContextHelper, *Properties)
├── exception/                      (GlobalExceptionHandler + typed exceptions)
├── auth/                           (register / login / refresh / logout)
│   ├── AuthController, AuthService
│   ├── dto/                        (records, jakarta.validation)
│   ├── model/RefreshToken
│   └── repository/RefreshTokenRepository
├── user/
│   ├── UserController (/me)
│   ├── dto/UserResponse
│   ├── model/{User, Role}
│   └── repository/UserRepository
├── trip/
│   ├── TripPlanController, TripPlanService
│   ├── dto/{TripPlanRequest, TripPlanResponse, TripDayDto, ActivityDto}
│   ├── model/{TripPlan, TripDay, Activity, Visibility, TripType, ActivityCategory}
│   ├── repository/TripPlanRepository
│   └── mapper/TripPlanMapper
└── place/
    ├── VacationPlaceController, VacationPlaceService
    ├── dto/{VacationPlaceRequest, VacationPlaceResponse, CoordinatesDto}
    ├── model/{VacationPlace, Coordinates}
    ├── repository/VacationPlaceRepository
    └── mapper/VacationPlaceMapper
```

Rules of the modular monolith:

- A feature module (`trip`, `place`, `user`, `auth`) **must not** reach into another module's `repository` or `model` packages. Cross‑module use goes through the public service API (the controller/service classes at the module root).
- `common`, `security`, `exception`, `config` are shared infrastructure — anyone may import them.
- DTOs live in the module that owns the endpoint; never expose `@Document` entities over HTTP.

## 4. Security architecture

- **Stateless**: `SessionCreationPolicy.STATELESS`, no `JSESSIONID`.
- **Passwords**: BCrypt strength 12, never logged.
- **Tokens**: two HS256 JWTs per session.
  - Access token: short TTL (default 15 min), carries `sub`, `roles`, `email`, `typ=ACCESS`.
  - Refresh token: long TTL (default 30 d), `typ=REFRESH`, **hash stored** in `refresh_tokens` (SHA‑256 of the raw token). On refresh we rotate: the old record is revoked, a new one is issued.
- **Secret**: `app.security.jwt.secret` — validated at startup to be ≥32 bytes, fails fast otherwise.
- **Filter chain**: `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`. Public routes are `/api/v1/auth/**`, `/api/v1/places/**` (GET), `actuator/health|info`, swagger.
- **Authorization**: `@PreAuthorize("hasRole('ADMIN')")` for admin endpoints; resource‑level checks (owner / collaborators / visibility) live in services, not controllers.
- **CORS**: explicit allow‑list from `app.cors.*`.
- **Errors**: 401/403 emit `ApiResponse.fail` JSON via `AuthenticationEntryPoint` and `AccessDeniedHandler` — never HTML.

## 5. Domain model

### User

```
users {
  _id, name, email (unique, case‑insensitive),
  passwordHash, roles[USER|ADMIN],
  enabled, profileImageUrl,
  createdAt, updatedAt, version
}
```

### TripPlan (rich itinerary)

```
trip_plans {
  _id, title, description,
  destination (idx), country (idx),
  startDate, endDate,
  estimatedBudget, currency,
  tripType, coverImage, tags[],
  visibility (PUBLIC|PRIVATE),
  ownerId (idx), collaborators[userId],
  itineraryDays[
    { dayNumber, date, activities[
        { title, description, location, startTime, endTime,
          category, notes, estimatedCost }
    ] }
  ],
  createdAt, updatedAt, version
}
```

### VacationPlace (discovery)

```
vacation_places {
  _id, name (idx), city (idx), country (idx),
  description, categories[],
  rating, images[], bestTimeToVisit,
  estimatedBudget, currency, tags[],
  coordinates { lat, lng },
  createdAt, updatedAt, version
}
```

### RefreshToken

```
refresh_tokens {
  _id, tokenHash (unique), userId (idx),
  expiresAt, revoked, createdAt
}
```

Auditing is automatic via `@EnableMongoAuditing` + `BaseDocument` (`createdAt`, `updatedAt`, `version`).

## 6. API contracts (v1)

All endpoints under `/api/v1`. All responses wrapped in:

```json
{ "success": true, "data": { ... }, "timestamp": "..." }
```

or on failure:

```json
{ "success": false, "error": { "code": "VALIDATION_ERROR", "message": "...", "fieldErrors": {...}, "path": "..." }, "timestamp": "..." }
```

| Method | Path                       | Auth     | Purpose                                |
|--------|----------------------------|----------|----------------------------------------|
| POST   | /auth/register             | Public   | Register new user                      |
| POST   | /auth/login                | Public   | Email + password → tokens              |
| POST   | /auth/refresh              | Public   | Rotate refresh → new access+refresh    |
| POST   | /auth/logout               | User     | Revoke all refresh tokens for user     |
| GET    | /users/me                  | User     | Current user profile                   |
| GET    | /trips                     | User     | List accessible trips (query, page)    |
| GET    | /trips/mine                | User     | Trips owned by current user            |
| POST   | /trips                     | User     | Create trip                            |
| GET    | /trips/{id}                | User     | Get trip (owner/collab/public)         |
| PUT    | /trips/{id}                | User     | Full update (owner or collaborator)    |
| DELETE | /trips/{id}                | User     | Delete (owner only)                    |
| GET    | /places                    | Public   | List, filter by country/city, sort     |
| GET    | /places/search?query=&minRating= | Public | Search with regex                  |
| GET    | /places/{id}               | Public   | Get place                              |
| POST   | /places                    | Admin    | Create place                           |
| PUT    | /places/{id}               | Admin    | Update place                           |
| DELETE | /places/{id}               | Admin    | Delete place                           |

Pagination/sorting use Spring's standard `?page=0&size=20&sort=field,desc`.

OpenAPI UI: `http://localhost:8080/swagger-ui.html`.

## 7. Production readiness checklist

- [x] Externalized config (`application-{dev,staging,prod}.yml`, env‑driven)
- [x] Profiles via `SPRING_PROFILES_ACTIVE`
- [x] Actuator: `health` (with `liveness`/`readiness` probes), `info`, `metrics`, `prometheus`
- [x] Structured `@Slf4j` logging, dev/staging/prod log levels
- [x] Global exception handler with consistent error contract
- [x] Bean Validation on every request DTO
- [x] CORS allow‑list via config
- [x] Swagger / OpenAPI v3
- [x] Multi‑stage Dockerfile, non‑root user, container `HEALTHCHECK`
- [x] docker‑compose with Mongo health gating
- [x] CI: build + tests + dependency graph; PR also builds the Docker image
- [x] Testcontainers Mongo integration test for context wiring
- [x] Mongo auditing + unique/secondary indexes

## 8. Step‑by‑step migration strategy (executed in this refactor)

1. **Wipe** the old `com.example.test` package and demo `application.properties`.
2. **Rewrite `pom.xml`**: `groupId=com.tripperr`, `artifactId=tripperr-api`; remove JPA + javax.persistence; upgrade jjwt → 0.12.6; add validation, actuator, springdoc, lombok, testcontainers.
3. **Bring up the new package skeleton** under `com.tripperr.api` with `common/`, `config/`, `security/`, `exception/`, and feature modules.
4. **Introduce `BaseDocument`** with Spring Data auditing.
5. **Implement security**: `JwtProperties`, `JwtService` (jjwt 0.12), `AppUserPrincipal`, `AppUserDetailsService`, `JwtAuthenticationFilter`, new `SecurityConfig` with `EnableMethodSecurity` and STATELESS sessions.
6. **Implement Auth flow**: register/login/refresh/logout with BCrypt + refresh‑token rotation stored hashed in Mongo.
7. **Redesign TripPlan**: full itinerary structure, owner + collaborators + visibility, ownership checks in service, search query in repository.
8. **Redesign VacationPlace**: categories, rating, images, coordinates, regex search + min rating filter.
9. **DTO layer**: request DTOs with validation; response DTOs as records; mappers per module; controllers return `ApiResponse<T>`.
10. **GlobalExceptionHandler**: typed exceptions → consistent error codes.
11. **Profiles & env**: `application.yml` + `application-dev/staging/prod.yml`; `.env.example`; everything sensitive (`JWT_SECRET`, `MONGODB_URI`, CORS) is env‑driven.
12. **Docker**: multi‑stage build, non‑root user, healthcheck; `docker‑compose.yml` with Mongo health‑gated startup.
13. **CI**: Java 21 Temurin, `mvn -B -ntp clean verify`, upload Surefire reports, smoke‑build Docker image on PRs.
14. **Tests**: Testcontainers context test + `JwtService` unit tests; pattern in place for adding more.

## 9. Scalability recommendations

- **Read scaling**: Mongo replica set; read preference `secondaryPreferred` for `places` endpoints once traffic grows.
- **Caching**: introduce Spring Cache + Redis for `/places` list/search and `/users/me`. Cache key = query+page; TTL ~60 s.
- **Search**: when free‑text load grows beyond regex, move place search to MongoDB Atlas Search or Elasticsearch (read model rebuilt via change streams).
- **Async**: outgoing email/push/notification work goes onto a queue (SQS/Kafka). Spring + virtual threads (Java 21) for blocking I/O without thread pool tuning.
- **Rate limiting**: add Bucket4j filter at the edge; 60 req/min/IP on unauthenticated routes, 600 req/min/user authenticated.
- **Connection pooling**: tune Mongo `maxPoolSize` for prod (Spring Data default 100 is usually fine; raise for high‑concurrency).
- **Stateless**: no in‑memory session state — horizontal scale is free behind any L7 load balancer.
- **Observability**: scrape `actuator/prometheus`, dashboards for request rate, latency p50/p95/p99, error rate, Mongo op time; logs to a central sink (ELK/Datadog).
- **DB indexes** already declared:
  - `users.email` unique
  - `refresh_tokens.tokenHash` unique, `userId` indexed
  - `trip_plans.ownerId`, `destination`, `country`
  - `vacation_places.name`, `city`, `country`
- Add geospatial index `coordinates_2dsphere` on `vacation_places` when nearby‑search lands.

## 10. Future microservice split (the path, not the plan)

The package boundaries above were chosen so that, when traffic justifies it, each module becomes a service:

| Module            | Becomes service        | Owns                          |
|------------------|------------------------|-------------------------------|
| auth, user        | `identity-service`     | users, refresh tokens, OAuth  |
| trip              | `itinerary-service`    | trip_plans                    |
| place             | `discovery-service`    | places, search index          |
| (new)             | `media-service`        | image uploads, CDN signing    |
| (new)             | `notification-service` | email, push, in‑app           |

Extraction recipe per module:

1. Replace direct repository calls from other modules with a service interface (most are already isolated).
2. Move the module + its DTOs/models into a new Spring Boot project, keep the same package names to minimize churn.
3. Replace cross‑module calls with REST (sync) or events on Kafka (async).
4. Database split: each service gets its own Mongo cluster (or collection namespace) and migrates over via dual‑write + backfill.
5. Add an API gateway (Spring Cloud Gateway / Kong) and move JWT verification there.

## 11. Cost notes

- One small JVM (1 vCPU / 1 GiB) + one Mongo M10 covers low‑hundreds of concurrent users.
- Until you have meaningful read traffic, don't pay for Redis or Elastic.
- Build artifacts are layer‑cached in Docker so CI/CD stays cheap.

## 12. What's intentionally **not** done

- Email verification, password reset, OAuth/social login — design noted, not implemented (would add `PasswordResetToken`, transactional mailer, OAuth providers).
- Rate limiting filter — declared in checklist, not added (Bucket4j is a one‑class drop‑in).
- MapStruct — manual mappers are clearer at this scale and avoid annotation‑processor overhead.
- Liquibase/Flyway — Mongo doesn't need schema migrations; index creation is automatic via `auto-index-creation: true`.
