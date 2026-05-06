# SkillSwipe

A Spring Boot REST API for a peer-to-peer skill exchange platform. Users can list skills they want to teach or learn, send exchange requests, chat with each other, and leave reviews.

---

## TODO

### Topics to Learn / Research

- [ ] **JWT Authentication** — Secure API endpoints with JSON Web Tokens (login, registration, token refresh)
- [ ] **Recommendation Algorithm/Model** — Suggest skill matches based on user preferences, location, experience level, etc.
- [ ] **Real-time Chat System** — Implement WebSocket-based messaging (Spring WebSocket / STOMP) instead of REST polling
- [ ] **Email Notifications** — Notify users on new requests, messages, and reviews
- [ ] **Add remaining services and controllers** — Complete any missing CRUD operations and endpoints

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.14**
- **Spring Data JPA**
- **Lombok**
- **Maven**

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- A relational database (MySQL/PostgreSQL) configured in `application.properties`

### Run

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080` (or your configured port).

---

## API Endpoints

### User Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/update` | Update user (partial update supported) |
| GET | `/api/users/search?name=` | Search users by name |

### Skill Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/skills` | Get all skills |
| POST | `/api/skills` | Create a new skill |
| POST | `/api/users/skills` | Add a skill to a user |
| DELETE | `/api/users/skills/{id}` | Remove a skill from a user |

### Exchange Request Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/requests` | Create a new exchange request |
| GET | `/api/requests/sent?userId=` | Get requests sent by a user |
| GET | `/api/requests/received?userId=` | Get requests received by a user |
| PUT | `/api/requests/{id}/accept` | Accept a pending request |
| PUT | `/api/requests/{id}/reject` | Reject a pending request |
| PUT | `/api/requests/{id}/complete` | Mark an accepted request as completed |

### Review Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/reviews` | Create a review (self-review not allowed) |
| GET | `/api/reviews/user/{id}` | Get all reviews for a user |

### Message Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/messages` | Send a message |
| GET | `/api/messages/conversation?userId1=&userId2=` | Get conversation between two users |
| GET | `/api/messages/unread?userId=` | Get unread messages for a user |

### Dashboard Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard?userId=` | Get user dashboard (skills, active requests, sessions, avg rating) |

---

## Data Models

### Users
| Field | Type |
|-------|------|
| userId | int (PK) |
| userName | String |
| userEmail | String (unique) |
| userPassword | String |
| userBio | String |
| userRole | String |

### Skills
| Field | Type |
|-------|------|
| skillId | int (PK) |
| skillName | String (unique) |

### UsersSkills
| Field | Type |
|-------|------|
| id | int (PK) |
| user | FK → Users |
| skill | FK → Skills |
| type | TEACH / LEARN |
| experienceLevel | BEGINNER / INTERMEDIATE / ADVANCED |

### ExchangeRequests
| Field | Type |
|-------|------|
| id | int (PK) |
| sender | FK → Users |
| receiver | FK → Users |
| offeredSkill | FK → Skills |
| requestedSkill | FK → Skills |
| message | String |
| status | PENDING / ACCEPTED / REJECTED / COMPLETED |
| createdAt | LocalDateTime |

### Message
| Field | Type |
|-------|------|
| id | int (PK) |
| sender | FK → Users |
| receiver | FK → Users |
| content | TEXT |
| timestamp | LocalDateTime |
| seen | boolean |

### Reviews
| Field | Type |
|-------|------|
| id | int (PK) |
| reviewer | FK → Users |
| reviewedUser | FK → Users |
| rating | int |
| comment | TEXT |
| createdAt | LocalDateTime |

---

## Project Structure

```
src/main/java/com/backend/SkillSwipe/
├── SkillSwipeApplication.java
├── controller/
│   ├── UserController.java
│   ├── SkillController.java
│   ├── ExchangeRequestController.java
│   ├── ReviewController.java
│   ├── MessageController.java
│   └── DashboardController.java
├── service/
│   ├── UserService.java
│   ├── SkillService.java
│   ├── ExchangeRequestService.java
│   ├── ReviewService.java
│   ├── MessageService.java
│   └── DashboardService.java
├── repository/
│   ├── UserRepo.java
│   ├── SkillRepo.java
│   ├── UserSkillsRepo.java
│   ├── ExchangeRequestsRepo.java
│   ├── ReviewsRepo.java
│   └── MessageRepo.java
└── model/
    ├── Users.java
    ├── Skills.java
    ├── UsersSkills.java
    ├── ExchangeRequests.java
    ├── Reviews.java
    ├── Message.java
    └── DashboardResponse.java
```

---

## Business Rules

- **Exchange Request State Machine:** `PENDING` → `ACCEPTED` or `REJECTED`; `ACCEPTED` → `COMPLETED`
- **Reviews:** A user cannot review themselves
- **User Update:** Supports partial updates (null fields are ignored)
- **Dashboard:** Shows only active requests (PENDING/ACCEPTED), completed sessions, and average rating
