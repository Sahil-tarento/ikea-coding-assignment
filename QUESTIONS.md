# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Refactoring: Yes, I would refactor to consistent patterns.
Reasoning: Currently, the codebase mixes implementation strategies: Store uses the Active Record pattern (extending PanacheEntity), while Warehouse follows the Repository pattern (extending PanacheRepository with an Adapter). To improve maintainability and testability, I would standardize on the Repository pattern (as seen in Warehouse). The Repository pattern decouples the domain model from the persistence framework, adhering closer to Clean Architecture principles, and makes unit testing easier by allowing repositories to be mocked without needing to mock static methods on entities.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Contract-First (Warehouse):
Pros: Clear contract before coding, better collaboration, auto-generated code, single source of truth (OpenAPI spec).
Cons: Extra build step, rigidity, potential verbose generated code.

Code-First (Product/Store):
Pros: Faster coding, full control over code, familiar to most devs.
Cons: Documentation can drift from code, API changes harder to track/communicate.

Choice: I prefer the Code-First approach. In my experience, I have used Code-First for the majority of projects I've worked on. I have only used the Contract-First approach (OpenAPI) in one project. I find Code-First more agile and natural for many Java developers, especially when tools like Quarkus can automatically generate the OpenAPI spec from the code annotations.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Prioritization: Focus on Domain Logic (Use Cases) and Integration Tests for Critical Paths (Controllers/Gateways). Domain rules are critical, so Unit Tests for Use Cases are high value and cheap.
Integration Tests: Essential to verify the adapter wiring (DB, API) works.

Strategy:
1. Unit Tests: Cover all Use Cases and Domain Entities. Mock repositories.
2. Integration Tests: Use @QuarkusTest to test Resources end-to-end with in-memory DB or Testcontainers.
3. Coverage: Use JaCoCo to track coverage. Enforce thresholds (e.g. 85%) in CI pipeline. Review tests in PRs. Adopt TDD to ensure tests are written with features.
```