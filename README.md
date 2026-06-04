# Spring Boot REST API – E-commerce Backend
REST API built with Spring Boot and JPA simulating an e-commerce backend system, including users, products, categories, orders, and payments.

---

## Technologies
- Java 17
- Spring Boot 3
- Spring Data JPA (Hibernate)
- Lombok
- H2 Database
- Bean Validation
- Maven

---

## Architecture
- RESTful API
- Layered architecture (**Controller / Service / Repository**)
- DTO pattern for data transfer
- Global exception handling with `@ControllerAdvice`
- Database Projections (Interface-based projections)

---

## Features
- Full CRUD for Users, Products, Categories, and Orders
- Order creation with multiple items and stock control
- Order status management with custom business workflow
- Order items include subtotal calculation (price × quantity)
- Order includes total price calculation
- Product–Category many-to-many relationship
- Input validation with Bean Validation
- Duplicate data protection (email and product/category name)
- Centralized exception handling
- Pagination support on all list endpoints using `@PageableDefault`
- Partial update support (send only the fields you want to update)
- Business rule: finalized orders (CANCELED/REFUNDED) cannot have status updated
- Automated stock restore when canceling or refunding orders
- Automated payment lifecycle creation and clean-up
- In-memory H2 database for testing

---

## Business Rules
- Each user can place multiple orders
- Each order can contain multiple products
- Each order item stores quantity and product price snapshot at purchase time
- Orders support status tracking:
  - `WAITING_PAYMENT`
  - `PAID`
  - `SHIPPED`
  - `DELIVERED`
  - `CANCELED`
  - `REFUNDED`
- Finalized orders (CANCELED/REFUNDED) cannot have their status updated
- Delivered orders (DELIVERED) can only be updated to `REFUNDED`
- Paid orders (PAID) can only be updated to `SHIPPED` or `REFUNDED`
- Shipped orders (SHIPPED) can only be updated to `DELIVERED` or `REFUNDED`
- Orders can only be deleted if their current status is `WAITING_PAYMENT`

---

## How to Run

- Clone the repository
- Open the project in IntelliJ IDEA or Eclipse
- Run `DemoApplication`

Access API at:

- `http://localhost:8080`
- H2 Console: http://localhost:8080/h2-console

---

## API Endpoints

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /users | List all users (paginated) |
| GET | /users/{id} | Find user by id |
| POST | /users | Create user |
| PUT | /users/{id} | Update user (partial) |
| DELETE | /users/{id} | Delete user |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /products | List all products (paginated) |
| GET | /products/{id} | Find product by id |
| POST | /products | Create product |
| PUT | /products/{id} | Update product (partial) |
| DELETE | /products/{id} | Delete product |
| GET | /products/category | Find products by category id or name (paginated) |
| GET | /products/search | Search products by name or id using minified projection (paginated) |

### Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /categories | List all categories (paginated) |
| GET | /categories/{id} | Find category by id |
| POST | /categories | Create category |
| PUT | /categories/{id} | Update category (partial) |
| DELETE | /categories/{id} | Delete category |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /orders | List all orders (paginated) |
| GET | /orders/{id} | Find order by id |
| POST | /orders | Create order with items (and automatic stock deduction) |
| PUT | /orders/{id}/status | Update order status |
| DELETE | /orders/{id} | Delete order (Only if WAITING_PAYMENT) |
| GET | /orders/products-sales | Custom sales report using database projections matching products and clients (pagined) |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /payments | List all payments (paginated) |
| GET | /payments/{id} | Find payment by id |