🧩 Job Portal System

A complete Spring Boot–based Job Portal System, designed to connect employers and applicants on one platform.
The system allows employers to post jobs, and users to apply, upload resumes, and track application status — all secured using Spring Security + JWT.

🚀 Features

👤 User Roles – Candidate, Recruiter, and Admin

🔐 JWT Authentication – Secure login using token-based access

📄 Job Management – Employers can create, update, and delete jobs

💼 Applications – Users can apply for jobs and upload resumes

🔎 Search & Filter – Find jobs easily with keyword-based search

🧰 File Uploads – Resume file storage handled by Spring FileSystem

🧑‍💻 Admin Access – Manage users and roles efficiently

🧾 Swagger API Docs – Auto-generated API documentation

⚙️ Tech Stack

Backend: Java 17, Spring Boot 3, Spring Data JPA, Hibernate

Security: Spring Security, JWT Authentication

Database: MySQL

Build Tool: Maven

Testing Tool: Postman

IDE: Spring Tool Suite (STS)

Version Control: Git & GitHub

🧠 Project Structure
Job-Portal-System/
│
├── src/main/java/com/ncst/job/portal/
│   ├── controllers/         # REST Controllers (User, Job, Application, Auth)
│   ├── entities/            # JPA Entities
│   ├── dto/                 # Data Transfer Objects
│   ├── repository/          # Repositories for DB access
│   ├── service/             # Services & Implementations
│   ├── configurations/      # Spring Security & JWT Config
│   └── security/            # JWT Token Helper, Filter, EntryPoint
│
├── src/main/resources/
│   ├── application.properties
│   └── data.sql             # Optional default roles seeding
│
├── pom.xml
├── README.md
└── runlog.txt (for logs)

🧾 API Endpoints Overview
🔐 Authentication
Method	Endpoint	Description
POST	/api/auth/login	User login (returns JWT)
POST	/api/users/register	Register new user
👤 User Management
Method	Endpoint	Description
GET	/api/users/me	Get current user details
PATCH	/api/users/{id}/roles	Update user roles (Admin only)
DELETE	/api/users/{id}	Delete a user
💼 Job Management
Method	Endpoint	Description
POST	/api/jobs	Create a new job
PUT	/api/jobs/{id}	Update existing job
GET	/api/jobs/{id}	View job by ID
GET	/api/jobs/search/{keyword}	Search jobs
DELETE	/api/jobs/{id}	Delete job (Admin/Employer)
📄 Applications
Method	Endpoint	Description
POST	/api/applications	Apply for a job
PUT	/api/applications/{id}	Update application status
GET	/api/applications/user/{id}	View applications by user
GET	/api/applications/job/{id}	View all applications for a job
DELETE	/api/applications/{id}	Delete an application
📂 File Uploads

Resume Upload:

POST /api/jobs/upload-resume
FormData → file: <choose file>


Resume Download:

GET /api/jobs/resumes/{filename}

⚡ How to Run Locally

Clone the Repository

git clone https://github.com/NikStack20/Job-Portal-System.git
cd Job-Portal-System
git checkout feature


Setup Database

Create a MySQL database:

CREATE DATABASE jobportal;


Update your credentials in application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/jobportal
spring.datasource.username=root
spring.datasource.password=yourpassword
app.jwtSecret=<Paste your Base64 JWT key>
app.jwtExpirationMs=86400000


Run the Application

mvn spring-boot:run


App will start on http://localhost:8099

Test APIs
Use Postman for testing.
Login → Get JWT token → Add it to headers as:

Authorization: Bearer <token>

🎥 Demo Folder

You can find demo screenshots and short clips of project testing under:
📁 /demo

This folder contains Postman test visuals, entity screenshots, and running API examples.

🧩 Future Enhancements

Integrate frontend using React or Thymeleaf

Add email notifications on job application updates

Deploy on Docker + AWS for cloud access

👨‍💻 Author

Nikhil Chauhan
🎓 B.Tech Student | Backend Developer
📧 chauhannikil20062005@gmail.com

🔗 GitHub – NikStack20