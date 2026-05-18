# Tripperr API — curl Examples

Base URL: `http://localhost:8080`

> Replace `<ACCESS_TOKEN>` with the JWT returned by Login or Register.  
> Replace `<REFRESH_TOKEN>`, `<TRIP_ID>`, `<PLACE_ID>` as needed.

---

## Auth

### Register
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Aryan Singh",
    "email": "aryan@tripperr.com",
    "password": "Secret@1234"
  }' | jq .
```

### Login
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "aryan@tripperr.com",
    "password": "Secret@1234"
  }' | jq .

# Save the token for subsequent requests:
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"aryan@tripperr.com","password":"Secret@1234"}' \
  | jq -r '.data.accessToken')
```

### Refresh Token
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<REFRESH_TOKEN>"}' | jq .
```

### Logout
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <ACCESS_TOKEN>" | jq .
```

---

## User

### Get Current User
```bash
curl -s http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>" | jq .
```

---

## Trips

### Create Trip
```bash
curl -s -X POST http://localhost:8080/api/v1/trips \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tokyo Adventure 2026",
    "description": "10-day cultural and food tour of Tokyo",
    "destination": "Tokyo",
    "country": "Japan",
    "startDate": "2026-09-01",
    "endDate": "2026-09-10",
    "estimatedBudget": 3000.00,
    "currency": "USD",
    "tripType": "ADVENTURE",
    "visibility": "PRIVATE",
    "tags": ["food", "culture", "solo"],
    "itineraryDays": [
      {
        "dayNumber": 1,
        "date": "2026-09-01",
        "activities": [
          {
            "title": "Arrive at Narita Airport",
            "location": "Narita International Airport",
            "startTime": "14:00",
            "category": "TRANSPORT",
            "estimatedCost": 30.00
          }
        ]
      }
    ]
  }' | jq .
```

### Get All Accessible Trips
```bash
curl -s "http://localhost:8080/api/v1/trips?page=0&size=20" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" | jq .

# With search query:
curl -s "http://localhost:8080/api/v1/trips?query=tokyo&page=0&size=10" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" | jq .
```

### Get My Trips
```bash
curl -s "http://localhost:8080/api/v1/trips/mine?page=0&size=20" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" | jq .
```

### Get Trip by ID
```bash
curl -s http://localhost:8080/api/v1/trips/<TRIP_ID> \
  -H "Authorization: Bearer <ACCESS_TOKEN>" | jq .
```

### Update Trip
```bash
curl -s -X PUT http://localhost:8080/api/v1/trips/<TRIP_ID> \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tokyo Adventure 2026 — Updated",
    "destination": "Tokyo",
    "country": "Japan",
    "startDate": "2026-09-01",
    "endDate": "2026-09-11",
    "estimatedBudget": 3500.00,
    "currency": "USD",
    "tripType": "ADVENTURE",
    "visibility": "PUBLIC",
    "tags": ["food", "culture", "solo", "extended"]
  }' | jq .
```

### Delete Trip
```bash
curl -s -X DELETE http://localhost:8080/api/v1/trips/<TRIP_ID> \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -w "\nHTTP %{http_code}\n"
# Returns 204 No Content on success
```

---

## Places

### Get All Places (public)
```bash
curl -s "http://localhost:8080/api/v1/places?page=0&size=20" | jq .

# Filter by country and city:
curl -s "http://localhost:8080/api/v1/places?country=Japan&city=Tokyo" | jq .
```

### Search Places (public)
```bash
curl -s "http://localhost:8080/api/v1/places/search?query=tokyo&minRating=4.0" | jq .
```

### Get Place by ID (public)
```bash
curl -s http://localhost:8080/api/v1/places/<PLACE_ID> | jq .
```

### Create Place (Admin only)
```bash
curl -s -X POST http://localhost:8080/api/v1/places \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Shibuya Crossing",
    "city": "Tokyo",
    "country": "Japan",
    "description": "World-famous pedestrian scramble crossing in the heart of Tokyo.",
    "categories": ["SIGHTSEEING", "CULTURE"],
    "rating": 4.7,
    "images": ["https://example.com/shibuya1.jpg"],
    "bestTimeToVisit": "October to November",
    "estimatedBudget": 0,
    "currency": "USD",
    "tags": ["iconic", "urban", "photography"],
    "coordinates": {
      "latitude": 35.6595,
      "longitude": 139.7005
    }
  }' | jq .
```

---

## Health & Docs

```bash
# Health check
curl -s http://localhost:8080/actuator/health | jq .

# OpenAPI spec (JSON)
curl -s http://localhost:8080/v3/api-docs | jq .

# Swagger UI (open in browser)
open http://localhost:8080/swagger-ui/index.html
```

---

## Enum Reference

| Field          | Valid Values |
|----------------|-------------|
| `tripType`     | `LEISURE`, `BUSINESS`, `ADVENTURE`, `HONEYMOON`, `FAMILY`, `SOLO`, `GROUP`, `BACKPACKING`, `WORKATION`, `OTHER` |
| `visibility`   | `PUBLIC`, `PRIVATE` |
| `category` (activity) | `SIGHTSEEING`, `FOOD`, `TRANSPORT`, `LODGING`, `EVENT`, `SHOPPING`, `NATURE`, `NIGHTLIFE`, `WELLNESS`, `OTHER` |
