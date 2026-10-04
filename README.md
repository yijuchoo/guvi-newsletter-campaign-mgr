# Newsletter & Email Campaign Manager API

A secure backend REST API built with Spring Boot for managing mailing lists,
subscribers, and email campaigns with JWT authentication.

## Tech Stack
- Java 17
- Spring Boot 4.x
- Spring Security + JWT
- PostgreSQL (hosted on Neon) + Spring Data JPA + Hibernate
- Swagger / OpenAPI

## Features
- JWT-based user authentication (register/login)
- Mailing list management (create, rename, delete)
- Subscriber management (add/remove per list, duplicate email prevention)
- Campaign management (create, edit, schedule, reschedule)
- Simulated email sending via scheduled logs
- Paginated and filterable campaign queries
- Global exception handling with consistent error responses

## Getting Started

### Prerequisites
- Java 17
- Maven
- A free [Neon](https://neon.tech) account (serverless PostgreSQL)

### Setup

1. Clone the repository
```bash
   git clone https://github.com/yijuchoo/guvi-newsletter-campaign-mgr.git
   cd guvi-newsletter-campaign-mgr
```

2. Create a Neon database
   - Create a project in the [Neon Console](https://console.neon.tech). A database (`neondb`) is created by default.
   - Click **Connect** and copy the connection details (host, database, username, password).

3. Configure properties
```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Fill in your Neon credentials and JWT secret. Neon shows a `postgresql://...` URI, but Spring Boot needs the JDBC format, with the username and password set separately:
```properties
   spring.datasource.url=jdbc:postgresql://<your-neon-host>/neondb?sslmode=require
   spring.datasource.username=<your-username>
   spring.datasource.password=<your-password>
```
> Never commit `application.properties`. It contains live database credentials.

4. Run the application
```bash
   mvn spring-boot:run
```

## Deployment
The live API is deployed on Render and connects to a Neon PostgreSQL database.
Database credentials and the JWT secret are supplied via Render environment variables, not committed to the repo.

**Note:** Neon suspends idle databases, and Render's free tier sleeps idle services,
so the first request after inactivity may take some time to respond.

## API Documentation
Once running, visit: http://localhost:8080/swagger-ui.html

Swagger Public: https://guvi-newsletter-campaign-mgr.onrender.com/swagger-ui.html

Postman API: https://documenter.getpostman.com/view/30937434/2sBXqCP41g

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login, returns JWT |

### Mailing Lists
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/mailing-lists | Create mailing list |
| GET | /api/mailing-lists | Get all mailing lists |
| GET | /api/mailing-lists/{id} | Get mailing list by id |
| PUT | /api/mailing-lists/{id} | Rename mailing list |
| DELETE | /api/mailing-lists/{id} | Delete mailing list |

### Subscribers
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/mailing-lists/{id}/subscribers | Add subscriber |
| GET | /api/mailing-lists/{id}/subscribers | Get all subscribers |
| DELETE | /api/mailing-lists/{id}/subscribers/{subId} | Remove subscriber |

### Campaigns
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/campaigns | Create campaign |
| GET | /api/campaigns | Get all campaigns (paginated) |
| GET | /api/campaigns/{id} | Get campaign by id |
| PUT | /api/campaigns/{id} | Update campaign |
| POST | /api/campaigns/{id}/schedule | Schedule campaign |
| POST | /api/campaigns/{id}/reschedule | Reschedule campaign |
| DELETE | /api/campaigns/{id} | Delete campaign |