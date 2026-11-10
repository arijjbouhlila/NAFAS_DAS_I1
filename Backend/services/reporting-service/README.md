# Reporting Service (NAFAS) — Java / Spring Boot

Citizens submit environmental observations (title, category, description, GPS, optional photo).
Administrators review them (validate / reject with a comment) and can delete them.

Port **8085** · Java 17+ · Maven · PostgreSQL (or in-memory H2 for a zero-setup start)

## Run (zero setup: H2 in memory, 4 demo reports)
    mvn spring-boot:run
or in IntelliJ: run `ReportingApplication`. Run the tests with `mvn test`.

## Run with PostgreSQL
    docker compose up -d
    mvn spring-boot:run -Dspring-boot.run.profiles=postgres
(IntelliJ: Run configuration > Active profiles = `postgres`.)
Defaults: db `nafas_reporting`, user/password `nafas`; override with DB_URL, DB_USER, DB_PASSWORD.

## Open
| What | URL |
|---|---|
| Web console (submit, review, map, API log) | http://localhost:8085/ |
| Swagger UI | http://localhost:8085/swagger-ui.html |
| OpenAPI JSON | http://localhost:8085/v3/api-docs |
| Hello | http://localhost:8085/hello |
| Health | http://localhost:8085/actuator/health |
| H2 console (H2 only) | http://localhost:8085/h2-console (jdbc:h2:mem:reporting, user `sa`, empty password) |

## Simulated roles
Until the User Management Service issues JWTs, the role is sent in a header: `X-User-Role: CITIZEN` or `ADMIN`.
- Web console: "Acting as" selector (top right).
- Swagger UI: fill the `X-User-Role` field on admin endpoints (type `ADMIN`).

## Endpoints
| Method | Path | Who |
|---|---|---|
| GET | /hello | anyone |
| POST | /api/reports | citizen / admin |
| POST | /api/reports/{id}/photo | citizen / admin |
| GET | /api/reports?status=&category= | anyone |
| GET | /api/reports/stats | anyone |
| GET | /api/reports/{id} and /photo | anyone |
| PATCH | /api/reports/{id}/validate, /reject | ADMIN |
| DELETE | /api/reports/{id} | ADMIN |

Workflow: `SUBMITTED` → `VALIDATED` or `REJECTED`. A reviewed report cannot be reviewed again (409).

## Next steps
- Register in Eureka / Config Server and go through the API Gateway
- Replace the header with the JWT from the User Management Service
- Resolve `locationId` through the Location Service (OpenFeign)
- Expose validated reports to the Analytics Service
