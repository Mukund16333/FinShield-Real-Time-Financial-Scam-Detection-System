# FinShield — Real-Time Financial Scam Detection System

FinShield is a Spring Boot REST API that evaluates financial transactions
in real time against a configurable, rule-based fraud scoring engine —
flagging or blocking suspicious activity as it happens, with full audit
traceability and role-based access control.

## Features

- **JWT authentication** with role-based access control (`ADMIN` / `CUSTOMER`)
- **Real-time fraud scoring engine** — velocity checks, amount thresholds,
  and blacklist detection, combined into a single risk score and decision
  (`SUCCESS` / `FLAGGED` / `BLOCKED`)
- **Automated alerting** on flagged/blocked transactions
- **Immutable audit logging** of every transaction evaluation and admin action
- **Admin-managed fraud rules and blacklist**, configurable without redeploying
- **Swagger/OpenAPI** documentation for all endpoints
- **Unit + integration test suite** (JUnit 5, Mockito, MockMvc)

## Tech Stack

Java 17 · Spring Boot 3.x · Spring Security · JWT (jjwt) · Spring Data JPA
· MySQL · Maven · Swagger/OpenAPI · JUnit 5 · Mockito

## Project Structure

```
finshield/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/finshield/
    │   │   ├── config/       # Security, Swagger, JPA auditing config
    │   │   ├── security/     # JWT provider/filter, user details service
    │   │   ├── controller/   # REST endpoints
    │   │   ├── service/      # Business logic + fraud/ scoring engine
    │   │   ├── repository/   # Spring Data JPA repositories
    │   │   ├── entity/       # JPA entities + enums
    │   │   ├── dto/          # request/ and response/ payloads
    │   │   ├── exception/    # Global exception handling
    │   │   └── util/         # RiskScoreCalculator
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       └── data.sql
    └── test/
        └── java/com/finshield/
            ├── service/      # unit tests, incl. service/fraud/
            └── controller/   # integration tests
```

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8 running locally (or update the datasource URL for a remote instance)

### 1. Clone and configure
```bash
git clone <your-repo-url>
cd finshield
```

Set the required environment variables (or edit `application-dev.yml` directly for local testing):
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_db_password
export JWT_SECRET=$(openssl rand -base64 32)
```

### 2. Run the application
```bash
mvn spring-boot:run
```
The app starts on `http://localhost:8080` using the `dev` profile by default
(seeds `data.sql` on startup: a sample admin, customer, fraud rules, and
blacklist entries — see credentials in `data.sql` comments).

### 3. Explore the API
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 4. Run tests
```bash
mvn test
```

### 5. Build a deployable JAR
```bash
mvn clean package
java -jar target/finshield-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Key Endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Authenticate, receive JWT |
| POST | `/api/transactions` | Customer | Submit a transaction for fraud evaluation |
| GET | `/api/transactions/{id}` | Customer/Admin | Retrieve a transaction |
| GET | `/api/admin/alerts` | Admin | List flagged/blocked transaction alerts |
| POST | `/api/admin/rules` | Admin | Create/update a fraud rule |
| POST | `/api/admin/blacklist` | Admin | Add an entity to the blacklist |
| GET | `/api/admin/audit-logs` | Admin | View the full audit trail |

## Fraud Scoring Logic

Each transaction is evaluated against every active rule; triggered rules
contribute weighted points to a composite risk score:

| Rule | Weight |
|---|---|
| Velocity check (>5 txns / 10 min) | 30 |
| Amount threshold (≥ configured limit) | 35 |
| Blacklist match | 100 |

| Score | Decision |
|---|---|
| < 40 | `SUCCESS` |
| 40–79 | `FLAGGED` |
| ≥ 80 | `BLOCKED` |

Thresholds are externalized in `application.yml` under `finshield.fraud.*`.

## Future Enhancements
- ML-based anomaly detection to complement/replace static rules
- Kafka-based streaming ingestion for higher throughput
- Device fingerprinting and geo-velocity (impossible-travel) checks
- Admin dashboard for visualizing fraud trends

## Author
Mukund Balaji B
