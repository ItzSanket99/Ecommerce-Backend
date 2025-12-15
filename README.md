# 📦 Ecommerce Backend – Spring Boot | JWT | PostgreSQL

Backend service for a full-stack **E-commerce Application** built using **Spring Boot**, **Spring Security (JWT)**, and **PostgreSQL**.  
This backend provides secure authentication, product management APIs, user roles, and scalable database architecture.

## 🧾 Project Overview

### 🚀 E-Shop – Spring Boot Backend 

This backend serves as the core service for an enterprise-style e-commerce platform, supporting:

- 🔐 **JWT-based Authentication**  
- 👑 **Role-Based Access (Admin / Seller / Customer)**  
- 🏪 **Seller product management**  
- 🧾 **Admin dashboard for system-wide control**  
- 💳 **Stripe Payment Integration**  
- 📦 **Order & inventory handling**  
- 🗂️ **Database relationships with JPA/Hibernate** 

It follows clean coding practices, layered architecture, and production-ready configurations.

## 🛠️ Tech Stack
- Spring Boot 3+
- Spring Security (JWT Authentication)
- Spring Data JPA / Hibernate
- PostgreSQL 
- Maven

## 📡 Deployment
Deployed using [Render](https://render.com/)
Dockerized container is live and integrated with Netlify frontend

## 📂 Folder Structure

```
src/main/java/com.ecommerce.project
│
├── config/                # App-level configuration
├── controller/            # REST controllers
├── exceptions/            # Global exception handling
├── model/                 # Entities (User, Role, Product, Category...)
├── payload/               # Request & Response payloads
├── repositories/          # Spring Data JPA Repositories
│
├── security/
│   ├── jwt/               # Token provider, token filter, entry point
│   ├── request/           # Login & Signup request DTOs
│   ├── response/          # JWT response, user info response
│   ├── services/          # UserDetailsServiceImpl, Password encoder
│   ├── WebConfig          # CORS configuration
│   └── WebSecurityConfig  # Spring Security configuration
│
├── service/               # Business logic (Product, User, Category services)
├── util/                  # Utility classes
└── EcommerceApplication   # Main Spring Boot application
```
---

## 🌐 API Endpoints (Sample)

### 🔑 **Auth Endpoints**
| Method |      Endpoint       |         Description       |
|--------|---------------------|---------------------------|
| POST   | `/api/auth/signup`  | Register a new user       |
| POST   | `/api/auth/login`   | Login & receive JWT token |

### 🛍️ **Product Endpoints**
| Method |        Endpoint        |           Description       |
|--------|------------------------|-----------------------------|
| GET    | `/api/public/products` | Get all products            |
| POST   | `/api/admin/products`  | Create product (Admin only) |

### 📁 **Category Endpoints**
| Method |         Endpoint         |        Description   |
|--------|--------------------------|----------------------|
| GET    | `/api/public/categories` | Fetch all categories |

---

## ⚙️ Installation & Setup

### **1️⃣ Clone the Repository**
```bash
git clone https://github.com/ItzSanket99/Ecommerce-Backend.git
cd Ecommerce-Backend
```

### **2️⃣ Configure PostgreSQL**
**Create Database**
`CREATE DATABASE ecommerce;`

**Update application.properties**
```
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### **3️⃣ Stripe Configuration**
**Add these to application.properties:**
`stripe.secret.key=YOUR_STRIPE_SECRET_KEY`

### **4️⃣ Run the Backend**
`mvn spring-boot:run`
