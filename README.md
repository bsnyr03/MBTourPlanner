# MB Tour Planner

## Table of Contents

1. [Description](#description)  
2. [Architecture](#architecture)  
   - [Layers](#layers)  
   - [Class Diagrams](#class-diagrams)  
3. [Use Cases](#use-cases)  
4. [Mockups & Wireframes](#mockups--wireframes)  
5. [Library Decisions](#library-decisions)  
6. [Lessons Learned](#lessons-learned)  
7. [Design Patterns](#design-patterns)  
8. [Unit Testing](#unit-testing)  
9. [Unique Feature](#unique-feature)  
10. [Time Tracking](#time-tracking)  
11. [Git Repositories](#git-repositories)

---

## Description

MB Tour Planner is a Spring Boot REST API for creating, managing, and reporting custom tours. Features include CRUD operations for tours and logs, full-text search, import/export (JSON, CSV), PDF reporting, and dynamic static map generation via OpenRouteService and OpenStreetMap.

## Architecture

### Layers

- **Controller Layer**  
  Handles HTTP endpoints, request validation, and delegates to services.

- **Service Layer**  
  Implements business logic, computes metrics (popularity, child friendliness), integrates with OpenRouteService, and builds static map URLs.

- **Repository Layer**  
  Spring Data JPA repositories for `TourEntity` and `TourLogEntity` with custom queries for search.

- **Mapper Layer**  
  `AbstractMapper` + concrete mappers to convert between entities and DTOs.

- **Converter Layer**  
  JPA `AttributeConverter` for converting `java.time.Duration` ↔ SQL `INTERVAL`.

### Class Diagrams

See [class-diagram.puml](docs/class-diagram.puml) for a visual overview of key classes and relationships.

## Use Cases

![Use Case Diagram](docs/use-case-diagram.png)

- **Manage Tours**: Create, read, update, delete tours.
- **Manage Tour Logs**: Add, view, update, delete logs tied to tours.
- **Search**: Full-text search across tours and logs.
- **Import/Export**: Bulk data import/export via JSON and CSV.
- **Reporting**: Generate detailed and summary PDF reports.

## Mockups & Wireframes

Browse the Figma project: [Wireframes & Mockups](docs/mockups/)

## Library Decisions

- **Spring Boot**: Simplify REST API development.  
- **Spring Data JPA**: ORM and repository abstractions.  
- **WebClient**: Non-blocking calls to OpenRouteService.  
- **iText 7**: PDF generation.  
- **OpenCSV**: CSV import/export.  
- **JUnit 5 & Mockito**: Unit and integration testing.  
- **Lombok**: Boilerplate reduction.

## Lessons Learned

- Handling `Duration` ↔ `INTERVAL` mapping with a custom converter.  
- Error handling with `@ControllerAdvice`.  
- Building polylines and static map URLs.  
- Testing external API integrations.

## Design Patterns

- **Repository**: Data access abstraction.  
- **DTO & Mapper**: Decoupling domain from persistence.  
- **Factory**: Static map URL construction in `OpenRouteService`.  

## Unit Testing

- **`@DataJpaTest`** for repository-level tests.  
- **Mocking** external ORS calls with Mockito.  
- **Jackson** tests for JSON serialization/deserialization.  

## Unique Feature

- **CSV Import/Export**: Allows bulk import and export of both tours and tour logs in CSV format, in addition to the standard JSON endpoints.
    - **Export**: `GET /tours/{id}/export.csv` generates a CSV file combining tour details and its associated logs.
    - **Import**: `POST /tours/{id}/import.csv` consumes a multipart-form CSV upload to update tour details and create multiple logs in one request.
    - Uses Apache Commons CSV for parsing and printing.
    - Fully covered by integration tests for both endpoints.
    - 
## Time Tracking Backend

| Task                                 | Hours |
|--------------------------------------|-------|
| Architecture & design                | 8     |
| Core CRUD & search                   | 12    |
| Validation & error handling          | 4     |
| Import/Export (JSON, CSV)            | 6     |
| Reporting & PDF generation           | 8     |
| ORS integration & map URLs           | 10    |
| Unit testing & mocking               | 8     |
| Documentation & diagrams             | 4     |
| **Total**                            | **60** |

## Git Repositories

- **Backend**: https://github.com/bsnyr03/MBTourPlanner  
- **Frontend**: https://github.com/bsnyr03/MBTourPlannerFrontend  
