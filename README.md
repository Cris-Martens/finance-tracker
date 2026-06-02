# Finance Tracker API

A production-ready backend for a finance tracker application. It is build with modern practices and modern tech stack.
This RESTful API allows users to track their monthly spending, set budgets for different categories and see how much
they save each month based on their income.

The purpose of this project is to demonstrate backend development expertise including:

- REST API design
- Authentication & authorization
- Database architecture
- Secure coding practices
- Scalable backend systems
- Financial data management
- Testing & deployment workflows

---

# Table of Contents

- [Project Overview](#project-overview)
- [Core Features](#core-features)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Application Flow](#application-flow)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Example API Request](#example-api-request)
- [Example API Response](#example-api-response)
- [Error Response Example](#error-response-example)
- [Validation & Error Handling](#validation--error-handling)
- [Security](#security)
- [Performance Optimizations](#performance-optimizations)
- [Testing](#testing)
- [Docker Support](#docker-support)
- [Installation & Setup](#installation--setup)
- [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
- [CI/CD](#cicd)
- [Future Improvements](#future-improvements)
- [Learning Outcomes](#learning-outcomes)
- [License](#license)

---

# Project Overview

This Finance Tracker API is a backend service that helps users monitor their monthly spending and find out how much
they actually save each month.

Users can:

- Track expenses;
- Set and manage budgets for different categories;
- See how much money they have left for the month;
- Compare spending habits for different months 
- Analyse spending habits

---

# Core Features

## Authentication & Authorization

- User registration
- User login
- JWT authentication
- Protected routes
- Role-based access control
- Secure password hashing with bcrypt

---

## Expenses Management

- Create new expenses
- Read all expenses
- Read single expense
- Update expenses
- Delete expenses

---

## Budget Management

- Monthly budgets per category
- Insights on spending compared to budget

---

## Financial Analytics

- Income vs expenses
- Category spending analysis

---

## Security Features

- JWT authentication
- Password hashing
- Environment variable protection
- Request validation

---

# Tech Stack

### Backend

- Java 25
- Spring Boot 4.0.5

### Database

- MySQL 8
- Spring Data JPA

### Authentication

- JWT (JSON Web Tokens)
- bcrypt

### Testing

- JUnit
- Mockito

### DevOps & Tooling

- Docker
- GitHub Actions
- Postman

---

# System Architecture

The project follows a layered architecture pattern.

```bash
src/
│
├── main/
│    ├── java/
│    │    ├── auth/
│    │    ├── controller/
│    │    ├── dto/
│    │    ├── exceptions/
│    │    ├── model/
│    │    ├── repository/
│    │    ├── service/
│    │    └── FinanceTrackerApplication.java
│    └──  resources/
│        └── keys
└── tests/
     ├── java/
     │    ├── integrationtests/
     │    ├── security
     │    └── uninttesting
     └── resources
          ├── application-test.yaml
          ├── data.sql
          └── schema.sal
```

---

# Application Flow

```text
Client Request
      ↓
Routes
      ↓
Controllers
      ↓
Services
      ↓
Repositories
      ↓
Database
      ↓
API Response
```

---

# Database Design

## Main Entities

### app_users

Stores user authentication information.

### Expenses

Stores user expense records.

### category

Used to organize transactions.

### budget

Stores user set budgets for different categories.

### account_info
Stores additional account information for the users.
.

---

# Entity Relationships

```text
app_users
 ├── expenses
 ├── account_info
 └── budget

expenses
 └── category
 
budget
 └── category
```

---

# API Endpoints

### Authentication Routes

| Method | Endpoint         | Description           |
|---|------------------|-----------------------|
| POST | `/api/v1/register` | Register user         |
| POST | `/api/v1/auth/login` | Login user            |
| GET | `/api/v1/delete` | Delete logged in user |

---

### Expenses Routes

| Method | Endpoint                | Description |
|---|-------------------------|---|
| POST | `/api/v1/expenses`      | Create transaction |
| GET | `/api/v1/expenses`      | Get all transactions |
| GET | `/api/v1/expenses/{id}` | Get transaction by ID |
| PUT | `/api/v1/expenses/`     | Update transaction |
| DELETE | `/api/v1/expenses/`     | Delete transaction |

---

### Budget Routes

| Method | Endpoint                    | Description |
|---|-----------------------------|---|
| POST | `/api/v1/budget`            | Create budget |
| GET | `/api/v1/budget`            | Get budgets |
| PUT | `/api/v1/budget`            | Update budget |
| DELETE | `/api/v1/budget/{category}` | Delete budget |

---

### Account info Routes

| Method | Endpoint              | Description        |
|---|-----------------------|--------------------|
| POST | `/api/v1/accountinfo` | Create accountinfo |
| GET | `/api/v1/accountinfo` | Get accountinfo    |
| PUT | `/api/v1/accountinfo` | Update accountinfo |
| DELETE | `/api/v1/accountinfo` | Delete accountinfo |

---

### Dashboard Routes

| Method | Endpoint                                 | Description                                                       |
|--------|------------------------------------------|-------------------------------------------------------------------|
| GET    | `/api/v1/dashboard/latest-expenses`      | Get five most recent expenses                                     |
| GET    | `/api/v1/dashboard/expenses-by-month`    | Get total spend by month                                          |
| GET    | `/api/v1/dashboard/category-budget-left` | Get get five smallest differences spend this month and set budget |
| GET    | `/api/v1/dashboard/totalsaved`           | Get differenct income and spend this month                        |

---

# Authentication

Authentication is handled using JWT tokens.

Protected routes require:

```http
Authorization: Bearer <token>
```

Passwords are securely hashed using bcrypt before storage.

---

# Example API Request

## Create Expense

```http
POST /api/v1/expenses
Authorization: Bearer <token>
Content-Type: application/json
```

### Request Body

```json
{
  "expense_date": "2026-04-23",
  "category": {
    "id": "1",
    "name": "Housing"
  },
  "amount": "650",
  "description": "Rent"
}
```

---

# Example API Response
Status code 201

```json
{
  "amount": 650.0,
  "category": {
    "id": 1,
    "name": "Housing"
  },
  "description": "Rent",
  "expense_date": "2026-04-23",
  "id": 1
}
```

---

# Error Response Example

```json
{
  "detail": "category with name nonexistent not found",
  "instance": "/api/v1/user/expenses",
  "status": 404,
  "title": "Category Not Found"
}
```

---

# Validation & Error Handling

This project implements:

- Request validation
- Centralized error middleware
- Standardized API responses
- Standardised error handling
- HTTP status consistency

---

# Security

The API includes several security best practices:

- JWT token verification
- Password hashing
- Protected routes
- Environment variable protection
- SQL injection protection

---

# Performance Optimizations

Performance strategies include:

- Query optimization
- Efficient relational queries
- Optimized response payloads

---

# Testing

Testing includes:

- Unit tests
- Authentication tests
- API endpoint tests

## Run Tests

```bash
./mvnw test
```

---

# Docker Support

This project includes Docker support for running the backend API in a containerised environment.

In this project it includes/
- Custom Docker image for the Spring Boot Backend
- Multi-stage setup with Docker Compose
- MySQL 8 database container for development

## Build Docker Image

```bash
docker build -t finance-tracker .
```

## Run Docker Container

```bash
docker run -p 8080:8080 finance-tracker
```

---

# Installation & Setup

## With Docker

### 1. Clone Repository

```bash
git clone https://github.com/Cris-Martens/finance-tracker.git
```

---

### 2. Navigate Into Project

```bash
cd /path-to-folder/finance-tracker
```

---

### 4. Configure Environment Variables

Create a `.env` file in the root directory.

#### Environment Variables
```env
# SQL
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_PASSWORD=your_app_password

# Oauth2.0
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

### 5. Run Docker compose
```bash
docker compose up --build
```

---

## Local Development

### 1. Clone Repository
```bash
git clone https://github.com/Cris-Martens/finance-tracker.git
```
---

### 2. Navigate to Project
```bash
cd /path-to-folder/finance-tracker
```
---

### 3. Install Requirements
For MacOS
```bash
brew install openjdk@25
brew install maven
brew instal mysql
brew services start mysql
```
For Windows
```powershell
winget install EclipseAdoptium.Temurin.25.JDK
winget install Apache.Maven
winget install Oracle.MySQL
```

---

### 4. Configure Environment Variables
Create a `.env` file in the root directory.

#### Environment Variables
```env
# SQL
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_PASSWORD=your_app_password

# Oauth2.0
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

---

### 5. Start application
For MacOS
```bash
./mvnw spring-boot:run
```
For Windows
```pawershell
mvnw.cmd spring-boot:run
```
---

# API Documentation

TODO

---

# CI/CD

CI/CD workflows:

- Automated testing
- Docker image builds

Tooling:

- GitHub Actions

---

# Future Improvements

Potential future features:

- proper testcontainers
- OAuth authentication
- AI financial insights
- CSV/PDF exports
- Front end design
- Recurring transactions
- Notification system
- Graph dashboards

---

# Learning Outcomes

This project demonstrates knowledge of:

- REST API development
- Backend architecture
- Authentication systems
- Database schema design
- Secure coding practices
- Secure data handling
- Error handling
- Testing strategies
- Deployment workflows
- Production-ready backend engineering

---

# License
 
This project is licensed under the MIT License.

---

# Author

## Cris Martens

Backend Developer

### Contact

- LinkedIn: https://linkedin.com/in/crismartens111
- Portfolio: https://github.com/cris-martens
- Email: martens1cris@gmail.com

---

# Recruiter Notes

This project was built to demonstrate backend engineering capabilities beyond basic CRUD operations.

The focus areas include:

- Scalability
- Secure authentication
- Database design
- API structure
- Production-level coding practices
- Maintainability
- Testing & deployment pipelines

This project reflects real-world backend development patterns used in scalable applications.