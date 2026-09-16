# Week 7 — Spring Data JPA E-Commerce Backend

A comprehensive e-commerce backend built with **Spring Boot, Spring Data JPA, Hibernate, H2/PostgreSQL, Flyway, and REST APIs**.

This project focuses on database integration, JPA repositories, custom queries, transaction management, auditing, caching, pagination, query optimization, connection pooling, database seeding, validation, and automated testing.

## 📌 Project Overview

The application provides a layered REST API for an e-commerce system with support for:

- User management and authentication
- Product management
- Hierarchical categories
- Order processing
- Order items
- Payment management
- Daily order reporting
- Spring Data JPA repositories
- Custom JPQL queries
- Transaction management
- Pessimistic locking
- Flyway database migrations
- JPA auditing
- Product caching
- Pagination, filtering, and sorting
- Database indexes and fetch optimization
- HikariCP connection pooling
- Development database seeding
- Automated integration and repository testing
- Swagger/OpenAPI documentation

## 🎯 Objectives

- Integrate Spring Data JPA with a relational database.
- Implement reusable JPA repositories.
- Use derived queries and custom JPQL queries.
- Implement transaction management for order processing.
- Protect inventory updates using pessimistic locking.
- Manage database schema changes using Flyway.
- Implement Spring Data JPA auditing.
- Add caching for frequently accessed product data.
- Implement pagination, filtering, and sorting.
- Optimize database access using indexes and entity graphs.
- Configure HikariCP connection pooling.
- Implement validated development data seeding.
- Add automated repository and service integration tests.
- Provide interactive REST API documentation using Swagger/OpenAPI.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming Language |
| Spring Boot 4.0.8 | Backend Framework |
| Spring Data JPA | Database Access & Repository Layer |
| Hibernate | ORM / JPA Implementation |
| Spring Web MVC | REST API Development |
| H2 Database | Development & Testing |
| PostgreSQL | Production Database |
| Flyway | Database Migration |
| HikariCP | Database Connection Pooling |
| Spring Cache | Application Caching |
| BCrypt | Password Hashing |
| Jakarta Validation | Request Validation |
| Swagger / OpenAPI | API Documentation |
| Maven | Build & Dependency Management |
| JUnit | Automated Testing |
| Git & GitHub | Version Control |

## ✨ Features

### 👤 User Management

- User registration
- User login
- BCrypt password hashing
- Email validation
- Active/inactive user handling
- Customer and admin roles
- User profile
- User order history

### 📦 Product Management

- Create products
- Update products
- Delete products
- Retrieve products
- Search products by name
- Filter by category
- Filter by price range
- Active product filtering
- Pagination
- Sorting
- Product caching

### 🗂️ Category Management

- Create categories
- Update categories
- Delete categories
- Root categories
- Child categories
- Parent-child category hierarchy
- Category relationship validation
- Prevention of invalid category relationships
- Category database indexing

### 🛒 Order Processing

- Create orders
- Retrieve orders
- Retrieve all orders
- Retrieve orders for a specific user
- Update order status
- Cancel orders
- Automatic stock deduction
- Stock restoration after cancellation
- Transaction management
- Pessimistic locking for inventory operations

### 💳 Payment Management

- Create payments
- Retrieve payments
- Payment status management
- Order-payment relationship

### 📊 Reporting

- Daily order count
- Daily revenue calculation
- Date-based order reporting

### ⚡ Performance & Optimization

- Database indexes
- Lazy loading
- Entity graphs
- Custom JPQL queries
- Pagination
- Product caching
- HikariCP connection pooling
- Hibernate SQL logging
- JDBC parameter logging

## 🗄️ Database Design

The application uses the following primary tables:

```text
users
   │
   └── orders
          │
          ├── order_items ─── products
          │                       │
          │                       └── categories
          │
          └── payments
```

### Main Tables

- `users`
- `products`
- `categories`
- `orders`
- `order_items`
- `payments`

### Category Hierarchy

Categories support hierarchical relationships through:

```text
parent_category_id
```

Example:

```text
Electronics
   │
   ├── Laptops
   ├── Mobiles
   └── Accessories
```

## 🔄 Flyway Database Migrations

Flyway is used to manage and version database schema changes.

Current migrations:

```text
V1__create_initial_schema.sql
V2__add_category_parent.sql
```

### V1

Creates the initial e-commerce database schema, tables, constraints, and indexes.

### V2

Adds hierarchical category support through the `parent_category_id` column and its foreign-key relationship.

## 🏗️ Project Architecture

The project follows a layered architecture:

```text
com.ashutosh.week7_jpa
│
├── config
│   ├── CacheConfig.java
│   ├── DataSeeder.java
│   ├── JpaAuditConfig.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
│
├── controller
│   ├── CategoryController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   ├── ProductController.java
│   └── UserController.java
│
├── dto
│
├── entity
│
├── exception
│
├── repository
│
└── service
```

## 🔌 REST API Endpoints

### Users & Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate user |
| GET | `/api/users/profile` | Get user profile |
| GET | `/api/users/{userId}/orders` | Get user's orders |

### Products

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | List/search products |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

### Categories

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/categories` | Get all categories |
| GET | `/api/categories/root` | Get root categories |
| GET | `/api/categories/{id}` | Get category by ID |
| GET | `/api/categories/parent/{parentId}` | Get child categories |
| POST | `/api/categories` | Create category |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Create order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| PUT | `/api/orders/{id}/status` | Update order status |
| POST | `/api/orders/{id}/cancel` | Cancel order |
| GET | `/api/orders/report/daily` | Get daily order report |

### Payments

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments` | Create payment |
| GET | `/api/payments/{id}` | Get payment |

## 🔍 Product Search, Filtering & Pagination

The product API supports:

- Name filtering
- Category filtering
- Minimum price
- Maximum price
- Pagination
- Sorting

Example:

```text
GET /api/products?page=0&size=5&sortBy=price&direction=asc
```

## 🔐 Transaction Management

Order processing is implemented using transactional service methods.

The order workflow is:

```text
Validate User
      ↓
Validate Products
      ↓
Lock Products
      ↓
Check Stock
      ↓
Deduct Stock
      ↓
Calculate Total
      ↓
Create Order
      ↓
Commit Transaction
```

Pessimistic locking is used during stock-sensitive operations to help protect inventory updates during concurrent transactions.

## 📝 Spring Data JPA Auditing

Spring Data JPA auditing automatically maintains:

- `createdAt`
- `updatedAt`

A reusable `BaseAuditableEntity` is used by the relevant entities.

## ⚡ Caching

Spring Cache is used for product retrieval.

Caching operations include:

- `@Cacheable`
- `@CachePut`
- `@CacheEvict`

This helps reduce repeated database access for frequently requested product data.

## 🚀 HikariCP Connection Pooling

HikariCP is configured as the application's database connection pool.

The configuration includes:

- Maximum pool size
- Minimum idle connections
- Connection timeout
- Idle timeout
- Maximum connection lifetime

## 🧪 Testing

The project contains repository, service, caching, auditing, seeding, and integration tests.

Testing areas include:

- Application context
- Product repository
- Custom JPA queries
- Product pagination and filtering
- Order processing
- Payment processing
- Daily order reporting
- Product caching
- User registration
- User login
- Password hashing
- Data seeding
- JPA auditing

### Test Result

```text
Tests run: 24
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

## 📖 Swagger / OpenAPI

Interactive API documentation is available through Swagger UI.

When the application is running:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger provides documentation and testing support for REST endpoints, request bodies, parameters, and response schemas.

## ⚙️ Configuration

### Development Profile

The development environment uses an in-memory H2 database.

```properties
spring.profiles.active=dev
```

H2 Console:

```text
http://localhost:8080/h2-console
```

### Production Profile

The production configuration supports PostgreSQL using environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Flyway manages database migrations in the configured environment.

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Ashutosh9-pan/week7-spring-data-jpa.git
```

### 2. Navigate to the Project

```bash
cd week7-spring-data-jpa
```

### 3. Run Tests

Windows:

```powershell
.\mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

### 4. Start the Application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

### 5. Open Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

## 📸 Project Screenshots

### 01 — Project Structure
![Project Structure](screenshots/01_Project_Structure.png)

### 02 — Java Architecture
![Java Architecture](screenshots/02_Java_Architecture.png)

### 03 — Entity Layer
![Entity Layer](screenshots/03_Entity_Layer.png)

### 04 — Repository Layer
![Repository Layer](screenshots/04_Repository_Layer.png)

### 05 — Service Layer
![Service Layer](screenshots/05_Service_Layer.png)

### 06 — Controller Layer
![Controller Layer](screenshots/06_Controller_Layer.png)

### 07 — Database & Resources Configuration
![Database Configuration](screenshots/07_Database_Resources_Config.png)

### 08 — Test Structure
![Test Structure](screenshots/08_Test_Structure.png)

### 09 — Test Execution — 24/24
![Test Results](screenshots/09_Test_Execution_24_of_24.png)

### 10 — Swagger API Overview
![Swagger API Overview](screenshots/10_Swagger_API_Overview.png)

### 11 — Orders, Payments & Categories APIs
![Orders Payments Categories](screenshots/11_Orders_Payments_Categories.png)

### 12 — Users & Products APIs
![Users Products](screenshots/12_Users_Products.png)

### 13 — User Registration Request
![User Registration Request](screenshots/13_User_Registration_Request.png)

### 14 — User Registration Success — 201 Created
![User Registration Success](screenshots/14_User_Registration_Success_201.png)

### 15 — User Login Request
![User Login Request](screenshots/15_User_Login_Request.png)

### 16 — User Login Success — 200 OK
![User Login Success](screenshots/16_User_Login_Success_200.png)

### 17 — User Response Schema
![User Response Schema](screenshots/17_User_Response_Schema.png)

### 18 — Login Response Schema
![Login Response Schema](screenshots/18_Login_Response_Schema.png)

### 19 — Product Pagination & Filtering Parameters
![Product Pagination Parameters](screenshots/19_Product_Pagination_Filter_Parameters.png)

### 20 — Product Pagination & Filtering — 200 OK
![Product Pagination Success](screenshots/20_Product_Pagination_Filter_Success_200.png)

### 21 — Product Response Schema
![Product Response Schema](screenshots/21_Product_Response_Schema.png)

### 22 — Category Creation Request
![Category Creation Request](screenshots/22_Category_Creation_Request.png)

### 23 — Category Creation Success — 201 Created
![Category Creation Success](screenshots/23_Category_Creation_Success_201.png)

### 24 — Category Hierarchy Request
![Category Hierarchy Request](screenshots/24_Category_Hierarchy_Request.png)

### 25 — Category Hierarchy Success — 200 OK
![Category Hierarchy Success](screenshots/25_Category_Hierarchy_Success_200.png)

### 26 — Category Response Schema
![Category Response Schema](screenshots/26_Category_Response_Schema.png)

## 📁 Project Structure

```text
week7-jpa/
│
├── screenshots/
│   ├── 01_Project_Structure.png
│   ├── 02_Java_Architecture.png
│   ├── 03_Entity_Layer.png
│   ├── 04_Repository_Layer.png
│   ├── 05_Service_Layer.png
│   ├── 06_Controller_Layer.png
│   ├── 07_Database_Resources_Config.png
│   ├── 08_Test_Structure.png
│   ├── 09_Test_Execution_24_of_24.png
│   ├── 10_Swagger_API_Overview.png
│   ├── ...
│   └── 26_Category_Response_Schema.png
│
├── src/
├── .gitignore
├── .gitattributes
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 🔮 Future Enhancements

- JWT-based authentication and authorization
- Persistent shopping cart
- Advanced inventory management
- Order pagination
- Redis-based distributed caching
- Docker containerization
- CI/CD integration
- Production monitoring and metrics
- Additional PostgreSQL deployment configuration

## 👨‍💻 Author

**Ashutosh Panwar**

B.Tech Computer Science Engineering Graduate

GitHub: https://github.com/Ashutosh9-pan

## 📄 License

This project is developed for educational, learning, and portfolio purposes.
