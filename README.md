# Authentication and Authorization Microservice

This project is a microservice designed for user authentication and authorization using JWT tokens. The project includes integration with PostgreSQL and is containerized with Docker for easy deployment.

## Features

- User registration and login
- JWT-based authentication and authorization
- Token refresh functionality
- PostgreSQL database integration
- Docker support

## Requirements

- Docker and Docker Compose

## Environment Variables

To configure the application, use the following environment variables:

### Application Environment Variables:

- `APP_PORT`: Port on which the application runs (default: `8080`)
- `DB_HOST`: Hostname for the PostgreSQL database (default: `localhost`)
- `DB_PORT`: Port for the PostgreSQL database (default: `5432`)
- `DB_NAME`: PostgreSQL database name (default: `auth_db`)
- `DB_USERNAME`: PostgreSQL username (default: `auth_user`)
- `DB_PASSWORD`: PostgreSQL password (default: `password`)
- `JWT_SALT`: Secret salt for JWT signing (default: `over-salted-soup`)
- `ACCESS_TOKEN_EXPIRATION`: Expiration time for access tokens in milliseconds (default: `86400000` - 1 day)
- `REFRESH_TOKEN_EXPIRATION`: Expiration time for refresh tokens in milliseconds (default: `2592000000` - 15 days)
- `PASSWORD_SALT`: Salt used for password hashing (default: `very-over-salted-soup`)

## Running the Application

You can start the application either using provided scripts or manually.

### 1. Using Scripts

- On Linux/macOS:
    ```bash
    ./start.sh
    ```

- On Windows:
    ```bash
    start.bat
    ```

### 2. Manual Setup

Alternatively, you can manually run the following commands to start the application:

```bash
gradlew clean
gradlew bootJar
docker-compose stop
docker-compose up --build -d
```

### Stopping the Application

To stop the application, run:

```bash
docker-compose stop
```