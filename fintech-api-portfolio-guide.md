# 🏦 FinWallet API — Guía de Proyecto Portfolio Backend
> Guía paso a paso para construir una API REST de gestión de pagos y transferencias con Java + Spring Boot.  
> Nivel: Junior con conocimientos de DAW. Objetivo: portfolio real para entrar al sector fintech/banca.

---

## 📋 Tabla de Contenidos

1. [Visión general del proyecto](#1-visión-general-del-proyecto)
2. [Stack tecnológico](#2-stack-tecnológico)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Modelo de datos](#4-modelo-de-datos)
5. [Endpoints de la API](#5-endpoints-de-la-api)
6. [Plan de construcción por fases](#6-plan-de-construcción-por-fases)
7. [Seguridad con JWT](#7-seguridad-con-jwt)
8. [Transacciones y concurrencia](#8-transacciones-y-concurrencia)
9. [Tests](#9-tests)
10. [Docker y despliegue](#10-docker-y-despliegue)
11. [CI/CD con GitHub Actions](#11-cicd-con-github-actions)
12. [README profesional](#12-readme-profesional)
13. [Cómo defenderlo en entrevista](#13-cómo-defenderlo-en-entrevista)

---

## 1. Visión General del Proyecto

### ¿Qué es FinWallet API?
Una API REST que simula el backend de una aplicación de pagos (tipo Bizum o Revolut simplificado). Los usuarios pueden registrarse, tener una cuenta con saldo, hacer transferencias entre sí y consultar su historial.

### ¿Por qué este proyecto?
- Cubre exactamente lo que se evalúa en entrevistas de banca y fintech
- Las **transacciones ACID** y la **concurrencia** son temas que distinguen a un junior de un mid
- No lo hace casi nadie — la mayoría sube e-commerce o blogs
- Tiene lógica de negocio real, no solo CRUD
- Es defendible en entrevista durante 30 minutos

### Funcionalidades principales
- Registro e inicio de sesión con JWT + refresh tokens
- Cuentas de usuario con saldo en euros
- Transferencias entre usuarios con transacciones ACID
- Límites diarios de transferencia por usuario
- Historial de movimientos con filtros y paginación
- Detección simple de operaciones sospechosas
- Notificaciones por email al operar
- Tests de integración con base de datos real

---

## 2. Stack Tecnológico

```
Backend:      Java 21 + Spring Boot 3.x
Seguridad:    Spring Security + JWT (jjwt)
Base datos:   PostgreSQL 15
Migraciones:  Flyway
Testing:      JUnit 5 + Mockito + Testcontainers
Contenedores: Docker + Docker Compose
CI/CD:        GitHub Actions
Email:        JavaMailSender (con Mailtrap para dev)
Despliegue:   Railway o Fly.io
```

### Dependencias Maven (pom.xml)

```xml
<!-- Spring Boot Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Base de datos -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Utilidades -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

---

## 3. Estructura del Proyecto

```
finwallet-api/
├── src/
│   ├── main/
│   │   ├── java/com/finwallet/
│   │   │   ├── FinwalletApplication.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtConfig.java
│   │   │   │   └── MailConfig.java
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AccountController.java
│   │   │   │   └── TransactionController.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── AccountService.java
│   │   │   │   ├── TransactionService.java
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   └── FraudDetectionService.java
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── AccountRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   └── RefreshTokenRepository.java
│   │   │   │
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Account.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── RefreshToken.java
│   │   │   │   └── enums/
│   │   │   │       ├── TransactionStatus.java
│   │   │   │       └── TransactionType.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   └── TransferRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── AuthResponse.java
│   │   │   │       ├── AccountResponse.java
│   │   │   │       └── TransactionResponse.java
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InsufficientFundsException.java
│   │   │   │   ├── DailyLimitExceededException.java
│   │   │   │   └── SuspiciousActivityException.java
│   │   │   │
│   │   │   └── security/
│   │   │       ├── JwtAuthFilter.java
│   │   │       └── UserDetailsServiceImpl.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   │           ├── V1__create_users_table.sql
│   │           ├── V2__create_accounts_table.sql
│   │           ├── V3__create_transactions_table.sql
│   │           └── V4__create_refresh_tokens_table.sql
│   │
│   └── test/
│       └── java/com/finwallet/
│           ├── integration/
│           │   ├── AuthIntegrationTest.java
│           │   └── TransactionIntegrationTest.java
│           └── unit/
│               ├── TransactionServiceTest.java
│               └── FraudDetectionServiceTest.java
│
├── docker-compose.yml
├── Dockerfile
├── .github/
│   └── workflows/
│       └── ci.yml
└── README.md
```

---

## 4. Modelo de Datos

### Entidad: User
```java
@Entity
@Table(name = "users")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private boolean enabled = false; // activación por email

    @Column(nullable = false)
    private boolean locked = false;  // bloqueo por actividad sospechosa

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;
}
```

### Entidad: Account
```java
@Entity
@Table(name = "accounts")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String accountNumber; // generado automáticamente (IBAN simplificado)

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyTransferLimit; // límite diario, por defecto 1000€

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyTransferredAmount; // acumulado del día

    private LocalDate lastTransferDate; // para resetear el acumulado diario

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Versión para optimistic locking — clave para concurrencia
    @Version
    private Long version;
}
```

### Entidad: Transaction
```java
@Entity
@Table(name = "transactions")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id", nullable = false)
    private Account senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id", nullable = false)
    private Account receiverAccount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // PENDING, COMPLETED, FAILED, FLAGGED

    @Enumerated(EnumType.STRING)
    private TransactionType type; // TRANSFER, DEPOSIT, WITHDRAWAL

    private String failureReason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}
```

### Migraciones Flyway

**V1__create_users_table.sql**
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

**V2__create_accounts_table.sql**
```sql
CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number VARCHAR(30) UNIQUE NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    daily_transfer_limit NUMERIC(10, 2) NOT NULL DEFAULT 1000.00,
    daily_transferred_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    last_transfer_date DATE,
    user_id UUID NOT NULL REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0
);
```

**V3__create_transactions_table.sql**
```sql
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_account_id UUID NOT NULL REFERENCES accounts(id),
    receiver_account_id UUID NOT NULL REFERENCES accounts(id),
    amount NUMERIC(10, 2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP
);

CREATE INDEX idx_transactions_sender ON transactions(sender_account_id);
CREATE INDEX idx_transactions_receiver ON transactions(receiver_account_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
```

---

## 5. Endpoints de la API

### Auth — `/api/v1/auth`

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/register` | No | Registro de usuario |
| POST | `/login` | No | Login, devuelve JWT |
| POST | `/refresh` | No | Refresca el access token |
| GET | `/verify?token=xxx` | No | Verifica email |
| POST | `/logout` | Sí | Invalida refresh token |

### Account — `/api/v1/accounts`

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/me` | Sí | Mi cuenta y saldo |
| GET | `/me/transactions` | Sí | Historial paginado |
| GET | `/me/transactions/{id}` | Sí | Detalle de transacción |

### Transfers — `/api/v1/transfers`

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/` | Sí | Realizar transferencia |

### Ejemplos de request/response

**POST /api/v1/auth/register**
```json
// Request
{
  "email": "juan@example.com",
  "password": "SecurePass123!",
  "firstName": "Juan",
  "lastName": "García"
}

// Response 201
{
  "message": "Registro exitoso. Revisa tu email para activar la cuenta."
}
```

**POST /api/v1/auth/login**
```json
// Request
{
  "email": "juan@example.com",
  "password": "SecurePass123!"
}

// Response 200
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "d4f5a6b7-...",
  "expiresIn": 900
}
```

**POST /api/v1/transfers**
```json
// Request
{
  "receiverAccountNumber": "FW-0000123456",
  "amount": 150.00,
  "description": "Cena del viernes"
}

// Response 200
{
  "transactionId": "uuid-...",
  "status": "COMPLETED",
  "amount": 150.00,
  "newBalance": 850.00,
  "completedAt": "2024-01-15T20:30:00"
}

// Response 422 — saldo insuficiente
{
  "error": "INSUFFICIENT_FUNDS",
  "message": "Saldo insuficiente. Disponible: 50.00€"
}

// Response 422 — límite diario
{
  "error": "DAILY_LIMIT_EXCEEDED",
  "message": "Has alcanzado el límite diario de transferencias (1000.00€)"
}
```

**GET /api/v1/accounts/me/transactions?page=0&size=10&from=2024-01-01&to=2024-01-31**
```json
{
  "content": [
    {
      "id": "uuid-...",
      "type": "TRANSFER",
      "direction": "OUTGOING",
      "amount": 150.00,
      "description": "Cena del viernes",
      "counterpart": "María López",
      "status": "COMPLETED",
      "createdAt": "2024-01-15T20:30:00"
    }
  ],
  "totalElements": 45,
  "totalPages": 5,
  "currentPage": 0
}
```

---

## 6. Plan de Construcción por Fases

### FASE 1 — Fundamentos (Semana 1-2)
> Objetivo: API funcionando con registro, login y JWT

**Paso 1: Configuración inicial**
- Crear proyecto en [start.spring.io](https://start.spring.io) con las dependencias del stack
- Configurar `application.yml` con perfiles dev/prod
- Levantar PostgreSQL con Docker Compose
- Configurar Flyway y crear la primera migración

**Paso 2: Entidades y repositorios**
- Crear `User`, `Account`, `RefreshToken`
- Crear repositorios JPA correspondientes
- Verificar que Flyway crea las tablas al arrancar

**Paso 3: Seguridad y JWT**
- Implementar `JwtService` (generación, validación, extracción)
- Implementar `UserDetailsServiceImpl`
- Configurar `SecurityConfig` (rutas públicas vs protegidas)
- Implementar `JwtAuthFilter`

**Paso 4: Registro y login**
- Implementar `AuthService` con registro y login
- Crear `AuthController`
- Probar con Postman o HTTPie

---

### FASE 2 — Lógica de Negocio (Semana 3-4)
> Objetivo: Transferencias funcionando con validaciones reales

**Paso 5: Verificación de email**
- Configurar Mailtrap para desarrollo
- Implementar `EmailService`
- Generar token de verificación al registrar
- Endpoint para confirmar email

**Paso 6: Servicio de transferencias**
- Implementar `TransactionService.transfer()` con `@Transactional`
- Validar saldo suficiente
- Validar límite diario (con reset automático)
- Actualizar saldo sender y receiver en una sola transacción
- Guardar registro en `transactions`

**Paso 7: Historial y paginación**
- Implementar consulta de historial con `Pageable`
- Añadir filtros por fecha (from/to)
- Diferenciar transacciones entrantes y salientes

**Paso 8: Manejo de errores global**
- Implementar `GlobalExceptionHandler` con `@RestControllerAdvice`
- Crear excepciones de dominio propias
- Respuestas de error estandarizadas

---

### FASE 3 — Seguridad Avanzada (Semana 5)
> Objetivo: Lo que diferencia este proyecto de los demás

**Paso 9: Detección de actividad sospechosa**
- `FraudDetectionService` con reglas simples:
  - Más de 5 transferencias en 1 hora
  - Transferencia > 50% del saldo en una sola operación
  - Receptor nunca antes utilizado + importe alto
- Si se activa: marcar transacción como `FLAGGED`, notificar por email, opcionalmente bloquear cuenta

**Paso 10: Optimistic Locking**
- Verificar que `@Version` en `Account` funciona correctamente
- Escribir test de concurrencia con múltiples threads
- Manejar `OptimisticLockException` correctamente

**Paso 11: Refresh tokens**
- Implementar almacenamiento de refresh tokens en BD
- Rotación de tokens (cada refresh invalida el anterior)
- Expiración y limpieza automática

---

### FASE 4 — Calidad y Despliegue (Semana 6)
> Objetivo: Proyecto listo para mostrar

**Paso 12: Tests**
- Tests unitarios para `TransactionService` y `FraudDetectionService`
- Tests de integración con Testcontainers
- Cobertura mínima objetivo: 70%

**Paso 13: Docker**
- Dockerfile para la aplicación
- Docker Compose completo (app + PostgreSQL)
- Variables de entorno externalizadas

**Paso 14: GitHub Actions**
- Pipeline de CI que ejecute los tests
- Build de la imagen Docker

**Paso 15: Despliegue**
- Desplegar en Railway o Fly.io
- Configurar variables de entorno en producción
- URL pública funcionando

---

## 7. Seguridad con JWT

### application.yml
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/finwallet}
    username: ${DB_USERNAME:finwallet}
    password: ${DB_PASSWORD:finwallet}
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway gestiona el schema, JPA solo valida
    show-sql: false
  flyway:
    enabled: true

app:
  jwt:
    secret: ${JWT_SECRET:cambiar-esto-en-produccion-minimo-256-bits-aqui}
    access-token-expiration: 900000      # 15 minutos en ms
    refresh-token-expiration: 604800000  # 7 días en ms
  mail:
    from: noreply@finwallet.com
```

### JwtService.java (esqueleto)
```java
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## 8. Transacciones y Concurrencia

Este es **el tema más importante** del proyecto. Debes entenderlo bien para la entrevista.

### El problema sin transacciones

```
// ❌ Sin @Transactional — PELIGROSO
// Si falla entre el débito y el crédito, se pierde dinero
senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
accountRepository.save(senderAccount); // ← si falla aquí...
receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
accountRepository.save(receiverAccount); // ...esto nunca se ejecuta
```

### La solución correcta

```java
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;
    private final FraudDetectionService fraudDetectionService;

    @Transactional  // ← Todo dentro es una sola unidad atómica
    public TransactionResponse transfer(UUID senderUserId, TransferRequest request) {

        // 1. Cargar cuentas con bloqueo pesimista para evitar race conditions
        Account sender = accountRepository
            .findByUserIdWithLock(senderUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        Account receiver = accountRepository
            .findByAccountNumberWithLock(request.getReceiverAccountNumber())
            .orElseThrow(() -> new ResourceNotFoundException("Cuenta destino no encontrada"));

        BigDecimal amount = request.getAmount();

        // 2. Validaciones de negocio
        validateTransfer(sender, amount);

        // 3. Comprobar fraude ANTES de ejecutar
        fraudDetectionService.checkForSuspiciousActivity(sender, amount);

        // 4. Ejecutar transferencia
        sender.setBalance(sender.getBalance().subtract(amount));
        updateDailyLimit(sender, amount);
        receiver.setBalance(receiver.getBalance().add(amount));

        // 5. Guardar ambas cuentas
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // 6. Registrar transacción
        Transaction transaction = Transaction.builder()
                .senderAccount(sender)
                .receiverAccount(receiver)
                .amount(amount)
                .description(request.getDescription())
                .status(TransactionStatus.COMPLETED)
                .type(TransactionType.TRANSFER)
                .completedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        // 7. Notificar por email (FUERA de la transacción principal — ver nota)
        emailService.sendTransferNotification(sender.getUser(), receiver.getUser(), amount);

        return TransactionResponse.from(transaction);
    }

    private void validateTransfer(Account sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(sender.getBalance());
        }
        resetDailyLimitIfNeeded(sender);
        if (sender.getDailyTransferredAmount().add(amount)
                .compareTo(sender.getDailyTransferLimit()) > 0) {
            throw new DailyLimitExceededException(sender.getDailyTransferLimit());
        }
    }

    private void resetDailyLimitIfNeeded(Account account) {
        if (!LocalDate.now().equals(account.getLastTransferDate())) {
            account.setDailyTransferredAmount(BigDecimal.ZERO);
            account.setLastTransferDate(LocalDate.now());
        }
    }

    private void updateDailyLimit(Account account, BigDecimal amount) {
        account.setDailyTransferredAmount(
            account.getDailyTransferredAmount().add(amount)
        );
        account.setLastTransferDate(LocalDate.now());
    }
}
```

### Repository con bloqueo pesimista

```java
public interface AccountRepository extends JpaRepository<Account, UUID> {

    // LOCK pesimista — bloquea la fila en BD mientras se procesa
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    Optional<Account> findByUserIdWithLock(@Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);
}
```

> **Nota sobre el email:** Los emails no deben enviarse dentro de la transacción principal porque si el servidor de correo falla, se haría rollback de la transferencia. Usa `@Async` o envía el email justo después de confirmar la transacción.

---

## 9. Tests

### Test unitario — FraudDetectionServiceTest.java
```java
@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @Test
    @DisplayName("Debe lanzar excepción si hay más de 5 transferencias en 1 hora")
    void shouldThrowExceptionWhenTooManyTransactionsInOneHour() {
        // Arrange
        Account sender = buildAccount();
        when(transactionRepository.countByAccountInLastHour(any(), any()))
            .thenReturn(6L);

        // Act & Assert
        assertThrows(SuspiciousActivityException.class,
            () -> fraudDetectionService.checkForSuspiciousActivity(sender, BigDecimal.TEN));
    }
}
```

### Test de integración con Testcontainers
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TransactionIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("finwallet_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Transferencia exitosa actualiza saldo de ambas cuentas")
    void successfulTransferUpdatesBothBalances() {
        // Setup: crear dos usuarios y hacer login
        // ...
        // Arrange: cuenta origen con 500€, transferir 100€
        // Act: POST /api/v1/transfers
        // Assert: origen tiene 400€, destino tiene 100€
    }

    @Test
    @DisplayName("Transferencia falla si saldo insuficiente y no modifica ningún saldo")
    void failedTransferDoesNotModifyAnyBalance() {
        // Demuestra atomicidad de la transacción
    }
}
```

---

## 10. Docker y Despliegue

### docker-compose.yml
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: finwallet
      POSTGRES_USER: finwallet
      POSTGRES_PASSWORD: finwallet
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U finwallet"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/finwallet
      DB_USERNAME: finwallet
      DB_PASSWORD: finwallet
      JWT_SECRET: tu-secreto-super-seguro-de-al-menos-256-bits
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  postgres_data:
```

### Dockerfile
```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 11. CI/CD con GitHub Actions

### .github/workflows/ci.yml
```yaml
name: CI Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: finwallet_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Run tests
        run: mvn test
        env:
          DB_URL: jdbc:postgresql://localhost:5432/finwallet_test
          DB_USERNAME: test
          DB_PASSWORD: test
          JWT_SECRET: test-secret-key-for-ci-pipeline-only

      - name: Build
        run: mvn package -DskipTests
```

---

## 12. README Profesional

El README es la primera impresión. Escríbelo en inglés.

```markdown
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
git clone https://github.com/your-username/finwallet-api
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
[See full endpoint reference →](docs/API.md)

## Live Demo
[https://finwallet-api.railway.app](https://finwallet-api.railway.app)
```

---

## 13. Cómo Defenderlo en Entrevista

Estas son las preguntas más comunes y cómo responderlas:

---

**"¿Por qué usaste `@Transactional`?"**
> "Para garantizar atomicidad en las transferencias. Si falla cualquier paso — el débito, el crédito, o la escritura en el historial — toda la operación hace rollback. Sin esto, podría debitarse dinero de un usuario sin que llegue al destinatario."

---

**"¿Qué pasa si dos usuarios transfieren desde la misma cuenta a la vez?"**
> "Uso bloqueo pesimista con `@Lock(LockModeType.PESSIMISTIC_WRITE)` en la consulta de la cuenta. La segunda operación queda en espera hasta que la primera termina. Consideré optimistic locking con `@Version`, pero para transferencias prefiero la consistencia garantizada del pesimista."

---

**"¿Por qué Flyway en vez de `ddl-auto: create`?"**
> "En producción no puedes dejar que Hibernate recree el schema. Flyway da control total sobre los cambios, permite rollback controlado y el historial de migraciones queda versionado en Git junto al código."

---

**"¿Cómo testeas sin base de datos real?"**
> "Uso Testcontainers que levanta un contenedor real de PostgreSQL durante los tests. Es más fiable que H2 en memoria porque el comportamiento es idéntico a producción, incluyendo tipos de datos, índices y el comportamiento de transacciones."

---

**"¿Qué mejorarías si lo llevaras a producción?"**
> "Rate limiting en la API con Bucket4j, circuit breaker para el servicio de email, métricas con Actuator + Prometheus, y posiblemente mover la detección de fraude a un servicio asíncrono con eventos para no bloquear la transferencia."

---

> 💡 **Tip final:** Despliega la API antes de la entrevista. Poder decir "aquí está funcionando en vivo" vale más que cualquier explicación.

---

*Guía creada como referencia de mentor — proyecto FinWallet API Portfolio*
*Stack: Java 21 + Spring Boot 3 + PostgreSQL + Docker*
```
