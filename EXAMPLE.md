# TaskFlow — Team Productivity Dashboard

> A full-stack productivity dashboard that helps small teams manage tasks, deadlines, and project progress in real time.

![Node.js](https://img.shields.io/badge/Backend-Java/SpringBoot-green)
![PostgreSQL](https://img.shields.io/badge/Database-MySQL-blue)

---

# Overview

TaskFlow is a collaborative task management application designed for small development teams. Users can create projects, assign tasks, track deadlines, and visualize progress through interactive dashboards.

This project was built to strengthen my understanding of:

- Full-stack application architecture
- REST API development
- Authentication and authorization
- State management in React
- Database relationships and query optimization

---

# Features

## User Features

- User registration and login
- JWT-based authentication
- Create and manage projects
- Task assignment system
- Drag-and-drop Kanban board
- Deadline reminders
- Responsive mobile-friendly design

## Technical Features

- RESTful API architecture
- Protected routes and middleware
- Role-based access control
- PostgreSQL relational database
- Form validation
- Error handling and logging
- Unit and integration testing

---

# Tech Stack

## Backend

- Java
- Spring Boot
- MySQL
- JWT Authentication

## Testing

- JUnit
- Mockito
- TestContainers

## DevOps / Tools

- Docker
- GitHub Actions
- ESLint
- Prettier

---

# Project Structure

```bash
taskflow/
├── client/                 # Frontend application
│   ├── src/
│   ├── components/
│   ├── pages/
│   └── services/
│
├── server/                 # Backend API
│   ├── controllers/
│   ├── routes/
│   ├── middleware/
│   └── prisma/
│
├── docs/                   # Documentation & screenshots
├── tests/                  # Automated tests
└── README.md
```

---

# Installation & Setup

## Prerequisites

Make sure you have installed:

- Node.js (v20+)
- PostgreSQL
- Git

---

## 1. Clone the repository

```bash
git clone https://github.com/johndoe/taskflow.git
cd taskflow
```

---

## 2. Install dependencies

### Docker
**docker-compose.yml**

Services:
- Spring-boot application
- MySQL

Start everything:
```bash
docker-compose up --build
```
Stop everything:
```bash
docker-compose down
```
### Frontend

```bash
cd client
npm install
```

### Backend

```bash
cd ../server
npm install
```

---

## 3. Configure environment variables

Create a `.env` file inside `/server`.

```env
DATABASE_URL="postgresql://postgres:password@localhost:5432/taskflow"
JWT_SECRET="super-secret-key"
PORT=5000
```

---

## 4. Run database migrations

```bash
npx prisma migrate dev
```

---

## 5. Start the development servers

### Backend

```bash
npm run dev
```

### Frontend

```bash
cd ../client
npm run dev
```

---

# 🧪 Running Tests

## Backend Tests

```bash
npm run test
```

## Frontend Tests

```bash
npm run test:frontend
```

---

# 📸 Screenshots

## Dashboard View

_Add screenshot here_

## Kanban Board

_Add screenshot here_

## Mobile Responsive Layout

_Add screenshot here_

---

# 🧠 Challenges & What I Learned

One of the biggest challenges was implementing real-time task updates while keeping the frontend state synchronized efficiently.

To solve this:

- I normalized Redux state
- Reduced unnecessary API calls
- Implemented optimistic UI updates

This project improved my understanding of:

- Scalable frontend architecture
- API security best practices
- Database schema design
- Writing maintainable code

---

# Future Improvements

Planned future features include:

- Real-time collaboration with WebSockets
- File uploads and attachments
- Email notifications
- Dark mode
- Performance monitoring dashboard
- CI/CD deployment pipeline

---

# Deployment

| Service | Link |
|---|---|
| Frontend | https://taskflow-demo.vercel.app |
| Backend API | https://taskflow-api.onrender.com |

---

# API Documentation

## Example Endpoint

```http
POST /api/tasks
```

### Request Body

```json
{
  "title": "Finish README",
  "priority": "high",
  "assignedTo": "user_123"
}
```

### Example Response

```json
{
  "id": "task_001",
  "title": "Finish README",
  "priority": "high",
  "status": "in-progress"
}
```

---

# About Me

Hi, I'm Cris Martens, a junior Java Developer passionate about building scalable web applications and business solutions.

## Interests

- Full-stack development
- Backend systems
- UI/UX design
- Open source

## Currently Learning

- System design
- Cloud infrastructure
- Kubernetes

---

# Contact

- Email: martens1cris&gmail.com
- LinkedIn: https://linkedin.com/in/crismartens111

---

# Contributing

Contributions, suggestions, and feedback are welcome.

1. Fork the repository
2. Create a feature branch

```bash
git checkout -b feature/new-feature
```

3. Commit your changes

```bash
git commit -m "Add new feature"
```

4. Push to the branch

```bash
git push origin feature/new-feature
```

5. Open a Pull Request
