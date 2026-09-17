# Banking API

A REST API for banking operations built with Java 17 and Spring Boot. Covers account creation, listing, and fund transfers with validation and structured error responses.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Persistence | Spring Data JPA + Hibernate |
| Validation | Bean Validation (jakarta.validation) |
| Database | H2 (in-memory, development) |
| Code Generation | Lombok |
| Build Tool | Apache Maven 3.9 |
| Testing | JUnit 5, Mockito, Spring Test (MockMvc) |

---

## Project Structure

```
src/main/java/com/rohit/banking/
├── BankingApiApplication.java
├── controller/
│   ├── AccountController.java   # POST/GET /api/accounts, POST /api/accounts/transfer
│   └── HelloController.java     # GET / - health check
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientFundsException.java
│   └── ResourceNotFoundException.java
├── model/
│   └── Account.java             # id, accountHolderName, balance
├── repository/
│   └── AccountRepository.java
└── service/
    └── AccountService.java
```

---

## API Reference

### Health Check

```http
GET /
```
Response:
```
200 OK
"Banking API is running"
```

### Accounts

#### Create Account
```http
POST /api/accounts
Content-Type: application/json

{
  "accountHolderName": "Rohit Barhate",
  "balance": 1000.0
}
```

Responses:
- `201 Created` — `{ "id": 1, "accountHolderName": "Rohit Barhate", "balance": 1000.0 }`
- `400 Bad Request` — `{ "accountHolderName": "Account holder name is required" }`
- `400 Bad Request` — `{ "balance": "Balance cannot be negative" }`

#### List All Accounts
```http
GET /api/accounts
```

Response:
- `200 OK` — `[ { "id": 1, "accountHolderName": "Rohit Barhate", "balance": 1000.0 } ]`

#### Transfer Funds
```http
POST /api/accounts/transfer?fromId=1&toId=2&amount=250.0
```

Responses:
- `200 OK` — `"Transfer successful"`
- `400 Bad Request` — `{ "error": "Transfer amount must be greater than zero" }`
- `400 Bad Request` — `{ "error": "Sender and receiver accounts cannot be the same" }`
- `404 Not Found` — `{ "error": "Sender account not found: 1" }`
- `404 Not Found` — `{ "error": "Receiver account not found: 2" }`
- `422 Unprocessable Entity` — `{ "error": "Insufficient balance: available 100.0, requested 250.0" }`

---

## Running Locally

```bash
# Requires Java 17+ and Maven 3.6+
git clone https://github.com/rohitbarhate111-commits/banking-api.git
cd banking-api
mvn spring-boot:run
```

Server starts at `http://localhost:8080`.

H2 console is available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:bankingdb`, username: `sa`, no password).

```bash
mvn test           # run test suite (16 unit and MockMvc integration tests)
mvn clean package  # build JAR in target/
```

---

## Architecture & Implementation Notes

- **Transactional Consistency**: `@Transactional` on the transfer operation ensures atomic operations — if debit or credit encounters any failure, the entire transaction rolls back automatically.
- **Layered Validation**:
  - Request ingress validation via `@Valid` and Bean Validation (`@NotBlank`, `@Min`) ensures malformed request payloads are rejected before reaching business logic.
  - Domain validation ensures non-positive transfers and self-transfers are rejected with clear error feedback.
- **Structured Error Handling**: `GlobalExceptionHandler` maps domain exceptions to appropriate HTTP status codes:
  - `IllegalArgumentException` / Validation errors -> `400 Bad Request`
  - `ResourceNotFoundException` -> `404 Not Found`
  - `InsufficientFundsException` -> `422 Unprocessable Entity`
  - Unhandled errors -> `500 Internal Server Error`

---

## License

MIT © 2026 Rohit Barhate