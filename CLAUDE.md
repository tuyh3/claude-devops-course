# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a learning project (claude-devops-course) designed to help study various programming languages, middleware, and databases using Claude Code assistance. The project follows industry-standard technology stack versions used by major tech companies and web3 companies.

## Business Scenario

This project simulates a **Telecom Operator Business System** with the following components:

- **TCBS (Telecom Business System)** - Core telecom business system
  - Customer Management (CRM)
  - Product Management
  - Billing System
  - Usage Records

- **TCOA (Telecom Open API)** - Capability Open Platform
  - API Application Management
  - API Call Logs

## Technology Stack Requirements

The project is configured with the following specific versions based on industry standards:

- **Java**: Java 21 (LTS) - configured with Java toolchain
- **Gradle**: 9.2.x - used for build automation (Maven is explicitly NOT used)
- **Spring Boot**: 3.5.7 - main application framework
- **Database**: Oracle 19c RAC (Real Application Clusters with ASM)
- **ORM Framework**: Spring Data JPA with Hibernate
- **Connection Pool**: HikariCP (built into Spring Boot)
- **Testing**: JUnit 5 (Jupiter) with Spring Boot Test

### Future Technology Stack (for reference when adding new components)

When expanding this project to include other technologies, use these versions:
- Python 3.11
- Go 1.24.x (N-1)
- Spring Boot 3.x
- MySQL 8.4 LTS, PostgreSQL 16, Oracle 19c
- Redis 7.0 LTS, Kafka 4.0.1
- Kubernetes v1.34
- Prometheus 3.5.0 (LTS), Grafana v12.x, Zabbix 7.0 LTS
- Ansible Automation Platform 2.6, OpenTofu 1.6.x
- ELK 9.2.1

## Dependencies

### Core Dependencies

The project uses the following key dependencies (configured in build.gradle):

- `spring-boot-starter-web` - Web and REST API support
- `spring-boot-starter-data-jpa` - JPA and Hibernate ORM
- `spring-boot-devtools` - Hot reload during development
- `spring-boot-starter-test` - Testing framework

### Oracle Database Dependencies

- `com.oracle.database.jdbc:ojdbc11:23.6.0.24.10` - Oracle JDBC driver for Java 11+
- `com.oracle.database.jdbc:ucp:23.6.0.24.10` - Universal Connection Pool

## Build Commands

### Essential Commands

```bash
# Build the entire project
./gradlew build

# Run the Spring Boot application
./gradlew bootRun

# Run with local profile (if you have database configured)
./gradlew bootRun --args='--spring.profiles.active=local'

# Alternative: Run using gradle run
./gradlew run

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.devops.course.MainTest"

# Run a specific test method
./gradlew test --tests "com.devops.course.MainTest.testJavaVersion"

# Clean build artifacts
./gradlew clean

# Build executable JAR
./gradlew bootJar

# View Gradle version and configuration
./gradlew --version
```

### Development Workflow Commands

```bash
# Continuous test execution (useful during development)
./gradlew test --continuous

# Build without running tests (faster iteration - commonly used!)
./gradlew clean build -x test --stacktrace

# Generate test reports (located in build/reports/tests/test/index.html)
./gradlew test

# Check for dependency updates
./gradlew dependencyUpdates

# Refresh dependencies
./gradlew --refresh-dependencies
```

## Project Structure

```
claude-devops-course/
├── doc/                                # 📚 Documentation directory
│   ├── README.md                       # Documentation index
│   ├── 01-gradle-springboot-...guide.md    # Gradle quick start (for Maven users)
│   ├── 02-intellij-idea-...setup.md        # IDEA configuration guide
│   ├── 03-spring-boot-oracle-...md         # Oracle RAC integration guide
│   └── oracle_dbpv_create_data.sql         # Database creation scripts
├── src/main/java/com/devops/course/
│   ├── Main.java                       # Application entry point
│   ├── controller/                     # REST API controllers
│   │   ├── HelloController.java        # Basic API endpoints
│   │   └── CustomerController.java     # Customer management API
│   ├── service/                        # Business logic layer
│   │   └── CustomerService.java        # Customer service
│   ├── repository/                     # Data access layer
│   │   └── CustomerRepository.java     # JPA repository for customers
│   └── entity/                         # JPA entity classes
│       └── Customer.java               # Customer entity (TCBS.CUSTOMERS table)
├── src/main/resources/
│   ├── application.yml                 # Main configuration (with placeholders)
│   └── application.yml.example         # Configuration template for users
├── src/test/java/com/devops/course/    # Test code
├── build.gradle                        # Build configuration
├── settings.gradle                     # Project settings
├── gradle.properties                   # Gradle JVM and performance settings
├── CLAUDE.md                           # This file - Claude Code guidance
└── README.md                           # Project overview and quick start
```

### Package Structure

- **Base package**: `com.devops.course`
- **Main class**: `com.devops.course.Main` (application entry point)
- **Group ID**: `com.devops.course`
- **Architecture**: Three-tier architecture (Controller → Service → Repository)

### Application Architecture

The project follows Spring Boot best practices with a three-tier architecture:

1. **Controller Layer** (`controller/`)
   - Handles HTTP requests and responses
   - REST API endpoints
   - Request validation and error handling

2. **Service Layer** (`service/`)
   - Business logic implementation
   - Transaction management
   - Data transformation

3. **Repository Layer** (`repository/`)
   - Database access using Spring Data JPA
   - Query methods and custom queries
   - Entity management

## Important Configuration Details

### Java Configuration

- The project uses Java 21 toolchain (configured in build.gradle:9-12)
- Java home is explicitly set in gradle.properties to ensure Java 21 is used
- UTF-8 encoding is enforced for all Java compilation

### Gradle Configuration

- Gradle wrapper version: 9.2.0
- Gradle daemon is enabled for performance
- Build cache is enabled
- Parallel execution is enabled
- JVM is configured with 2GB max heap and 512MB max metaspace

### Spring Boot Configuration

- Spring Boot version: 3.5.7 (configured in build.gradle)
- Application configuration: `src/main/resources/application.yml`
- Default server port: 8080
- Spring Boot DevTools enabled for hot reload during development
- Main application class is annotated with `@SpringBootApplication` (Main.java)

### Database Configuration

The project is configured to connect to Oracle 19c RAC database:

**Database Environment**:
- Database: Oracle 19c with ASM + RAC (Real Application Clusters)
- Service Name: `dbpv`
- RAC Nodes: 192.168.1.66:1521, 192.168.1.67:1521
- Load Balancing: ON
- Failover: ON (SELECT mode with BASIC method)
- Schema: `TCBS` (Telecom Business System)

**Configuration Files**:
1. `application.yml` - Contains configuration template with placeholder password
2. `application.yml.example` - Template for users to copy and configure
3. For local development, create `application-local.yml` with your actual database credentials

**Connection Pool** (HikariCP):
- Pool name: OracleHikariCP
- Minimum idle connections: 5
- Maximum pool size: 20
- Max lifetime: 30 minutes
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes

**JPA/Hibernate Configuration**:
- Dialect: Oracle12cDialect
- DDL auto: validate (production-safe)
- SQL logging: enabled (for development)
- Batch size: 20
- Physical naming strategy: StandardImpl (preserves case)

### REST API Endpoints

The application includes the following REST endpoints:

#### Basic Endpoints
- `GET /api/hello` - Returns welcome message with Java version info
- `GET /api/health` - Returns application health status

#### Customer Management Endpoints (TCBS)
- `GET /api/customers` - Get all customers from TCBS.CUSTOMERS table
- `GET /api/customers/{customerId}` - Get customer by ID
- `GET /api/customers/type/{customerType}` - Get customers by type (e.g., "Individual", "Enterprise")

**Example API Usage**:
```bash
# Test basic endpoint
curl http://localhost:8080/api/hello

# Get all customers
curl http://localhost:8080/api/customers

# Get specific customer
curl http://localhost:8080/api/customers/CUST000001

# Get customers by type
curl http://localhost:8080/api/customers/type/Individual
```

### Testing Framework

- JUnit 5 (Jupiter) is used for testing
- Spring Boot Test (`@SpringBootTest`) for integration tests
- MockMvc for testing REST controllers
- Test platform launcher is included for IDE support
- Tests are located in `src/test/java/com/devops/course/`

## Quick Start for New Developers

### Prerequisites
- Java 21 or higher installed
- Git installed
- (Optional) Access to Oracle database for testing database features

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd claude-devops-course
   ```

2. **Configure database (optional)**
   ```bash
   # Copy the configuration template
   cp src/main/resources/application.yml.example src/main/resources/application-local.yml

   # Edit the file and replace 'your_password_here' with actual password
   vim src/main/resources/application-local.yml
   ```

3. **Build the project**
   ```bash
   # First build (will download Gradle 9.2.0 automatically)
   ./gradlew clean build
   ```

4. **Run the application**
   ```bash
   # Run with default profile
   ./gradlew bootRun

   # Or with local profile (if you configured database)
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

5. **Test the application**
   ```bash
   # Access in browser or use curl
   curl http://localhost:8080/api/hello
   curl http://localhost:8080/api/health
   ```

### Without Database
The application can start without database connection. Database-related endpoints will fail, but basic endpoints (`/api/hello`, `/api/health`) will work.

## Development Guidelines

### Adding New Technologies

When adding new middleware or database components to this learning project:

1. Follow the version specifications listed in the Technology Stack section
2. Create separate packages under `com.devops.course` for each technology area
3. Add corresponding test classes for each new component
4. Update build.gradle with necessary dependencies
5. Document the new component in the relevant documentation files

### Code Organization

- Main application logic goes in `src/main/java/com/devops/course/`
- Test files mirror the main source structure in `src/test/java/com/devops/course/`
- Use the same package structure for tests as for source code
- Follow three-tier architecture: Controller → Service → Repository

### Working with Gradle

- Always use the Gradle wrapper (`./gradlew`) rather than a system-installed Gradle
- The wrapper ensures consistent Gradle version (9.2.0) across all environments
- Gradle properties are pre-configured for optimal performance on the development machine
- For Maven users: See `doc/01-gradle-springboot-project-initialization-guide.md`

### Database Development

- Database schema is defined in `doc/oracle_dbpv_create_data.sql`
- Entity classes are in `entity/` package with JPA annotations
- Repositories extend `JpaRepository` for automatic CRUD operations
- Use Hibernate naming strategy to preserve Oracle table/column names (case-sensitive)
- Always test database connections before committing code

## Common Issues and Solutions

### Build Failures
```bash
# Clear Gradle cache and rebuild
./gradlew clean
./gradlew --refresh-dependencies
./gradlew build
```

### Database Connection Issues
1. Check network connectivity: `ping 192.168.1.66`
2. Verify credentials in `application-local.yml`
3. Ensure Oracle service is running
4. Check firewall settings for port 1521
5. Review detailed logs in console output

### Java Version Issues
- Ensure Java 21 is installed: `java -version`
- Gradle wrapper will use Java 21 as configured in `gradle.properties`
- If using IDEA, configure Project SDK to Java 21

### Gradle Wrapper Permission Issues
```bash
# On macOS/Linux, add execute permission
chmod +x gradlew
```

## Documentation Resources

For detailed guides, refer to the `doc/` directory:

1. **[Gradle Quick Start](doc/01-gradle-springboot-project-initialization-guide.md)** - For Maven users transitioning to Gradle
2. **[IDEA Configuration](doc/02-intellij-idea-gradle-project-setup.md)** - Complete IDEA setup guide
3. **[Oracle Integration](doc/03-spring-boot-oracle-rac-integration.md)** - Oracle RAC database integration

## Working with Claude Code

This project is optimized for Claude Code development:

- All configurations have detailed comments
- Documentation is comprehensive and easy to navigate
- Common commands are documented in README.md and this file
- Code follows industry best practices and patterns

**Common Claude Code Commands**:
- "Run the Spring Boot application"
- "Add a new REST API endpoint for [feature]"
- "Explain the Oracle RAC configuration"
- "Run tests and fix any failures"
- "Add a new JPA entity for [table_name]"