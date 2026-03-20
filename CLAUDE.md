# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
./gradlew build        # Build the project
./gradlew test         # Run all tests
./gradlew clean        # Clean build artifacts

# Run a single test class
./gradlew test --tests "com.study.loadtest.service.order.OrderServiceTest"

# Run a single test method
./gradlew test --tests "com.study.loadtest.service.order.OrderServiceTest.methodName"
```

## Architecture Overview

Spring Boot 4.0.1 / Java 21 REST API for a ticketing system (event booking with inventory management and payment processing).

### Layer Structure

```
interfaces/     → REST controllers (v1), GlobalExceptionHandler, DTOs
service/        → Business logic (EventService, OrderService, PaymentService)
domain/         → Rich domain models (Event, Order, Payment) with enums and business logic
repository/     → Wrapper pattern over Spring Data JPA interfaces
shared/         → Custom exceptions (SoldOutException, NoSuchEntityException)
```

### Key Design Decisions

**Repository Wrapper Pattern:** Spring Data JPA interfaces (e.g., `EventJpaRepository`) are never injected directly into services. Instead, wrapper classes (e.g., `EventJpaRepositoryWrapper`) adapt them and are the actual dependency injected into services. This isolates JPA specifics from the service layer.

**Rich Domain Models with Builder:** All entities extend `BaseJpaEntity` (auto-timestamps in UTC) and use Lombok `@SuperBuilder`. Business logic lives on the domain model — e.g., `Event.decreaseQuantity(int)` throws `SoldOutException` when stock is exhausted.

**API Versioning:** Controllers live under `interfaces/{domain}/v1/` and are mapped to `/api/v1/{domain}`.

### Exception → HTTP Status Mapping

| Exception | Status |
|---|---|
| `NoSuchEntityException` | 404 NOT_FOUND |
| `SoldOutException` | 409 CONFLICT |
| `Exception` (fallback) | 500 INTERNAL_SERVER_ERROR |

All errors return `ErrorResponse { message }`.

### Database

H2 in-memory database (auto-configured). No explicit schema files — DDL generated from JPA entities.

### Test Strategies by Layer

| Layer | Annotation | Notes |
|---|---|---|
| Domain/Service | `@ExtendWith(MockitoExtension.class)` | Pure unit tests with Mockito |
| Repository | `@DataJpaTest` | Slice test with real H2 |
| Controller | `@WebMvcTest` | MockMvc, mocked service layer |
| Full context | `@SpringBootTest` | Smoke test only |

## API Endpoints

- [API specs](docs/api-spec.md)
- [Sequence Diagrams](docs/sequence.md)