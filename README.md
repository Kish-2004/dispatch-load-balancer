# Dispatch Load Balancer

A Java Spring Boot REST API application that assigns delivery orders to available vehicles based on vehicle capacity, order priority, and geographical distance.

The application uses PostgreSQL for persistent storage, Flyway for database migrations, and Docker Compose for containerized deployment.

## 1. Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming language |
| Spring Boot 3.5.7 | REST API development |
| Spring Data JPA | Database operations |
| PostgreSQL 17 | Relational database |
| Hibernate | Object-relational mapping |
| Flyway | Database schema migrations |
| Maven | Build and dependency management |
| Springdoc OpenAPI / Swagger | API documentation |
| JUnit 5 | Automated testing |
| Mockito | Mock-based testing |
| JaCoCo | Code coverage |
| Docker | Containerization |
| Docker Compose | Multi-container deployment |

## 2. Features

- Accept batches of delivery orders.
- Accept batches of vehicles.
- Persist orders and vehicles in PostgreSQL.
- Prioritize HIGH, MEDIUM, and LOW priority orders.
- Assign orders without exceeding vehicle capacity.
- Calculate geographical distances using the Haversine formula.
- Calculate incremental route distances when inserting delivery stops.
- Return unassigned orders when no vehicle can accommodate them.
- Validate incoming request data.
- Handle validation, database, and unexpected errors centrally.
- Generate dispatch plans through REST APIs.
- Expose interactive API documentation through Swagger.

## 3. Dispatch Optimization Approach

The dispatch optimizer uses a priority-based greedy assignment strategy.

### Assignment process

1. Sort orders by priority: HIGH, MEDIUM, then LOW.
2. Within the same priority, consider heavier orders first.
3. Use order ID as a deterministic tie-breaker.
4. Evaluate vehicles that have sufficient remaining capacity.
5. Calculate the additional route distance for each possible insertion position.
6. Assign the order to the vehicle and insertion position with the smallest additional distance.
7. Leave the order unassigned if no vehicle can accommodate it.

### Distance calculation

The application uses the Haversine formula to calculate geographical distances between latitude and longitude coordinates.

- Earth radius: 6,371 km.
- Distance is calculated between the vehicle's starting location and delivery locations.
- Route distance is updated when an order is inserted into a route.

### Capacity constraint

For every vehicle:

`Total assigned package weight <= Vehicle capacity`

### Optimization note

The implementation uses a greedy heuristic. It aims to fulfill higher-priority orders while reducing incremental route distance. It does not guarantee a globally optimal assignment or route.

## 4. Project Structure

```text
dispatch-load-balancer/
│
├── .github/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/com/logistics/dispatch/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── util/
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │
│   └── test/
│       └── java/com/logistics/dispatch/
│
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## 5. Database Design

PostgreSQL is used to persist application data.

### delivery_orders

Stores delivery order information:

- order_id
- latitude
- longitude
- address
- package_weight
- priority

### vehicles

Stores vehicle information:

- vehicle_id
- capacity
- current_latitude
- current_longitude
- current_address

Flyway manages database schema migrations automatically during application startup.

## 6. REST API Endpoints

Base URL:

`http://localhost:8080`

### 6.1 Submit Delivery Orders

**POST** `/api/dispatch/orders`

Request:

```json
{
  "orders": [
    {
      "orderId": "ORD001",
      "latitude": 28.6139,
      "longitude": 77.2090,
      "address": "New Delhi, India",
      "packageWeight": 25.500,
      "priority": "HIGH"
    }
  ]
}
```

Success response:

```json
{
  "message": "Delivery orders accepted.",
  "status": "success"
}
```

HTTP status: `201 Created`

### 6.2 Submit Vehicles

**POST** `/api/dispatch/vehicles`

Request:

```json
{
  "vehicles": [
    {
      "vehicleId": "VEH001",
      "capacity": 100.000,
      "currentLatitude": 28.6139,
      "currentLongitude": 77.2090,
      "currentAddress": "New Delhi, India"
    }
  ]
}
```

Success response:

```json
{
  "message": "Vehicle details accepted.",
  "status": "success"
}
```

HTTP status: `201 Created`

### 6.3 Generate Dispatch Plan

**GET** `/api/dispatch/plan`

Returns:

- Vehicle ID.
- Total assigned package weight.
- Total route distance.
- Assigned delivery orders.
- Unassigned delivery orders.

Example response structure:

```json
{
  "dispatchPlan": [
    {
      "vehicleId": "VEH001",
      "totalLoad": 25.500,
      "totalDistance": "2.45 km",
      "assignedOrders": [
        {
          "orderId": "ORD001",
          "latitude": 28.6139,
          "longitude": 77.2090,
          "address": "New Delhi, India",
          "packageWeight": 25.500,
          "priority": "HIGH"
        }
      ]
    }
  ],
  "unassignedOrders": []
}
```

The values above illustrate the response structure, not a guaranteed allocation.

HTTP status: `200 OK`

## 7. Request Validation and Error Handling

The application validates:

- Required order and vehicle IDs.
- Required delivery and vehicle addresses.
- Latitude between -90 and 90.
- Longitude between -180 and 180.
- Positive package weights and vehicle capacities.
- Maximum ID and address lengths.
- Maximum three decimal places for weights and capacities.
- Supported priority values: HIGH, MEDIUM, LOW.
- Non-empty batch requests.

A centralized `GlobalExceptionHandler` handles validation errors, invalid JSON, database constraint violations, and unexpected exceptions.

## 8. Run Locally

### Prerequisites

- Java 21
- Maven
- PostgreSQL 17

### Configure the database

Create a PostgreSQL database named:

`dispatch_db`

Configure the database environment variables in PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/dispatch_db"
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
```

Replace the example credentials with your local PostgreSQL credentials.

### Start the application

```powershell
mvn spring-boot:run
```

Flyway automatically applies pending migrations during startup.

The API runs at:

`http://localhost:8080`

## 9. Run with Docker Compose

### Prerequisites

- Docker Desktop
- Docker Compose

Build and start the Spring Boot application and PostgreSQL:

```powershell
docker compose up -d --build
```

Check running containers:

```powershell
docker ps
```

View application logs:

```powershell
docker logs dispatch-load-balancer
```

View PostgreSQL logs:

```powershell
docker logs dispatch-postgres
```

Stop the containers:

```powershell
docker compose down
```

PostgreSQL data is stored in a Docker named volume and persists when containers are stopped and restarted.

**Warning:** Running `docker compose down -v` removes the named database volume and its stored data.

## 10. API Documentation

Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

OpenAPI specification:

`http://localhost:8080/v3/api-docs`

Swagger allows users to inspect API endpoints, request and response schemas, and execute API requests interactively.

## 11. Automated Testing

The project includes automated tests covering:

- Dispatch optimization.
- Priority ordering.
- Vehicle capacity validation.
- Distance calculations.
- Route-distance calculations.
- Dispatch service behavior.
- Controller responses.
- Request validation.
- Exception handling.

Run all tests:

```powershell
mvn clean test
```

Run the full verification and generate the JaCoCo coverage report:

```powershell
mvn clean verify
```

**Latest verified test result: 46 tests passed, with 0 failures and 0 errors.**

JaCoCo HTML report:

`target/site/jacoco/index.html`

## 12. Build the Application

Build the executable Spring Boot JAR:

```powershell
mvn clean package
```

Generated artifact:

`target/dispatch-load-balancer-0.0.1-SNAPSHOT.jar`

## 13. Verified Project Status

The project has been verified with:

- 46 automated tests passing.
- PostgreSQL database connectivity.
- Flyway migration execution.
- REST API requests and dispatch plan generation.
- Database persistence.
- Swagger UI availability.
- Docker image build.
- Spring Boot and PostgreSQL containers running together.

---

**Dispatch Load Balancer**

Java 21 | Spring Boot | PostgreSQL | Docker
