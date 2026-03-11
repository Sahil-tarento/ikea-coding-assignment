# Case Study

This document details the approaches and strategies used for fulfilling the IKEA Coding Assignment tasks.

## Code Design & Architecture
- **Hexagonal Architecture:** The architecture ensures proper separation of domain logic from external concerns. Use cases enforce pure business rules using ports. Adapters implement databases (RepositoryAdapter, Hibernate Panache) and REST controllers.
- **Validations via Validator Class:** A major refactor introduces `WarehouseValidator` and `FulfillmentValidator` classes to encapsulate validation constraints, keeping the UseCases cohesive and making the validators independently testable.
- **Event-Driven Strategy:** Leveraged `StoreEvent` and a `LegacyStoreManagerEventObserver` with a CDI `TransactionPhase.AFTER_SUCCESS` observer pattern. This guarantees that `LegacyStoreManagerGateway` integrations solely process post-commit, ensuring high system integrity.
- **REST Endpoints:** Followed standard JAX-RS mappings to build and structure HTTP parameters based on standard definitions in openapi generator schemas.

## Testing Strategy
- Tests were structured to cover maximum domain logic covering core classes `WarehouseValidatorTest`, `ArchiveWarehouseUseCaseTest` and specific adapters.
- Mocking: Utilized Mockito annotations to stub DB implementations guaranteeing deterministic behavior regardless of database configuration.
- Jacoco plugin configured for coverage measuring and validation enforcing a minimum of 80% coverage rule over the logic implementation.
