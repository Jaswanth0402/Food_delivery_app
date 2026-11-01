# Food Delivery Platform Backend

A multi-tenant, production-grade food delivery backend engine built with Java 21, Spring Boot, MySQL, and Redis, fully containerized using Docker Compose.

## 🚀 Tech Stack & Architecture
- **Language & Framework:** Java 21, Spring Boot 4.x
- **Database & Migration:** MySQL 8.0, Flyway (11 version-controlled migrations)
- **Caching & Real-Time:** Redis 7 (Lettuce connection pooling, Geospatial indexing)
- **Security:** Stateless JWT Authentication with Role-Based Access Control (RBAC)
- **Containerization:** Multi-stage Docker builds with automated health checks

## 🛠️ Quick Start (Docker)

1. Clone repository:
   ```bash
   git clone [https://github.com/](https://github.com/)<your-username>/food-delivery-platform.git
   cd food-delivery-platform