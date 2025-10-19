# DarTabkh Backend

A Spring Boot application for homemade meals management.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

## Setup Instructions

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE dartabkh_db;
CREATE USER postgres WITH PASSWORD 'root';
GRANT ALL PRIVILEGES ON DATABASE dartabkh_db TO postgres;
```

### 2. Application Configuration

The application is configured to connect to:
- Database: `dartabkh_db`
- Host: `localhost:5432`
- Username: `postgres`
- Password: `root`

You can modify these settings in `src/main/resources/application.properties`.

### 3. Running the Application

#### Using Maven Wrapper (Recommended)

```bash
# Run the application
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

#### Using Maven directly

```bash
# Run the application
mvn spring-boot:run
```

#### Building and Running JAR

```bash
# Build the application
./mvnw clean package

# Run the JAR file
java -jar target/darTabkh-0.0.1-SNAPSHOT.jar
```

### 4. API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/api-docs
- **Application**: http://localhost:8081

### 5. Development

The application includes:
- JWT Authentication
- PostgreSQL Database
- RESTful API
- Swagger Documentation
- MapStruct for DTO mapping
- Lombok for reducing boilerplate code

### 6. Testing

```bash
# Run tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report
```

## API Endpoints

The application provides endpoints for:
- Authentication (login/register)
- User management
- Category management
- Meal management
- Order management
- Cooker management

All endpoints are documented in Swagger UI.
