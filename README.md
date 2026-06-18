# User Management Application

A full-stack **User Management** system composed of a **Spring Boot REST API** backend and an **Angular** frontend. The application supports complete CRUD operations on user data with JWT-based authentication.

---

## Repository Structure

```
UserManagementSpringBootApp/
├── backend/      # Spring Boot REST API (Java 21 + MongoDB)
├── frontend/     # Angular 21 frontend
└── e2e/          # End-to-end tests (Playwright)
```

---

## Technology Stack

### Backend (`backend/`)

| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 3.2.5 | REST API framework |
| Java | 21 | Runtime |
| MongoDB | latest | NoSQL data persistence |
| Spring Security | — | Authentication & authorization |
| JWT | — | Stateless token auth |
| Maven | 3.8+ | Build & dependency management |
| Docker / Docker Compose | — | Containerization |

### Frontend (`frontend/`)

| Technology | Version | Purpose |
|---|---|---|
| Angular | 21 | SPA framework |
| Node / npm | npm ≥ 11.10.0 | Package management |
| Angular CLI | — | Build & dev tooling |

### E2E Tests (`e2e/`)

| Technology | Version | Purpose |
|---|---|---|
| Playwright | ^1.52.0 | End-to-end testing framework |
| Node / npm | npm ≥ 11.10.0 | Package management |

---

## Features

- **User Management** — Create, Read, Update, and Delete user records
- **JWT Authentication** — Secure, stateless token-based auth
- **MongoDB Integration** — Spring Data MongoDB persistence
- **Automatic Data Seeding** — Sample users created on backend startup
- **RESTful API** — Clean REST endpoints consumed by the Angular frontend
- **Docker Support** — Easy containerized deployment for the backend + database
- **E2E Testing** — Playwright-based end-to-end tests covering all user CRUD operations

---

## Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.8.0** or higher
- **Node.js** with **npm ≥ 11.10.0**
- **Docker** and **Docker Compose** (recommended for MongoDB)

---

### 1. Start the Backend

#### Option A — Docker (Recommended)

```bash
cd backend/docker
docker-compose up -d        # Start MongoDB
cd ..
./mvnw spring-boot:run      # Start the Spring Boot app
```

#### Option B — Local MongoDB

```bash
# Ensure MongoDB is running locally on port 27017
cd backend
./mvnw spring-boot:run
```

#### Option C — Build & Run JAR

```bash
cd backend
./mvnw clean package
java -jar target/SpringBootRestApiExample-1.0.0.jar
```

The API will be available at **`http://localhost:8080`**.

---

### 2. Start the Frontend

```bash
cd frontend
npm install
npm start
```

The app will be available at **`http://localhost:4200`**.  
It communicates with the backend at `http://127.0.0.1:8080/SpringBootRestApi/api/user/` by default.

> Make sure the backend is running before launching the frontend.

---

## Backend API Reference

All endpoints (except `/login`) require a valid JWT token in the `Authorization` header.

### Authentication

```bash
# Get a JWT token
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"Marcos","password":"password"}'

# Response: {"token":"eyJhbGci...","type":"Bearer","expiresIn":864000000}
```

Store the token for subsequent requests:

```bash
TOKEN="Bearer <your-token-here>"
# Or extract automatically with jq:
TOKEN="Bearer $(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"Marcos","password":"password"}' | jq -r '.token')"
```

### User Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/user/` | List all users |
| `GET` | `/api/user/{id}` | Get user by ID |
| `POST` | `/api/user/` | Create a new user |
| `PUT` | `/api/user/{id}` | Update a user |
| `DELETE` | `/api/user/{id}` | Delete a user |

#### Get All Users
```bash
curl -X GET http://localhost:8080/api/user/ \
  -H "Authorization: $TOKEN"
```

#### Create User
```bash
curl -X POST http://localhost:8080/api/user/ \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{"name":"John Doe","age":30,"salary":50000.0,"password":"secret123"}'
```

#### Update User
```bash
curl -X PUT http://localhost:8080/api/user/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{"name":"John Updated","age":31,"salary":55000.0,"password":"newsecret"}'
```

#### Delete User
```bash
curl -X DELETE http://localhost:8080/api/user/{id} \
  -H "Authorization: $TOKEN"
```

---

## Sample Data

On backend startup, the following seed users are automatically created:

| Name | Age | Salary |
|---|---|---|
| Marcos | 23 | 30,000 |
| Gerardo | 17 | 1,000 |

---

## Docker Commands (Backend)

```bash
# Start MongoDB
cd backend/docker && docker-compose up -d

# Stop and clean up
docker-compose down -v

# View logs
docker-compose logs -f
```

---

## Backend Configuration

Default MongoDB settings (configurable in `application.properties`):

- **Host**: `localhost`
- **Port**: `27017`
- **Database**: auto-created by Spring Data MongoDB

---

## Build & Test

### Backend

```bash
cd backend
./mvnw clean package    # Build JAR
./mvnw test             # Run tests
```

### Frontend

```bash
cd frontend
npm run build           # Production build → dist/
```

### E2E Tests

```bash
cd e2e
npm install             # Install dependencies
npx playwright install  # Install browsers (first time only)

# Start backend + frontend first, then:
npm test                # Run all tests headlessly
npm run test:headed     # Run tests with visible browser
npm run test:debug      # Run tests in debug mode
npm run test:ui         # Open Playwright UI mode
```

The e2e tests require both the **backend** (API on port 8080) and **frontend** (dev server on port 4200) to be running.

#### E2E Test Specs

**Authentication** (`tests/auth.spec.ts`)
- Login with valid credentials
- Login with invalid credentials
- Redirect to login when unauthenticated
- Logout

**User CRUD** (`tests/users-crud.spec.ts`)
- Read: view seed users in table, view user details
- Create: single user, multiple users
- Update: edit user, partial update
- Delete: remove a user
- Full CRUD workflow (create → view → update → delete)

---

## Security

- JWT stateless authentication
- Password encryption via Spring Security
- Protected endpoints (all routes except `/login`)
- Custom JWT authentication & login filters
- Role-based access control ready

