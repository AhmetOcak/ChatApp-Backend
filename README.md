# ChatApp-Backend

This repository contains the backend implementation for a real-time chat application using Ktor, a Kotlin-based server framework. The application provides APIs for user authentication, messaging, and managing chat rooms.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Installation](#installation)
- [Running the Project](#running-the-project)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Features

- User registration and login with JWT-based authentication
- Real-time messaging between users
- Chat rooms for group discussions
- Persistence using a database
- RESTful API design for seamless integration

## Tech Stack

- **Kotlin**: Programming language used for the server.
- **Ktor**: Backend framework used to create the server.
- **Exposed**: ORM framework for database interactions.
- **PostgreSQL**: Database used for storing user and chat data.
- **JWT (JSON Web Tokens)**: For user authentication.
- **WebSockets**: For real-time messaging between users.
- **Gradle**: Build system.

## Installation

### Prerequisites

Before you begin, ensure you have met the following requirements:

- Kotlin 1.8+
- Gradle 7.0+
- PostgreSQL database setup and running
- A Java Development Kit (JDK) installed, preferably JDK 11+

### Clone the Repository

```
git clone https://github.com/AhmetOcak/ChatApp-Backend.git
cd ChatApp-Backend
```

### Configure Database

Update the application.conf file to configure the database connection. Provide your own PostgreSQL credentials and database URL:

```
ktor {
    deployment {
        port = 8080
        port = ${?PORT}
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
    database {
        url = "jdbc:postgresql://localhost:5432/chatapp"
        driver = org.postgresql.Driver
        user = "yourusername"
        password = "yourpassword"
    }
}
```

### Build the Project

To build the project, use the following Gradle command:

```
./gradlew build
```

### Database Migration

If you're using Exposed or any migration tool, ensure the database schema is up-to-date.

```
./gradlew runMigrations
```

### Running the Project

To run the Ktor server locally, use the following command:

```
./gradlew run
```

The server will be running on http://localhost:8080.

### Running in Docker (Optional)

You can also use Docker to containerize and run the application.

```
docker build -t chatapp-backend .
docker run -p 8080:8080 chatapp-backend
```

### API Endpoints

| Method | Endpoint               | Description                                     |
|--------|------------------------|-------------------------------------------------|
| POST   | `/register`            | Register a new user                             |
| POST   | `/login`               | Authenticate a user and return JWT              |
| GET    | `/users`               | Get a list of all users                         |
| GET    | `/users/{id}`          | Get user details by ID                          |
| PUT    | `/users/{id}`          | Update user information                          |
| DELETE | `/users/{id}`          | Delete a user account                           |
| GET    | `/chats`               | Get a list of all chat rooms                    |
| POST   | `/chats`               | Create a new chat room                          |
| GET    | `/chats/{id}`          | Get chat room details by ID                     |
| PUT    | `/chats/{id}`          | Update a chat room                              |
| DELETE | `/chats/{id}`          | Delete a chat room                              |
| POST   | `/chats/{id}/message`  | Send a message to a specific chat room          |
| GET    | `/chats/{id}/messages` | Get all messages from a specific chat room      |
| GET    | `/ws/chats`            | Establish WebSocket connection for messaging    |
| GET    | `/ws/chats/{id}`       | WebSocket connection for a specific chat room   |

### Contributing

Contributions are welcome! Please open an issue or submit a pull request for any changes or enhancements.

1. Fork the repository.
2. Create your feature branch (git checkout -b feature/feature-name).
3. Commit your changes (git commit -m 'Add feature').
4. Push to the branch (git push origin feature/feature-name).
5. Open a pull request.

### License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/AhmetOcak/ChatApp-Backend?tab=MIT-1-ov-file). file for details
