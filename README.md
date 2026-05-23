# Spring Boot REST API – E-commerce System (Backend)

REST API built with Spring Boot and JPA simulating an e-commerce backend system, including users, products, categories, orders and payments.

## Technologies
- Java 17
- Spring Boot 3
- Spring Data JPA (Hibernate)
- H2 Database (test profile)
- Bean Validation
- Maven

## Architecture
- RESTful API
- Layered architecture (Controller / Service / Repository)
- DTO pattern for data transfer
- Global exception handling (@ControllerAdvice)

## Features
- Full CRUD for Users, Products, Categories and Orders
- Order management with items and status control
- Product-category many-to-many relationship
- Input validation with Bean Validation
- Duplicate data protection (email and product/category name)
- Centralized exception handling
- In-memory database for testing (H2)

## Business Rules
- Each user can place multiple orders
- Each order contains multiple products with quantity and price snapshot
- Orders have status tracking (WAITING_PAYMENT, PAID, SHIPPED, DELIVERED, CANCELED)

## How to run
1. Clone the repository
2. Open in IntelliJ / Eclipse
3. Run `DemoApplication`
4. Access API at:
   - http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
