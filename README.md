
---

# 🍽️ DineYou – QR Based Digital Menu & Smart Ordering Platform

DineYou is a cloud-ready microservices-based in dining ordering platform that enables customers to scan a QR code, browse dynamic menus, place orders, and complete secure payments — while providing restaurants with operational intelligence and load-aware order handling.

---
=======
## 🎥 DineYou – System Demo

This demo showcases:

- Restaurant onboarding
- Menu management
- QR ordering
- Cart & checkout
- Payment flow
- Order tracking

👇 Watch the full demo below:

https://github.com/user-attachments/assets/YOUR_VIDEO_ID.mp4


## 🏗 Architecture Overview
>>>>>>> c01f662 (Added demo video)

DineYou follows a **microservices architecture** built using **Spring Boot**, secured with **Keycloak**, containerized with **Docker**, and orchestrated via **Docker Compose**.

### Core Services

| Service            | Port | Responsibility                       |
| ------------------ | ---- | ------------------------------------ |
| restaurant_service | 8080 | Restaurant management & onboarding   |
| menucard_service   | 8081 | Dynamic menu management              |
| cart_service       | 8082 | Cart lifecycle & item aggregation    |
| order_service      | 8083 | Order processing & state transitions |
| payment_service    | 8084 | Payment integration (Stripe)         |
| keycloak           | 8585 | Authentication & RBAC                |
| MySQL              | 3307 | Persistent storage                   |

---

## 🔐 Authentication & Authorization

* Powered by **Keycloak**
* Role-Based Access Control (RBAC)
* Secured endpoints via OAuth2 / JWT
* Custom Keycloak theme included (`keycloak-custom-theme`)

Roles typically include:

* Customer
* Restaurant Admin
* Platform Admin

---

## 🧩 Microservices Responsibilities

### 🏪 restaurant_service

* Restaurant onboarding
* Metadata management
* Restaurant-level configurations

### 📋 menucard_service

* Category & item management
* Availability toggling
* Dynamic prioritization

### 🛒 cart_service

* Cart creation
* Item addition/removal
* Quantity management
* Price aggregation

### 📦 order_service

* Order placement
* Status transitions (CREATED → CONFIRMED → PREPARING → COMPLETED)
* Inter-service orchestration

### 💳 payment_service

* Stripe payment integration
* Secure transaction handling
* Payment status validation
* Order-payment synchronization

---

## 🌐 Frontend

* React-based UI (`dineyou-ui`)
* Communicates via REST APIs
* Auth integrated with Keycloak
* Designed for QR-based restaurant access

---

# 🐳 Deployment (Docker-Based)

DineYou is fully containerized and deployable using Docker Compose.

---

## 📦 Prerequisites

* Docker
* Docker Compose
* Git

---

## 📂 Project Structure (Simplified)

```
DineYou/
│
├── restaurant_service/
├── menucard_service/
├── cart_service/
├── order_service/
├── payment_service/
├── dineyou-ui/
├── keycloak-custom-theme/
└── deployment/
    ├── docker-compose.yml
    └── env/
```

---

## ⚙️ Environment Configuration

Each service loads configuration from:

```
deployment/env/
```

Example structure:

```
env/
├── global.env
├── mysql/.env
├── restaurant_service/.env
├── menucard_service/.env
├── cart_service/.env
├── order_service/.env
└── payment_service/.env
```

⚠️ Secrets should never be committed. Use environment variables like:

```
STRIPE_SECRET_KEY=your_key_here
```

---

## 🚀 Running the Entire System

Navigate to the deployment directory:

```bash
cd deployment
```

Build and start all services:

```bash
docker-compose up --build
```

Run in detached mode:

```bash
docker-compose up -d --build
```

---

## 🔍 Service Access

| Component          | URL                                            |
| ------------------ | ---------------------------------------------- |
| Keycloak           | [http://localhost:8585](http://localhost:8585) |
| Restaurant Service | [http://localhost:8080](http://localhost:8080) |
| Menu Service       | [http://localhost:8081](http://localhost:8081) |
| Cart Service       | [http://localhost:8082](http://localhost:8082) |
| Order Service      | [http://localhost:8083](http://localhost:8083) |
| Payment Service    | [http://localhost:8084](http://localhost:8084) |
| MySQL              | localhost:3307                                 |

---

## 🗄 Database

* MySQL 8.0
* Persistent volume: `db-data`
* UTF8MB4 charset
* Health checks enabled

---

## 🧠 Smart System Design Highlights

* Microservice separation of concerns
* Secure OAuth2-based authentication
* Stripe payment flow integration
* Containerized environment parity
* Health-checked service startup dependencies
* Persistent volumes for logs & database
* Isolated Docker network (`dineyou-network`)

---

## 🔄 Service Dependency Flow

```
Keycloak → Authentication Layer

Restaurant → Menu → Cart → Order → Payment
```

Each service depends only on necessary upstream services to reduce coupling.

---

## 🧾 Logs

Each service maintains persistent logs via Docker volumes:

* restaurant-service-logs
* menucard-service-logs
* cart-service-logs
* order-service-logs
* payment-service-logs

---

## 🌍 Production Considerations (Recommended)

For production deployment:

* Replace `start-dev` Keycloak with production mode
* Use managed MySQL (RDS/Azure DB)
* Add API Gateway or NGINX reverse proxy
* Enable HTTPS (TLS)
* Externalize secrets via:

  * AWS Secrets Manager
  * Azure Key Vault
  * Environment injection
* Add centralized logging (ELK)
* Add distributed tracing (Zipkin / OpenTelemetry)

---

## 🔮 Future Enhancements

* Event-driven order processing (Kafka)
* Horizontal scaling via Kubernetes
* Rate limiting
* Restaurant performance analytics
* Real-time order status via WebSockets

---

## 👨‍💻 Tech Stack

* Java 17
* Spring Boot
* Spring Security
* Keycloak
* MySQL
* Docker
* Docker Compose
* React
* Stripe API

---

# 📌 Summary

DineYou demonstrates:

* Microservices architecture
* Secure authentication & authorization
* Payment gateway integration
* Containerized deployment
* Production-ready environment structuring

This project reflects backend system design, service orchestration, and real-world application architecture.

---

