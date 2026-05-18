You are a senior backend architect and staff-level Java engineer.

I am building a startup-grade travel planning platform called “Tripperr”.

Current stack:
- Java 21
- Spring Boot 3.4.1
- MongoDB
- Maven
- JWT authentication
- GitHub Actions

Current backend state:
- Early MVP skeleton exists
- CRUD APIs for trip plans and vacation places
- JWT filter exists
- MongoDB connectivity exists
- Security layer exists
- Application is NOT production ready

Your task:
Refactor and redesign this backend into a scalable, production-grade architecture while keeping it monolithic for now (modular monolith approach).

IMPORTANT:
Do NOT overengineer into microservices yet.
The goal is:
- clean architecture
- scalability
- maintainability
- security
- production readiness
- startup velocity

==================================================
CURRENT PROBLEMS TO FIX
==================================================

1. Hardcoded authentication:
- Current auth only accepts:
  user/password
- Replace with real user management.

2. Broken persistence configuration:
- Some entities use JPA annotations/repositories
- App actually uses MongoDB only
- Remove JPA completely unless truly needed.

3. JWT secret is hardcoded.
- Move to environment variables/config.

4. Repository mismatch:
- MongoRepository<TripPlan, Long>
- But entity ID is String.
- Fix all repository typing issues.

5. Dead code:
- UserDetails/UserProfile models exist but are unused.
- Either integrate properly or remove.

6. Missing features:
- User registration
- Refresh tokens
- Forgot/reset password flow design
- Update endpoints
- Validation
- Pagination
- Sorting
- Filtering

7. Domain models are too weak:
TripPlan only contains:
- title
- startDate
- endDate

VacationPlace only contains:
- name
- location

Need proper scalable domain design.

8. Old JWT dependency:
- jjwt 0.9.1
- Upgrade to latest stable version.

9. Missing production essentials:
- CORS
- Logging
- Exception handling
- DTO layer
- API versioning
- OpenAPI/Swagger
- Config separation
- Docker support
- Health checks
- Metrics
- Rate limiting
- CI/CD improvements

==================================================
TARGET ARCHITECTURE
==================================================

Use clean layered architecture:

controller/
service/
repository/
model/
dto/
mapper/
security/
config/
exception/
util/

Use SOLID principles and production conventions.

==================================================
AUTHENTICATION & SECURITY
==================================================

Implement proper auth system:

User model:
- id
- name
- email
- passwordHash
- roles
- createdAt
- updatedAt
- enabled
- profileImageUrl

Features:
- Register
- Login
- Refresh token
- BCrypt password hashing
- JWT access token
- JWT refresh token
- Role-based authorization
- Secure stateless authentication

Use:
Spring Security 6 best practices.

DO NOT use deprecated APIs.

==================================================
TRIP DOMAIN REDESIGN
==================================================

Design TripPlan as a real itinerary system.

TripPlan fields:
- id
- title
- description
- destination
- country
- startDate
- endDate
- estimatedBudget
- tripType
- coverImage
- tags
- visibility (PUBLIC/PRIVATE)
- ownerId
- collaborators
- itineraryDays
- createdAt
- updatedAt

Create nested itinerary structure:

TripDay:
- dayNumber
- date
- activities

Activity:
- title
- description
- location
- startTime
- endTime
- category
- notes
- estimatedCost

==================================================
VACATION PLACE REDESIGN
==================================================

VacationPlace should support discovery features.

Fields:
- id
- name
- city
- country
- description
- categories
- rating
- images
- bestTimeToVisit
- estimatedBudget
- tags
- coordinates
- createdAt

==================================================
API DESIGN
==================================================

Use REST best practices.

Examples:
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/refresh

GET    /api/v1/trips
POST   /api/v1/trips
GET    /api/v1/trips/{id}
PUT    /api/v1/trips/{id}
DELETE /api/v1/trips/{id}

GET    /api/v1/places
GET    /api/v1/places/search
POST   /api/v1/places

Add:
- pagination
- filtering
- sorting
- validation

==================================================
PRODUCTION READINESS
==================================================

Add:
- Global exception handling
- Structured logging
- Request validation
- Standard API response wrapper
- Environment-based configs
- Profiles:
  dev
  staging
  prod

Add:
- Dockerfile
- docker-compose for MongoDB
- .env support
- Health endpoints
- Actuator
- Swagger/OpenAPI

==================================================
DATABASE
==================================================

Use MongoDB properly.

Add:
- indexes
- auditing
- createdAt/updatedAt automation

Use:
@Document
Mongo auditing
Indexed fields where appropriate

==================================================
CODE QUALITY
==================================================

Requirements:
- Use records where appropriate for DTOs
- Use Lombok minimally and correctly
- Follow Java 21 conventions
- Avoid field injection
- Constructor injection only
- No duplicated logic
- Proper package structure
- Clean naming

==================================================
TESTING
==================================================

Add:
- Unit tests
- Integration tests
- Security tests

Use:
JUnit 5
Mockito
Spring Boot Test
Testcontainers for MongoDB

==================================================
DELIVERABLES
==================================================

I want:
1. Full refactor plan
2. Improved folder structure
3. Updated pom.xml
4. Security architecture
5. Database schema design
6. API contracts
7. Step-by-step migration strategy
8. Production-grade code examples
9. Docker setup
10. CI/CD recommendations
11. Scalability recommendations
12. Future microservice migration path

IMPORTANT:
Do not give shallow explanations.
Act like the founding backend architect of a startup expected to scale to millions of users.

Prioritize:
- maintainability
- scalability
- developer experience
- clean APIs
- security
- production readiness
- cost efficiency

Generate the improved backend architecture and implementation plan.