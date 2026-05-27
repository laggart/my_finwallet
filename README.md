# FinWallet API

A RESTful backend API simulating a digital wallet and P2P transfer system,
built with Java 21 and Spring Boot 3. Designed as a portfolio project showcasing
real-world fintech backend patterns.

🚀 **Live demo**: [https://myfinwallet-production.up.railway.app/swagger-ui.html](https://myfinwallet-production.up.railway.app/swagger-ui.html)

---

## Features
- JWT authentication with access/refresh token rotation
- User registration with automatic account creation
- P2P transfers with ACID transactions and pesimistic locking
- Daily transfer limits with automatic reset
- Fraud detection (rate limiting, large transfer detection)
- Transaction history with filters and pagination
- Full API documentation with Swagger UI
- Database migrations managed by Flyway
- Dockerized with Docker Compose
- Dockerized and deployed on Railway

## Tech Stack
| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (jjwt) |
| Database | PostgreSQL 15 |
| Migrations | Flyway |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Containerization | Docker |
| Deployment | Railway |
| Build tool | Maven |

## Architecture

The project follows a layered architecture pattern standard in enterprise Java:

```
Controller → Service → Repository → Database
```

```
src/
├── controller/        HTTP layer — receives requests, returns responses
├── service/           Business logic — transfers, fraud detection, auth
├── repository/        Data access — JPA repositories with custom queries
├── model/             JPA entities — User, Account, Transaction
├── dto/               Request and response objects
├── security/          JWT filter and UserDetailsService
├── config/            Security configuration
└── exception/         Custom exceptions and global error handler
```

---

## API Endpoints

### Auth — `/api/v1/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/register` | No | Register a new user and create account |
| POST | `/login` | No | Login and receive JWT token |

### Account — `/api/v1/accounts`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/me` | Yes | Get my account details and balance |
| GET | `/me/transactions` | Yes | Paginated transaction history |

### Transfers — `/api/v1/transfers`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/` | Yes | Send a transfer to another account |

Full interactive documentation available at the [Swagger UI](https://myfinwallet-production.up.railway.app/swagger-ui.html).

---

## Key Technical Decisions

**Pessimistic locking on transfers** — Account rows are locked at the database level during transfers using `@Lock(LockModeType.PESSIMISTIC_WRITE)`. This prevents race conditions when two transfers happen simultaneously on the same account.

**ACID transactions** — The entire transfer operation (debit sender, credit receiver, save record) runs inside a single `@Transactional` method. If anything fails, the whole operation rolls back — no money is lost.

**Flyway over Hibernate auto-ddl** — Database schema is managed through versioned SQL migration files. This gives full control over schema changes and makes the history auditable in Git.

**DTO pattern** — JPA entities are never exposed directly in API responses. DTOs decouple the API contract from the internal domain model, so database changes don't break API consumers.

**Fraud detection** — Simple rules-based detection flags suspicious activity: more than 5 transfers in one hour, or a transfer exceeding 80% of available balance.

---

## Quick Start

### Prerequisites

- Java 21
- Docker and Docker Compose
- Maven

### Run locally

\`\`\`bash
git clone https://github.com/laggart/my_finwallet.git
cd my_finwallet

# Start PostgreSQL
docker compose up -d

# Run the application
./mvnw spring-boot:run

# API running at http://localhost:8080
# Swagger UI: `http://localhost:8080/swagger-ui.html`
\`\`\`

### Environment variables

| Variable | Description | Default (dev) |
|----------|-------------|---------------|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/finwallet` |
| `DB_USERNAME` | Database username | `finwallet` |
| `DB_PASSWORD` | Database password | `finwallet` |
| `JWT_SECRET` | Secret key for signing JWT tokens | Dev key (change in production) |

---

## Example Usage

**Register a user**
```bash
curl -X POST https://myfinwallet-production.up.railway.app/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"SecurePass123!","firstName":"Juan","lastName":"García"}'
```

**Make a transfer**
```bash
curl -X POST https://myfinwallet-production.up.railway.app/api/v1/transfers \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"receiverAccountNumber":"FW-0000000001","amount":50.00,"description":"Dinner"}'
```

---

## Architecture decisions
- **Pessimistic locking** on account reads during transfers to prevent race conditions
- **Flyway** for database migrations instead of Hibernate auto-ddl
- **Externalized configuration** with environment variables for prod/local separation
- **DTOs** to decouple API contract from internal domain model

## What I would add next

- Refresh token rotation with persistent storage
- Email verification on registration
- Async email notifications on transfer completion
- Integration tests with Testcontainers
- CI/CD pipeline with GitHub Actions
- Rate limiting with Bucket4j
- Metrics and monitoring with Spring Actuator + Prometheus

---

## License

MIT License — feel free to use this project as a reference.
