# HMCTS Case Management Backend

Spring Boot REST API for the HMCTS Case Management System. This backend service provides comprehensive case management capabilities with robust validation, error handling, and API documentation.

## Features

- **RESTful API** with full CRUD operations for case management
- **Spring Boot 3.5.5** with Java 21 support
- **JPA/Hibernate** for database operations with H2 (dev) and PostgreSQL (prod) support
- **Bean Validation** with comprehensive input validation
- **OpenAPI/Swagger** documentation at `/swagger-ui.html`
- **Global exception handling** with proper HTTP status codes
- **Database indexing** for optimized queries
- **Comprehensive test suite** (unit, integration, smoke, functional)

## Tech Stack

- **Java 21** - Latest LTS Java version
- **Spring Boot 3.5.5** - Application framework
- **Spring Data JPA** - Database abstraction
- **Spring Boot Validation** - Input validation
- **H2 Database** - In-memory database for development
- **PostgreSQL** - Production database (optional)
- **SpringDoc OpenAPI 3** - API documentation
- **Lombok** - Boilerplate code reduction
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework for tests

## Quick Start

### Prerequisites
- Java 21 or higher
- Gradle 8.x (included via wrapper)

### Running the Application

```bash
# Clone the repository
git clone https://github.com/steve-davey/hmcts-dev-test-backend.git
cd hmcts-dev-test-backend

# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start on **http://localhost:4000**

### Key Endpoints
- **API Base**: `http://localhost:4000`
- **Swagger UI**: `http://localhost:4000/swagger-ui.html`
- **H2 Console**: `http://localhost:4000/h2-console` (dev only)
- **Health Check**: `http://localhost:4000/health`

## API Documentation

### Case Management Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/cases` | Get all cases | None | `PagedResponse<Case>` |
| GET | `/cases/{id}` | Get case by ID | None | `Case` |
| POST | `/cases` | Create new case | `Case` | `Case` (201) |
| PUT | `/cases/{id}` | Update existing case | `Case` | `Case` (200) |
| DELETE | `/cases/{id}` | Delete case | None | None (204) |

### Additional Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/get-example-case` | Get sample case data |
| GET | `/case-statuses` | Get available case status values |

### Case Model

```json
{
  "id": 1,
  "caseNumber": "ABC12345",
  "title": "Contract Dispute Resolution",
  "description": "Detailed case description",
  "status": "OPEN",
  "dueDate": "2024-12-31T17:00:00",
  "createdDate": "2024-01-15T10:30:00",
  "updatedDate": "2024-01-16T14:45:00"
}
```

### Case Status Values
- `OPEN` - Case is open and ready for work
- `IN_PROGRESS` - Case is currently being processed
- `CLOSED` - Case has been completed
- `CANCELLED` - Case has been cancelled

## Validation Rules

### Case Number
- **Required**: Cannot be null or empty
- **Length**: 3-20 characters
- **Pattern**: Only uppercase letters and numbers (A-Z, 0-9)
- **Unique**: Must be unique across all cases

### Title
- **Required**: Cannot be null or empty
- **Length**: 5-100 characters

### Description
- **Optional**: Can be null or empty
- **Max Length**: 500 characters

### Status
- **Required**: Must be one of the valid enum values
- **Values**: OPEN, IN_PROGRESS, CLOSED, CANCELLED

### Due Date
- **Required**: Cannot be null
- **Future**: Must be in the future (validated on create)

## Database Schema

```sql
CREATE TABLE cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_number VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    due_date TIMESTAMP NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_case_number ON cases(case_number);
CREATE INDEX idx_status ON cases(status);
CREATE INDEX idx_created_date ON cases(created_date);
```

## Configuration

### Application Properties

```yaml
# Server Configuration
server:
  port: 4000
  shutdown: "graceful"

# Database Configuration (H2 for development)
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true

  h2:
    console:
      enabled: true
      path: /h2-console

# API Documentation
springdoc:
  packagesToScan: uk.gov.hmcts.reform.dev.controllers
  writer-with-order-by-keys: true
```

### PostgreSQL Configuration (Production)

Uncomment the PostgreSQL section in `application.yaml` and set these environment variables:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=casemanagement
DB_USER_NAME=your_username
DB_PASSWORD=your_password
```

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run integration tests
./gradlew integration

# Run functional tests
./gradlew functional

# Run smoke tests
./gradlew smoke

# Generate test coverage report
./gradlew jacocoTestReport
```

### Test Structure

- **Unit Tests** (`src/test/java`): Test individual components and business logic
- **Integration Tests** (`src/integrationTest/java`): Test API endpoints with database
- **Functional Tests** (`src/functionalTest/java`): End-to-end testing scenarios
- **Smoke Tests** (`src/smokeTest/java`): Basic health and connectivity checks

### Example Test Cases

- Case creation with valid data
- Validation error handling
- Duplicate case number prevention
- Case retrieval and updates
- Status transition validation
- Due date validation (future dates only)

## Error Handling

The application uses a global exception handler that provides consistent error responses:

### Validation Errors (400 Bad Request)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/cases",
  "details": [
    "caseNumber: Case number must be between 3 and 20 characters",
    "dueDate: Due date is required"
  ]
}
```

### Data Integrity Errors (409 Conflict)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Data Integrity Violation", 
  "message": "The request conflicts with existing data",
  "path": "/cases"
}
```

### Not Found Errors (404 Not Found)
Cases that don't exist return a standard 404 response.

## Architecture

### Package Structure
```
uk.gov.hmcts.reform.dev/
├── controllers/          # REST controllers
│   ├── CaseController.java
│   └── RootController.java
├── models/              # Entity classes
│   ├── Case.java
│   ├── CaseStatus.java
│   ├── ExampleCase.java
│   └── PagedResponse.java
├── service/             # Business logic
│   ├── CaseService.java
│   └── CaseServiceImpl.java
├── repository/          # Data access
│   └── CaseRepository.java
├── config/              # Configuration
│   └── CorsConfiguration.java
└── exception/           # Exception handling
    └── GlobalExceptionHandler.java
```

### Key Design Patterns

- **Repository Pattern**: Data access abstraction with Spring Data JPA
- **Service Layer Pattern**: Business logic separation from controllers
- **DTO Pattern**: Clean API contracts with validation annotations
- **Exception Handling**: Centralized error handling with global exception handler

## Security Considerations

- **CORS Configuration**: Configured for cross-origin requests from frontend
- **Input Validation**: Comprehensive validation on all inputs
- **SQL Injection Prevention**: JPA/Hibernate with parameterized queries
- **Error Information**: Safe error messages without sensitive data exposure

## Performance Optimizations

- **Database Indexing**: Indexes on frequently queried columns
- **Connection Pooling**: HikariCP for efficient database connections
- **Lazy Loading**: JPA lazy loading for related entities
- **Pagination Support**: Built-in pagination for large datasets

## Deployment

### Building for Production

```bash
# Build JAR file
./gradlew bootJar

# The JAR will be created at build/libs/test-backend.jar
```

### Docker Support

```dockerfile
FROM openjdk:21-jre-slim
COPY build/libs/test-backend.jar app.jar
EXPOSE 4000
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | 4000 |
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | casemanagement |
| `DB_USER_NAME` | Database username | - |
| `DB_PASSWORD` | Database password | - |

## Development

### Adding New Features

1. **Create/Update Models**: Add new entities in `models` package
2. **Create Repository**: Extend `CrudRepository` for data access
3. **Implement Service**: Add business logic in service layer
4. **Create Controller**: Add REST endpoints with proper validation
5. **Add Tests**: Write comprehensive tests for all layers
6. **Update Documentation**: Update OpenAPI annotations

### Code Style

- Use **Lombok** annotations to reduce boilerplate
- Follow **Spring Boot** best practices
- Implement proper **validation** with Bean Validation
- Write **comprehensive tests** for all functionality
- Use **meaningful variable names** and proper documentation

## Troubleshooting

### Common Issues

**Port 4000 already in use**:
```bash
# Change port in application.yaml or set environment variable
SERVER_PORT=4001 ./gradlew bootRun
```

**Database connection errors**:
- Check H2 console at `/h2-console`
- Verify database configuration in `application.yaml`
- Ensure PostgreSQL is running (if using production config)

**Test failures**:
- Check that all required fields have valid test data
- Verify mock configurations in test setup
- Ensure database is properly reset between tests

### Logging

Enable debug logging by adding to `application.yaml`:
```yaml
logging:
  level:
    uk.gov.hmcts.reform.dev: DEBUG
    org.springframework.web: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for your changes
4. Ensure all tests pass (`./gradlew test`)
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## License

This project is developed for the HMCTS DTS Developer Challenge.