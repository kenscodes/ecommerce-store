# Design Decisions

## Decision: Use Spring Boot Framework

**Context:** Need to choose a framework for building the e-commerce REST APIs quickly and efficiently.

**Options Considered:**
- Option A: Plain Java with embedded HTTP server (like SparkJava)
- Option B: Spring Boot with auto-configuration and dependency injection
- Option C: Micronaut for lightweight microservices

**Choice:** Spring Boot with auto-configuration and dependency injection

**Why:** Spring Boot provides rapid development with minimal configuration, excellent documentation, built-in validation, testing support, and a large ecosystem. It's the industry standard for Java enterprise applications and provides features like:
- Auto-configuration reduces boilerplate code
- Dependency injection simplifies service management
- Built-in validation framework for request DTOs
- Excellent testing support with @SpringBootTest
- Easy to extend with additional features like security, databases, etc.

## Decision: In-Memory Storage Instead of Database

**Context:** The assignment specified that in-memory storage is acceptable and no database is required.

**Options Considered:**
- Option A: Use H2 in-memory database with JPA/Hibernate
- Option B: Implement custom in-memory storage with ConcurrentHashMap
- Option C: Use Redis for in-memory data store

**Choice:** Custom in-memory storage with ConcurrentHashMap

**Why:** 
- Simplicity: No additional dependencies or configuration needed
- Performance: Direct object access without ORM overhead
- Control: Complete control over data structure and behavior
- Testing: Easy to reset and manipulate in tests
- Learning: Demonstrates understanding of concurrent data structures
- Portability: No database setup required for running the application

The custom implementation also allows for easy analytics tracking and discount generation logic integration.

## Decision: Layered Architecture with Service Pattern

**Context:** Need to organize code structure for maintainability and testability.

**Options Considered:**
- Option A: Single controller with all logic
- Option B: Layered architecture (Controller → Service → Repository)
- Option C: Domain-driven design with aggregates and repositories

**Choice:** Layered architecture with Service pattern

**Why:**
- **Separation of Concerns**: Each layer has a specific responsibility
- **Testability**: Services can be unit tested independently of controllers
- **Maintainability**: Business logic is centralized in services
- **Reusability**: Services can be reused across different controllers
- **Transaction Management**: Easy to apply transaction boundaries at service level
- **Industry Standard**: Follows well-established patterns for enterprise applications

This approach makes the code more modular and easier to extend with new features.

## Decision: DTO Pattern for API Requests/Responses

**Context:** Need to design the API contract and handle data transfer between client and server.

**Options Considered:**
- Option A: Use domain entities directly in API responses
- Option B: Create separate DTOs for requests and responses
- Option C: Use generic Map<String, Object> for flexibility

**Choice:** Separate DTOs for requests and responses

**Why:**
- **API Stability**: Domain entities can change without breaking API contracts
- **Validation**: DTOs can have validation annotations specific to API requirements
- **Security**: Prevents exposing sensitive domain entity fields
- **Flexibility**: Can shape API responses differently from domain model structure
- **Documentation**: Clear separation between API contract and business logic
- **Versioning**: Easier to version APIs independently of domain changes

For example, `CartResponse` includes calculated totals that might not be stored in the domain `Cart` entity.

## Decision: BigDecimal for Monetary Values

**Context:** Need to handle prices, totals, and discount calculations accurately.

**Options Considered:**
- Option A: Use double/float for simplicity
- Option B: Use integer to store cents
- Option C: Use BigDecimal for precise decimal arithmetic

**Choice:** BigDecimal for precise decimal arithmetic

**Why:**
- **Precision**: Avoids floating-point rounding errors in financial calculations
- **Clarity**: More readable than integer cents (e.g., $19.99 vs 1999 cents)
- **Standard**: Industry best practice for monetary values in Java
- **Operations**: Built-in support for arithmetic operations
- **Formatting**: Easy to format for display purposes

While storing cents as integers would be more memory-efficient, BigDecimal provides better readability and is the standard approach for financial applications in Java. The in-memory store does convert to cents for analytics storage to demonstrate both approaches.

## Decision: RESTful API Design with Resource-Based URLs

**Context:** Need to design intuitive and scalable API endpoints.

**Options Considered:**
- Option A: RPC-style endpoints (/addToCart, /checkout)
- Option B: RESTful resource-based endpoints (/api/cart/{id}/items)
- Option C: GraphQL single endpoint with flexible queries

**Choice:** RESTful resource-based endpoints

**Why:**
- **Intuitive**: Resource-based URLs are self-documenting
- **HTTP Methods**: Proper use of GET, POST, PUT, DELETE verbs
- **Scalability**: Easy to add new resources and operations
- **Caching**: HTTP caching mechanisms work naturally
- **Standards**: Follows REST principles and industry conventions
- **Tooling**: Better support for API documentation and testing tools

For example, cart operations use resource URLs like `/api/cart/{customerId}/items` with appropriate HTTP methods rather than action-based endpoints.

## Decision: Atomic Counters for Analytics

**Context:** Need to track analytics (orders, revenue, discounts) efficiently in the in-memory store.

**Options Considered:**
- Option A: Calculate analytics on-the-fly by iterating through orders
- Option B: Maintain atomic counters that update with each operation
- Option C: Use a separate analytics service that processes events

**Choice:** Atomic counters with AtomicLong

**Why:**
- **Performance**: O(1) retrieval vs O(n) calculation
- **Consistency**: Thread-safe updates using AtomicLong
- **Simplicity**: No complex event processing required
- **Real-time**: Analytics are always up-to-date
- **Memory**: Minimal overhead compared to storing redundant data

The atomic counters are updated within the same transaction that saves orders, ensuring consistency between the data and analytics.

## Decision: Case-Insensitive Discount Code Lookup

**Context:** Customers might enter discount codes in different cases (SAVE10 vs save10).

**Options Considered:**
- Option A: Strict case-sensitive matching
- Option B: Case-insensitive matching
- Option C: Normalize to uppercase on input

**Choice:** Case-insensitive matching with lowercase storage

**Why:**
- **User Experience**: Customers don't need to worry about case
- **Forgiveness**: Reduces failed checkout attempts due to case errors
- **Consistency**: All codes stored in lowercase for uniform lookup
- **Simplicity**: Single storage format with flexible lookup
- **Standard**: Common practice for discount codes and promotional codes

The implementation stores codes in lowercase but preserves the original case in the response for display purposes.
