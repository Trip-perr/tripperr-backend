You are working on the Tripperr backend project.

Please perform the following tasks carefully and validate everything end-to-end before finishing.

==================================================
TASK 1 — RESTORE ORIGINAL startApp.sh
==================================================

I think someone modified @startApp.sh incorrectly.

Your job:
- inspect the current script
- identify what was changed
- restore it to a clean, reliable original version

Requirements:
- compatible with macOS/Linux
- uses Java 21
- validates JAVA_HOME
- starts MongoDB dependencies if needed
- loads environment variables properly
- starts Spring Boot reliably
- should work for local development

The script should:
- fail fast on errors
- print helpful logs
- support running with:
  ./startApp.sh

Do NOT overcomplicate it.

==================================================
TASK 2 — FIX SWAGGER UI
==================================================

Currently:
http://localhost:8080/swagger-ui/index.html

is NOT working.

Investigate and fix the issue completely.

Possible areas to check:
- springdoc-openapi dependency
- Spring Security blocking Swagger routes
- incorrect application.yml config
- actuator/security path conflicts
- missing OpenAPI bean/config
- wrong Swagger path
- incompatible dependency versions

Requirements:
- Swagger UI must open successfully
- OpenAPI JSON endpoint must work
- Swagger endpoints must be publicly accessible in dev
- API docs should display JWT auth support

Expected working endpoints:
- /swagger-ui/index.html
- /v3/api-docs

Also:
- add proper API metadata:
  - title: Tripperr API
  - version: v1
  - description

==================================================
TASK 3 — CREATE POSTMAN COLLECTION
==================================================

Create:
@Tripperr.postman_collection.json

Requirements:
- production-quality Postman collection
- organized folders
- variables for:
  - baseUrl
  - accessToken
  - refreshToken

Include realistic example requests for:

AUTH
- Register
- Login
- Refresh token
- Logout

USER
- Get current user

TRIPS
- Create trip
- Get all trips
- Get my trips
- Get trip by ID
- Update trip
- Delete trip

PLACES
- Get all places
- Search places
- Create place (admin)

Requirements:
- include example request bodies
- include example responses
- include Bearer token auth
- auto-save JWT token from login response into collection variables using test scripts
- proper environment variable usage

Also generate equivalent curl examples in:
@api-examples.md

==================================================
TASK 4 — VALIDATE EVERYTHING
==================================================

Before finishing:
- run the application
- verify Swagger UI works
- verify API docs load
- verify Postman collection imports successfully
- verify authentication flow works
- verify protected endpoints work with JWT

Then provide:
1. summary of fixes
2. files changed
3. how to run locally
4. Swagger URL
5. sample login credentials
6. example curl command

IMPORTANT:
Do not provide placeholders or pseudo-code.
Implement everything fully and make the project developer-friendly.