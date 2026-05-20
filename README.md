# FinWallet API

A RESTful backend API simulating a digital wallet and P2P transfer system,
built with Java 21 and Spring Boot 3. Designed as a portfolio project showcasing
real-world fintech backend patterns.

## Features
- JWT authentication with access/refresh token rotation
- P2P transfers with ACID transactions and optimistic locking
- Daily transfer limits with automatic reset
- Fraud detection (rate limiting, large transfer detection)
- Email notifications via JavaMailSender
- Full test suite with Testcontainers (real PostgreSQL)
- Dockerized with Docker Compose
- CI pipeline via GitHub Actions

## Tech Stack
Java 21 · Spring Boot 3 · Spring Security · PostgreSQL · Flyway · Docker · JUnit 5 · Testcontainers

## Quick Start
\`\`\`bash
git clone https://github.com/your-username/my_finwallet
cd finwallet-api
docker compose up
# API running at http://localhost:8080
\`\`\`

## Architecture decisions
- **Pessimistic locking** on account reads during transfers to prevent race conditions
- **Flyway** for database migrations instead of Hibernate auto-ddl
- **Separate dev/prod profiles** with externalized configuration
- **DTOs** to decouple API contract from internal domain model

## API Documentation
under construction

## Live Demo
under construction

## License

MIT License — feel free to use this project as a reference.
