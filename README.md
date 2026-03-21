# 🏦 Banking API

A modern, scalable backend REST API for banking operations built with Spring Boot, featuring user management, account operations, and transaction processing.

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (Development)
- **MySQL** (Production-ready)
- **Lombok**
- **Maven**

## 📚 Features

- ✅ User registration and authentication
- ✅ Account management
- ✅ RESTful API architecture
- ✅ JPA/Hibernate ORM
- ✅ In-memory H2 database for development
- ✅ Lombok for clean code
- ✅ Proper project structure

## 📝 Project Structure

```
banking-api/
├── src/
│   ├── main/
│   │   ├── java/com/rohit/banking/
│   │   │   ├── BankingApiApplication.java
│   │   │   ├── controller/
│   │   │   │   └── HelloController.java
│   │   │   ├── model/
│   │   │   │   └── User.java
│   │   │   ├── service/
│   │   │   └── repository/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## 🔧 Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Clone the repository
```bash
git clone https://github.com/rohitbarhate111-commits/banking-api.git
cd banking-api
```

### Build the project
```bash
mvn clean install
```

### Run the application
```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## 🧪 Test the API

### Health Check
```bash
curl http://localhost:8080/api/health
```

### Hello Endpoint
```bash
curl http://localhost:8080/api/hello
```

Response:
```
Banking API is running 🚀
```

## 📊 H2 Database Console

Access the H2 console at: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:bankingdb`
- **Username**: `sa`
- **Password**: (leave blank)

## 🛣️ API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/hello` | Test endpoint |
| GET | `/api/health` | Health check |

*More endpoints coming soon...*

## 👨‍💻 Author

**Rohit Barhate**
- Email: rohitbarhate111@gmail.com
- LinkedIn: [rohit-barhate](https://www.linkedin.com/in/rohit-barhate)
- GitHub: [@rohitbarhate111-commits](https://github.com/rohitbarhate111-commits)

## 📝 License

This project is open source and available under the MIT License.
