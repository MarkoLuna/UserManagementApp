# Spring Boot MongoDB BackEnd

## Purpose

The primary purpose of this application is for **User Management**. It provides a complete backend solution for managing user data, including secure authentication and full CRUD (Create, Read, Update, Delete) operations.

## Overview

This project demonstrates a complete backend solution for user management, featuring secure authentication using JSON Web Tokens (JWT) and full CRUD operations. The application automatically initializes with sample data on startup and provides a robust foundation for building scalable REST APIs.

## Technology Stack

- **Spring Boot 3.2.5** - Main framework for building REST APIs
- **Java 21** - Latest Java version with modern features
- **MongoDB** - NoSQL database for data persistence
- **Spring Security** - Authentication and authorization framework
- **JWT (JSON Web Tokens)** - Stateless authentication mechanism
- **Maven** - Build and dependency management
- **Jackson** - JSON/XML processing
- **Docker & Docker Compose** - Containerization and deployment

## Project Structure

```
src/main/java/com/springboot/
├── SpringBootRestApiApp.java          # Main application class
├── controller/
│   └── RestApiController.java         # REST API endpoints
├── model/
│   └── User.java                      # User entity model
├── repositories/
│   └── UserRepository.java            # MongoDB repository
├── security/
│   ├── AccountCredentials.java        # Authentication credentials
│   ├── CustomUserService.java         # Custom user service
│   ├── JWTAuthenticationFilter.java   # JWT authentication filter
│   ├── JWTLoginFilter.java            # JWT login filter
│   ├── TokenAuthenticationService.java # JWT token service
│   └── WebSecurityConfig.java         # Security configuration
└── util/
    └── CustomErrorType.java           # Custom error handling
```

## Features

- **User Management**: Complete CRUD operations for user entities
- **JWT Authentication**: Secure token-based authentication
- **MongoDB Integration**: NoSQL database with Spring Data MongoDB
- **Automatic Data Seeding**: Sample data initialization on startup
- **RESTful API**: Clean and well-structured REST endpoints
- **Security Configuration**: Comprehensive security setup with Spring Security
- **Docker Support**: Easy containerization and deployment

## Data Model

The `User` entity includes:
- **id**: MongoDB document ID (auto-generated)
- **name**: User name (String)
- **age**: User age (Integer)
- **salary**: User salary (Double)
- **password**: User password (String)

## API Endpoints

The application provides REST endpoints for:
- User registration and authentication
- CRUD operations on user data
- JWT token management
- Secure resource access

## Requirements

- **Java 21** or higher
- **Maven 3.8.0** or higher
- **MongoDB** (or use provided Docker setup)
- **Docker** and **Docker Compose** (for containerized deployment)

## API Testing with curl

The application runs on `http://localhost:8080` by default. All endpoints except `/login` require JWT authentication.

### 1. Authentication - Get JWT Token

First, authenticate with existing user credentials to get a JWT token:

```bash
# Login with sample user credentials
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"Marcos","password":"password"}'

# Response: Returns JWT token in JSON response body
# Example: {"token":"eyJhbGciOiJIUzUxMiJ9...","type":"Bearer","expiresIn":864000000}
```

### 2. Store JWT Token for Subsequent Requests

```bash
# Extract and store the token from response body (replace with actual token)
TOKEN="Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYXJjb3MiLCJleHAiOjE3NDQ1NjQ0MDB9.abc123def456"

# Alternative: Extract token using jq
TOKEN=$(curl -s -X POST http://localhost:8080/login -H "Content-Type: application/json" -d '{"username":"Marcos","password":"password"}' | jq -r '.token')
TOKEN="Bearer $TOKEN"
```

### 3. CRUD Operations

#### Get All Users
```bash
curl -X GET http://localhost:8080/api/user/ \
  -H "Authorization: $TOKEN"
```

#### Get User by ID
```bash
curl -X GET http://localhost:8080/api/user/{user-id} \
  -H "Authorization: $TOKEN"
```

#### Create New User
```bash
curl -X POST http://localhost:8080/api/user/ \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "name": "John Doe",
    "age": 30,
    "salary": 50000.0,
    "password": "newpassword123"
  }'
```

#### Update User
```bash
curl -X PUT http://localhost:8080/api/user/{user-id} \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "name": "John Updated",
    "age": 31,
    "salary": 55000.0,
    "password": "updatedpassword123"
  }'
```

#### Delete User
```bash
curl -X DELETE http://localhost:8080/api/user/{user-id} \
  -H "Authorization: $TOKEN"
```

### 4. Complete Testing Workflow

```bash
#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080"

echo "=== 1. Authentication ==="
# Login and get token
TOKEN_RESPONSE=$(curl -s -X POST $BASE_URL/login \
  -H "Content-Type: application/json" \
  -d '{"username":"Marcos","password":"password"}')

echo "Login response: $TOKEN_RESPONSE"

# Extract token (you may need to adjust based on actual response format)
TOKEN="Bearer $(echo $TOKEN_RESPONSE | grep -o '"[^"]*"' | tail -1 | tr -d '"')"
echo "Token: $TOKEN"

echo -e "\n=== 2. Get All Users ==="
curl -s -X GET $BASE_URL/api/user/ \
  -H "Authorization: $TOKEN" | jq .

echo -e "\n=== 3. Create New User ==="
CREATE_RESPONSE=$(curl -s -X POST $BASE_URL/api/user/ \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "name": "Alice Smith",
    "age": 28,
    "salary": 45000.0,
    "password": "alicepass123"
  }')

echo "Create response: $CREATE_RESPONSE"

# Extract new user ID
USER_ID=$(echo $CREATE_RESPONSE | jq -r '.id // empty')
echo "New user ID: $USER_ID"

if [ ! -z "$USER_ID" ]; then
    echo -e "\n=== 4. Get User by ID ==="
    curl -s -X GET $BASE_URL/api/user/$USER_ID \
      -H "Authorization: $TOKEN" | jq .

    echo -e "\n=== 5. Update User ==="
    curl -s -X PUT $BASE_URL/api/user/$USER_ID \
      -H "Content-Type: application/json" \
      -H "Authorization: $TOKEN" \
      -d '{
        "name": "Alice Updated",
        "age": 29,
        "salary": 47000.0,
        "password": "alicepass456"
      }' | jq .

    echo -e "\n=== 6. Delete User ==="
    curl -s -X DELETE $BASE_URL/api/user/$USER_ID \
      -H "Authorization: $TOKEN"
fi

echo -e "\n=== 7. Verify Deletion ==="
curl -s -X GET $BASE_URL/api/user/ \
  -H "Authorization: $TOKEN" | jq .
```

### 5. Error Handling Examples

#### Invalid Credentials
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"invalid","password":"wrong"}'
# Returns: 401 Unauthorized
```

#### Missing Token
```bash
curl -X GET http://localhost:8080/api/user/
# Returns: 401 Unauthorized
```

#### Invalid Token
```bash
curl -X GET http://localhost:8080/api/user/ \
  -H "Authorization: Bearer invalid-token"
# Returns: 401 Unauthorized
```

#### User Not Found
```bash
curl -X GET http://localhost:8080/api/user/507f1f77bcf86cd799439011 \
  -H "Authorization: $TOKEN"
# Returns: 404 Not Found
```

### 6. Tips for Testing

1. **Use jq for JSON formatting**: Install `jq` for pretty JSON output
2. **Save tokens temporarily**: Store tokens in environment variables for testing sessions
3. **Check HTTP status codes**: Use `-v` flag with curl to see response headers
4. **Test with Postman**: Import the examples into Postman for easier testing
5. **Sample data**: The app creates two users automatically: "Marcos" and "Gerardo"

### 7. Postman Collection

You can import these examples into Postman:

1. Create a new collection
2. Add environment variable `{{baseUrl}}` = `http://localhost:8080`
3. Add environment variable `{{token}}` 
4. Set up the following requests:
   - **Login**: POST `{{baseUrl}}/login`
   - **Get Users**: GET `{{baseUrl}}/api/user/` (Auth: Bearer {{token}})
   - **Create User**: POST `{{baseUrl}}/api/user/` (Auth: Bearer {{token}})
   - **Get User**: GET `{{baseUrl}}/api/user/{{userId}}` (Auth: Bearer {{token}})
   - **Update User**: PUT `{{baseUrl}}/api/user/{{userId}}` (Auth: Bearer {{token}})
   - **Delete User**: DELETE `{{baseUrl}}/api/user/{{userId}}` (Auth: Bearer {{token}})

## Quick Start

### Option 1: Run with Docker (Recommended)

```bash
# Start MongoDB with Docker Compose
cd docker && docker-compose up -d

# Run the Spring Boot application
./mvnw spring-boot:run
```

### Option 2: Run Locally

```bash
# Make sure MongoDB is running locally
./mvnw spring-boot:run
```

### Option 3: Build and Run

```bash
# Build the application
./mvnw clean package

# Run the JAR file
java -jar target/SpringBootRestApiExample-1.0.0.jar
```

## Docker Commands

### Start Services
```bash
cd docker && docker-compose up -d
```

### Stop Services
```bash
cd docker && docker-compose down -v
```

### View Logs
```bash
cd docker && docker-compose logs -f
```

## Configuration

The application uses default MongoDB settings:
- **Host**: localhost
- **Port**: 27017
- **Database**: Automatically created by Spring Data MongoDB

## Sample Data

On application startup, the following sample users are automatically created:
- **Marcos** (Age: 23, Salary: 30000)
- **Gerardo** (Age: 17, Salary: 1000)

## Development

### Build
```bash
./mvnw clean package
```

### Test
```bash
./mvnw test
```

### Development Mode (with hot reload)
```bash
./mvnw spring-boot:run
```

## Security Features

- JWT-based stateless authentication
- Password encryption
- Secure endpoint protection
- Custom authentication filters
- Role-based access control ready

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is open source and available under the [MIT License](LICENSE).
