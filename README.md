# 💪 Pulse

> Track workouts. Log progress. Achieve goals.

A comprehensive fitness tracking platform built with **Spring Boot** that helps you monitor your daily workouts, track exercise performance, and visualize your fitness progress over time.

---

## ✨ Features

- 🏋️ **Workout Tracking** – Log and manage your training sessions
- 📚 **Exercise Database** – Comprehensive library of exercises with detailed information
- 📊 **Progress Monitoring** – Track your performance metrics over time
- 🔐 **User Authentication** – Secure JWT-based authentication system
- 👤 **User Profiles** – Personalized workout and progress management

---

## 🛠️ Tech Stack

| Layer              | Technology                                                     |
| ------------------ |----------------------------------------------------------------|
| **Backend**        | Spring Boot 4.0.2, Spring Security, Spring Data JPA, Spring MVC |
| **Database**       | PostgreSQL, Hibernate, JPA                                     |
| **Authentication** | JWT (JJWT 0.12.6)                                              |
| **Validation**     | Spring Validation                                              |
| **Language**       | Java 21                                                        |
| **Build Tool**     | Maven                                                          |
| **Containerization** | Docker, Docker Compose                                       |
| **Utils**          | Lombok                                                         |

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker + Docker Compose (for containerized run)

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd pulse
   ```

2. **`.env` configuration**

   ```env
   DB_NAME=training
   DB_USER=postgres
   DB_PASSWORD=postgres
   ```

   `.env` is included in this repository and is committed with the project.

3. **Build the project**

   ```bash
   ./mvnw clean install
   ```

4. **Run the application (local)**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8080`

### Run with Docker

1. **Use existing `.env` from the repository** (see values above).
2. **Build and start services**

   ```bash
   docker compose up --build -d
   ```

3. **Check logs**

   ```bash
   docker compose logs -f backend
   ```

4. **Stop services**

   ```bash
   docker compose down
   ```

Backend is available at `http://localhost:8080`, PostgreSQL runs in the `db` container with data persisted in Docker volume `db_data`.

---

## 📁 Project Structure

```
pulse/
├── src/main/java/com/example/pulse/
│   ├── config/              # Security & application configuration
│   ├── controller/          # REST API endpoints
│   ├── entity/              # JPA entities & DTOs
│   ├── repository/          # Data access layer
│   ├── security/            # JWT authentication & filters
│   ├── service/             # Business logic
│   └── util/                # Utility classes
├── src/resources/
│   └── application.properties
├── README.md                # This file
└── HELP.md                  # Additional documentation & help
```

---

## 📖 Documentation

- **[README.md](README.md)** – Project overview and quick start guide
- **[HELP.md](HELP.md)** – Detailed setup instructions and troubleshooting

---

## 🔌 API Endpoints

The application provides REST endpoints for:

- 🔑 **Authentication** – Login and user registration
- 🏋️ **Workouts** – Create, read, update, and delete workouts
- 💪 **Exercises** – Browse and manage exercise database
- 📈 **Progress** – Track and analyze fitness progress

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork this project and submit pull requests for any improvements.
