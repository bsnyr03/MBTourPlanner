# MBTourPlanner

## Description

## Architecture

### Layers
- **Controller Layer**: Handles HTTP requests, input validation, and delegates to service layer.
- **Service Layer**: Contains business logic, route retrieval from OpenRouteService, calculations (popularity, childFriendliness), and static map URL generation.
- **Repository Layer**: JPA repositories for `TourEntity` and `TourLogEntity`, custom search queries.
- **Converter Layer**: JPA attribute converters for `Duration` ↔ SQL `INTERVAL`.
- **Mapper Layer**: AbstractMapper and concrete mappers converting between Entity and DTO.

### Class Diagrams
(See `class-diagram.puml`)

## Use Cases

### Use-Case Diagram
(See `use-case-diagram.puml`)

### Mookup
(See `mookup/...`)

## Library Decisions
- **Spring Boot** for rapid REST API development.
- **Spring Data JPA** for ORM.
- **OpenRouteService Java client / WebClient** for external routing.
- **iText 7** for PDF report generation.
- **OpenCSV** for CSV import/export.
- **JUnit 5 + Mockito** for unit testing.
- **Lombok** to reduce boilerplate.

## Lessons Learned
- Converting `java.time.Duration` to SQL `INTERVAL` required a custom `AttributeConverter`.
- Integrating with ORS API and static map URL construction.
- Exception handling with `@ControllerAdvice`.
- Importance of unidirectional mapping via AbstractMapper.

## Design Patterns
- **Repository** (Spring Data JPA).
- **DTO / Mapper** (AbstractMapper + concrete mappers).
- **Factory** for building static map URLs in `OpenRouteService`.
- **Strategy** can be considered for multiple report formats.

## Unit Testing Decisions
- Focus on service layer logic (filtering, computed attributes).
- Mock external calls (ORS) in service tests.
- Use `@DataJpaTest` for repository queries.
- Validate JSON (serialization/deserialization) via Jackson tests.

## Unique Feature
- Dynamic static map generation via OSM static map API with route polyline encoding.

## Time Tracking
- Total estimated effort: 60h  
  - Architecture & design: 8h  
  - Core CRUD & search: 12h  
  - Logging & error handling: 4h  
  - Import/Export: 6h  
  - Reporting & PDFs: 8h  
  - ORS integration & map URLs: 10h  
  - Unit tests & mocking: 8h  
  - Documentation & diagrams: 4h  

## Git Repository
- [GitHub - username/MBTourPlanner](https://github.com/username/MBTourPlanner)
