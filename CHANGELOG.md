# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2026-06-06

### Added

#### Architecture
- Hexagonal Architecture (Ports & Adapters) with strict layer separation enforced by ArchUnit
- Base package `br.com.cesarcastro.bankapi` with layers: `domain`, `application`, `adapter`, `infrastructure`

#### Domain Model
- `Customer` entity with CPF/CNPJ document uniqueness constraint and `ACTIVE/INACTIVE` status
- `Account` entity with `@Version` optimistic locking, `NUMERIC(19,4)` balance and `ACTIVE/INACTIVE/BLOCKED` status
- `Transaction` entity as append-only ledger record with `DEPOSIT`, `WITHDRAWAL`, `TRANSFER_OUT`, `TRANSFER_IN` types
- `NotificationEvent` record for async messaging
- Enums: `CustomerStatus`, `AccountStatus`, `TransactionType`, `DocumentType`

#### Database
- Flyway migrations V1–V4: schema creation, customers, accounts, transactions tables
- `NUMERIC(19,4)` monetary type throughout (no `double`)
- Optimistic lock `version` column on accounts table
- Indexes on high-query columns: document, status, customer_id, account_id, created_at

#### Customer Management (RF001)
- `POST /api/v1/customers` — create customer (RN001: unique CPF/CNPJ)
- `GET /api/v1/customers/{id}` — get by ID
- `GET /api/v1/customers` — paginated list with optional status filter
- `PATCH /api/v1/customers/{id}` — update data (RN004: document immutable; name change requires justification)
- `DELETE /api/v1/customers/{id}/deactivate` — logical deactivation (RN002: no active accounts, no debts)
- `DELETE /api/v1/customers/{id}` — permanent deletion (RN003: no accounts, no debts)

#### Account Management (RF002)
- `POST /api/v1/accounts` — create account (RN005: active customer required, initial balance 0.0)
- `GET /api/v1/accounts/{id}` — get by ID
- `GET /api/v1/accounts?customerId=...` — paginated list by customer
- `PATCH /api/v1/accounts/{id}/inactivate` — inactivate (RN006: zero balance, currently active)
- `PATCH /api/v1/accounts/{id}/block` — block with mandatory justification (RN008)
- `PATCH /api/v1/accounts/{id}/unblock` — unblock with mandatory justification (RN008)
- `DELETE /api/v1/accounts/{id}` — delete (RN007: zero balance required)

#### Financial Transactions (RF003)
- `POST /api/v1/transactions/deposit` — deposit with balance update and transaction record (RN010)
- `POST /api/v1/transactions/withdraw` — withdrawal with balance validation (RN011)
- `POST /api/v1/transactions/transfer` — transfer between accounts generating two ledger records and receipt (RN012)
- `GET /api/v1/transactions/{id}` — get transaction
- `GET /api/v1/transactions?accountId=...` — paginated statement with date range filter

#### Concurrency Strategy
- Optimistic locking (`@Version`) for single-account operations (deposit, withdrawal)
- Pessimistic write locking (`SELECT FOR UPDATE`) with deterministic UUID ordering for transfers — prevents deadlock under concurrent transfers between the same pair of accounts

#### Async Notifications (RF004)
- All transactions (deposit, withdrawal, transfer) publish `NotificationEvent` to RabbitMQ asynchronously
- Exchange `bank.notifications` (direct, durable) with routing key `transaction.event`
- Dead-letter exchange `bank.notifications.dlx` and queue `bank.notifications.transactions.dlq`
- `Jackson2JsonMessageConverter` for JSON serialization

#### Observability
- Spring Boot Actuator endpoints: `/actuator/health`, `/actuator/prometheus`, `/actuator/info`
- Micrometer + Prometheus metrics
- `@Observed` on all use case methods for automatic span creation
- OpenTelemetry Collector integration (OTLP traces)
- Grafana with provisioned Prometheus datasource

#### Documentation
- Springdoc OpenAPI 2.7 with Swagger UI at `/swagger-ui.html`
- `@Operation` and `@Tag` annotations on all controllers
- `README.md` with architecture decisions, stack, run instructions, and endpoint reference

#### Infrastructure & DevOps
- `Dockerfile` using `eclipse-temurin:21-jre-alpine`
- `docker-compose.yml` with 6 services: bank-api, PostgreSQL 16, RabbitMQ 3.13, Prometheus, Grafana, OTel Collector
- Health checks on all services with proper `depends_on` ordering
- Prometheus scrape config for the application metrics endpoint
- Grafana provisioning via `infra/grafana/provisioning/`

#### Tests
- 26 unit tests with Mockito (use case layer, zero Spring context)
- `@WebMvcTest` controller slice tests for HTTP mapping and error handling
- `HexagonalArchitectureTest` with 5 ArchUnit rules enforcing layer boundaries
- JaCoCo configured with 90% line coverage threshold
- Testcontainers setup for future integration and E2E tests

#### Error Handling
- Uniform `ErrorResponse` record: `timestamp`, `status`, `error`, `message`, `path`
- HTTP status mapping: 400 (validation), 404 (not found), 409 (conflict), 422 (business rule), 500 (unexpected)
- `GlobalExceptionHandler` with `@RestControllerAdvice`

[0.1.0]: https://github.com/cesarrqc/bank-api/releases/tag/v0.1.0
