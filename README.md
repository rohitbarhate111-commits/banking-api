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
mvn test           # run test suite
mvn clean package  # build JAR in target/
```

---

## Architecture & Implementation Notes

- `@Transactional` on the transfer operation ensures both account updates are atomic — if the receiver credit or persistence fails, the sender debit rolls back automatically.
- Custom domain exceptions (`InsufficientFundsException` -> 422, `ResourceNotFoundException` -> 404) keep HTTP concerns separated from business rules.
- Bean validation (`@NotBlank`, `@Min`) guards against invalid inputs at controller ingress before reaching domain logic.
- Uses H2 in-memory DB by default for instant local setup. Can be switched to PostgreSQL or MySQL via `src/main/resources/application.properties`.

---

## License

MIT © 2026 Rohit Barhate