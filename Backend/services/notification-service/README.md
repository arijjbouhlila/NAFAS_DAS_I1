# Notification Service (NAFAS)

Minimal Java 17 / Spring Boot service exposing a liveness endpoint and Swagger UI.

## Run

From this directory:

    mvn spring-boot:run

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| GET | /hello | Liveness response |
| GET | /swagger-ui/index.html | Swagger UI |
| GET | /v3/api-docs | OpenAPI document |

The service listens on port **8086** so it can run alongside the Reporting Service on port 8085. Run tests with `mvn test`.