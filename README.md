# RIVO9 Gateway - Intermediate Backend

## Architecture Overview

```
Angular App (Port 4200)
    ↓
RIVO9 Gateway (Port 9090) ← YOU ARE HERE
    ↓ (RestTemplate)
JWT_Authenticator_Project (Port 8080)
```

## Purpose

This intermediate gateway acts as a proxy between your Angular frontend and the main RIVO9 backend. It uses Spring Boot RestTemplate to forward requests to:

1. `/api/secure/rivofetch` - API Key authenticated endpoint
2. `/forward` - JWT authenticated endpoint

## Endpoints

### 1. POST /gateway/api/rivofetch
Forwards to backend `/api/secure/rivofetch` with API key authentication.

**Headers:**
- `x-api-key`: Your API key (required)
- `X-Custom-Origin`: Custom origin header (optional)
- `Content-Type`: application/json

**Request Body:**
```json
{
  "url": "https://example.com",
  "linkedin": true,
  "facebook": true,
  "youtube": true,
  "instagram": true,
  "x": true
}
```

**Example:**
```bash
curl -X POST http://localhost:9090/gateway/api/rivofetch \
  -H "x-api-key: sk-your-api-key" \
  -H "X-Custom-Origin: rivo.ai/api/mahesh" \
  -H "Content-Type: application/json" \
  -d '{"url":"https://google.com","linkedin":true}'
```

### 2. POST /gateway/api/forward
Forwards to backend `/forward` with JWT authentication.

**Headers:**
- `Authorization`: Bearer {jwt-token} (required)
- `Content-Type`: application/json

**Request Body:**
```json
{
  "url": "https://example.com",
  "linkedin": true,
  "facebook": true,
  "youtube": true,
  "instagram": true,
  "x": true
}
```

**Example:**
```bash
curl -X POST http://localhost:9090/gateway/api/forward \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"url":"https://google.com","linkedin":true}'
```

### 3. GET /gateway/api/health
Health check endpoint.

**Example:**
```bash
curl http://localhost:9090/gateway/api/health
```

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Gateway Port
server.port=9090
server.servlet.context-path=/gateway

# Backend API Configuration
backend.api.base-url=http://localhost:8080/rivo9
backend.api.timeout=30000

# CORS Configuration
cors.allowed-origins=http://localhost:4200,http://localhost:3000
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true
```

## Running the Gateway

### Prerequisites
- Java 21
- Maven 3.6+
- Backend JWT_Authenticator_Project running on port 8080

### Build and Run

```bash
# Navigate to gateway directory
cd d:\RIVO9\RIVO9_Gateway

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Or run directly:
```bash
java -jar target/rivo9-gateway-1.0.0.jar
```

## Angular Integration

Update your Angular `environment.ts`:

```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:9090/gateway'  // Point to gateway
};
```

Your existing Angular service will work without changes:

```typescript
fetchBrandWithApiKey(request: BrandFetchRequest, apiKey: string, customOrigin?: string): Observable<BrandResponse> {
  const headers = new HttpHeaders({
    'x-api-key': apiKey,
    'Content-Type': 'application/json',
    'X-Custom-Origin': customOrigin || 'rivo.ai/api/mahesh'
  });

  return this.http.post<BrandResponse>(
    `${this.apiUrl}/api/rivofetch`,  // Will call gateway
    request,
    {headers}
  );
}
```

## Benefits

1. **Decoupling**: Frontend doesn't directly connect to main backend
2. **Security**: Additional layer for authentication/authorization
3. **Flexibility**: Easy to add caching, rate limiting, or request transformation
4. **Monitoring**: Centralized logging of all API calls
5. **Load Balancing**: Can distribute requests across multiple backend instances

## Project Structure

```
RIVO9_Gateway/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── ai/
│       │       └── rivo9/
│       │           └── gateway/
│       │               ├── GatewayApplication.java
│       │               ├── config/
│       │               │   ├── RestTemplateConfig.java
│       │               │   └── CorsConfig.java
│       │               ├── controller/
│       │               │   └── GatewayController.java
│       │               ├── dto/
│       │               │   └── BrandRequest.java
│       │               └── service/
│       │                   └── GatewayService.java
│       └── resources/
│           └── application.properties
└── README.md
```

## Troubleshooting

### Gateway can't connect to backend
- Ensure JWT_Authenticator_Project is running on port 8080
- Check `backend.api.base-url` in application.properties

### CORS errors
- Add your frontend URL to `cors.allowed-origins`
- Ensure backend also has CORS configured

### Timeout errors
- Increase `backend.api.timeout` in application.properties
- Check backend response time

## Next Steps

1. Add request/response logging interceptor
2. Implement caching for frequently requested brands
3. Add circuit breaker pattern for backend failures
4. Implement API key validation at gateway level
5. Add metrics and monitoring (Actuator endpoints)
