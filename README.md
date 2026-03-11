# 🧩 Job Portal System

A **backend-focused Job Portal System** built using **Spring Boot and Spring Security (JWT)** that enables recruiters to publish job openings and candidates to apply with resumes.

This project demonstrates **secure REST API design, role-based access control, file handling, and layered backend architecture**, similar to what backend engineers implement in real-world job platforms.

---

# 📌 System Overview

The platform supports **three types of users**:

| Role | Description |
|-----|-------------|
| Candidate | Search jobs and submit applications |
| Recruiter | Create and manage job postings |
| Admin | Manage users and system roles |

### Core Capabilities

- Secure login using **JWT Authentication**
- Job posting and management
- Job application workflow
- Resume upload and download
- Keyword-based job search
- Role-based access control

---

# 🏗 Architecture Overview

The system follows a **layered backend architecture**, commonly used in Spring Boot applications.

Client  
↓  
Controllers (REST APIs)  
↓  
Service Layer (Business Logic)  
↓  
Repository Layer (Data Access)  
↓  
MySQL Database

---

## 📊 Architecture Diagram

            +-------------------+
            |      Client       |
            | (Postman / UI)    |
            +---------+---------+
                      |
                      v
            +-------------------+
            |  REST Controllers |
            |   Spring Boot     |
            +---------+---------+
                      |
                      v
            +-------------------+
            |   Service Layer   |
            |  Business Logic   |
            +---------+---------+
                      |
                      v
            +-------------------+
            | Repository Layer  |
            | Spring Data JPA   |
            +---------+---------+
                      |
                      v
            +-------------------+
            |     MySQL DB      |
            +-------------------+


           
---

# 🚀 Key Features

## 🔐 Secure Authentication

Authentication is implemented using **Spring Security with JWT tokens**.

### Authentication Flow

1. User logs in  
2. Server validates credentials  
3. JWT token is generated  
4. Client sends token with every request  

Header example:

Authorization: Bearer <JWT_TOKEN>


This allows **stateless and secure API communication**.

---

## 👥 Role-Based Access Control

The system supports three roles:

- Candidate
- Recruiter
- Admin

Authorization ensures only permitted users can perform certain actions.

| Role | Allowed Actions |
|-----|----------------|
| Candidate | Apply to jobs |
| Recruiter | Create & manage jobs |
| Admin | Manage users and roles |

---

## 💼 Job Management

Recruiters can manage job postings through REST APIs.

Features include:

- Create job listings
- Update job details
- Delete job postings
- View job information

---

## 📄 Job Applications

Candidates can apply for jobs and track applications.

Application records contain:

- User information
- Job reference
- Application status
- Resume file reference

Recruiters can update application status.

---

## 📂 Resume File Upload

Candidates can upload resumes during job applications.

Features:

- Resume upload
- Resume download
- File storage using Spring FileSystem

This demonstrates **handling multipart file uploads in Spring Boot APIs**.

---

## 🔎 Job Search

Jobs can be searched using keyword-based search.

Example endpoint:

GET /api/jobs/search/{keyword}


---

# 🧰 Tech Stack

| Category | Technology |
|--------|-------------|
| Backend | Java 17, Spring Boot 3 |
| Persistence | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT |
| Database | MySQL |
| API Documentation | Swagger / OpenAPI |
| Build Tool | Maven |
| API Testing | Postman |
| Version Control | Git & GitHub |

---

# 📁 Project Structure

Job-Portal-System
│
├── controllers # REST API endpoints
├── service # Business logic layer
├── repository # Database access layer
├── entities # JPA entities
├── dto # Data transfer objects
├── security # JWT utilities and filters
├── configurations # Spring Security configuration
│
├── application.properties
├── data.sql
├── pom.xml
└── README.md


---

# 📡 API Endpoints Overview

## Authentication

| Method | Endpoint | Description |
|------|-----------|-------------|
| POST | /api/auth/login | User login and JWT generation |
| POST | /api/users/register | Register new user |

---

## User Management

| Method | Endpoint | Description |
|------|-----------|-------------|
| GET | /api/users/me | Get current user |
| PATCH | /api/users/{id}/roles | Update user roles (Admin only) |
| DELETE | /api/users/{id} | Delete user |

---

## Job Management

| Method | Endpoint | Description |
|------|-----------|-------------|
| POST | /api/jobs | Create job |
| PUT | /api/jobs/{id} | Update job |
| GET | /api/jobs/{id} | Get job by ID |
| GET | /api/jobs/search/{keyword} | Search jobs |
| DELETE | /api/jobs/{id} | Delete job |

---

## Applications

| Method | Endpoint | Description |
|------|-----------|-------------|
| POST | /api/applications | Apply for job |
| PUT | /api/applications/{id} | Update application status |
| GET | /api/applications/user/{id} | Applications by user |
| GET | /api/applications/job/{id} | Applications for job |
| DELETE | /api/applications/{id} | Delete application |

---

## Resume Management

Upload Resume

POST /api/jobs/upload-resume

Form Data

file: <resume file>

Download Resume

GET /api/jobs/resumes/{filename}


---

# ⚙️ Running the Project Locally

### Clone Repository

git clone https://github.com/NikStack20/Job-Portal-System.git

cd Job-Portal-System


---

### Setup Database

Create MySQL database:

CREATE DATABASE jobportal;

Update credentials in **application.properties**

spring.datasource.url=jdbc:mysql://localhost:3306/jobportal
spring.datasource.username=root
spring.datasource.password=yourpassword

JWT configuration

app.jwtSecret=yourSecretKey
app.jwtExpirationMs=86400000

---

### Run Application

mvn spring-boot:run

Application will start at
http://localhost:8099


---

# 🧪 API Testing

APIs can be tested using **Postman**.

Steps:

1. Login to obtain JWT token
2. Add token in request header

Authorization: Bearer <JWT_TOKEN>

---

# 📸 Demo

Project demo screenshots and test results are available in:

/demo

This folder contains:

- Postman API tests
- Entity screenshots
- API execution examples

---

# 🔮 Future Improvements

- Integrate React frontend
- Add email notifications
- Dockerize the application
- Deploy to AWS or cloud platform
- Add pagination and advanced search filtering

---

# 👨‍💻 Author

**Nikhil Chauhan**

B.Tech Student | Backend Developer

Email  
codingnik20@gmail.com

GitHub  
https://github.com/NikStack20

END OF README.md


