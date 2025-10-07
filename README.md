# Review System - Multi-Service Setup

This repository hosts a Spring Boot-based review platform composed of three applications plus a shared PostgreSQL database. Each service runs independently on its own port and collaborates through REST endpoints.

## Services

- **auth** (`:8001`) - Handles authentication, JWT issuance, and user management.
- **backend** (`:8002`) - Core API for products and reviews; triggers PDF exports via the PDF generator service.
- **pdf_generator** (`:8003`) - Receives product/review payloads and generates downloadable PDF reports.
- **postgres** (`:8000 -> 5432`) - Shared database with schema defined in `db/init.sql` (`uuid-ossp` enabled, tables for users, roles, products, reviews).

## Review Workflow

1. Product owners authenticate through the auth service and create products via the backend API.
2. End users submit reviews for those products; duplicate reviews per product/user are prevented.
3. Product owners can export their catalog plus reviews to PDF through the backend, which delegates to `pdf_generator`.
4. Data persists in Postgres; tables are provisioned automatically from `db/init.sql`.

## Infrastructure

- `docker-compose.yaml` orchestrates all services with shared Maven cache and database volume.
- Database schema is provisioned automatically from `db/init.sql` on first boot.
- Environment variables for each service (DB connection, JWT secret, PDF endpoint, ports) are defined in the compose file for consistent local deployments.

## Running the Stack

```bash
docker compose up --build
```

The first run downloads Maven dependencies; subsequent runs reuse the cached volume. Once containers are healthy:

- Auth API: http://localhost:8001
- Backend API: http://localhost:8002
- PDF Generator: http://localhost:8003
- Postgres: localhost:8000 (user `postgres`, password `9927`)

### Swagger

- Backend API docs: http://localhost:8002/swagger-ui/index.html
- Additional Swagger endpoints can be enabled per service (apply Springdoc starter if needed).

Stop everything with `docker compose down` (pass `-v` to drop volumes if you need to reset state).
