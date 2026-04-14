# Newsletter & Email Campaign Manager API

A secure backend REST API built with Spring Boot for managing mailing lists,
subscribers, and email campaigns with JWT authentication.

## Tech Stack
- Java 17
- Spring Boot 4.x
- Spring Security + JWT
- MySQL + Spring Data JPA + Hibernate
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
- MySQL

### Setup

1. Clone the repository
   git clone https://github.com/yijuchoo/guvi-newsletter-campaign-mgr.git
   cd newsletter-campaign-mgr

2. Create the database
   CREATE DATABASE newsletter_db;

3. Configure properties
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   Then edit application.properties with your MySQL credentials and JWT secret.

4. Run the application
   mvn spring-boot:run

## API Documentation
Once running, visit: http://localhost:8080/swagger-ui.html

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