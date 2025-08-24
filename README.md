
# Trash2Cash Backend

**Backend for Trash2Cash**, a waste management mobile app connecting **Waste Generators** with **Waste Recyclers**. Built with **Spring Boot**, **PostgreSQL**, and integrated with payment gateways and notifications, this backend supports authentication, waste management, transactions, and notifications for a sustainable and economically beneficial waste ecosystem.

---

## **Table of Contents**

* [Features](#features)
* [Tech Stack](#tech-stack)
* [Getting Started](#getting-started)
* [Environment Variables](#environment-variables)
* [Database Setup](#database-setup)
* [Running Locally](#running-locally)
* [API Endpoints](#api-endpoints)
* [Deployment](#deployment)
* [License](#license)

---

## **Features**

* User Authentication & Profile Management (Waste Generator & Recycler)
* Waste Uploads & Tracking
* Transaction Management & Payment Integration (Flutterwave/Paystack)
* Notifications for uploads, payments, and status updates
* JWT-based security for secure API access
* Dashboard support (points, redeemable amount, transaction history)

---

## **Tech Stack**

* **Backend:** Spring Boot (Java)
* **Database:** PostgreSQL
* **Payment Integration:** Flutterwave or Paystack API
* **Authentication:** JWT & OAuth2 (Google Sign-In)
* **Notifications:** Email / Push

---

## **Getting Started**

Clone the repo:

```bash
git clone https://github.com/<your-username>/waste-management-backend.git
cd waste-management-backend
```

---

## **Environment Variables**

Create a `.env` file or set these in Render:

| Variable Name                | Description                        |
| ---------------------------- | ---------------------------------- |
| `SERVER_PORT`                | Backend server port (default 8089) |
| `SPRING_DATASOURCE_URL`      | PostgreSQL database URL            |
| `SPRING_DATASOURCE_USERNAME` | Database username                  |
| `SPRING_DATASOURCE_PASSWORD` | Database password                  |
| `GOOGLE_CLIENT_ID`           | Google OAuth client ID             |
| `GOOGLE_CLIENT_SECRET`       | Google OAuth client secret         |
| `SECRET_KEY`                 | JWT secret key                     |
| `PAYMENT_API_KEY`            | Flutterwave/Paystack API key       |

---

## **Database Setup**

* Ensure PostgreSQL is installed or use Render PostgreSQL.
* Create database:

```sql
CREATE DATABASE waste_connect;
```

* Spring Boot will handle table creation via `spring.jpa.hibernate.ddl-auto=update`.

---

## **Running Locally**

```bash
# Build the project
./mvnw clean package

# Run the project
java -jar target/waste-management-backend-0.0.1-SNAPSHOT.jar
```

* Access API at: `http://localhost:8089/`

---

## **API Endpoints (Summary)**

**Authentication**

* `POST /auth/signup` – Register user
* `POST /auth/login` – Login
* `POST /auth/forgot-password` – Request password reset
* `POST /auth/reset-password` – Reset password

**Waste Management**

* `POST /waste` – Upload waste
* `GET /waste` – List all waste
* `GET /waste/{id}` – View waste details
* `PUT /waste/{id}` – Update status
* `DELETE /waste/{id}` – Delete waste

**Transactions**

* `POST /transactions/pay` – Make payment
* `POST /transactions/refund` – Request refund
* `GET /transactions/{userId}` – Transaction history

**Users**

* `GET /users/{id}` – Get profile
* `PUT /users/{id}` – Update profile

---

## **Deployment on Render**

1. Create **PostgreSQL database** on Render.
2. Set environment variables in Render dashboard.
3. Create a **Web Service** in Render, connect to GitHub.
4. Build command:

```bash
./mvnw clean package
```

5. Start command:

```bash
java -jar target/waste-management-backend-0.0.1-SNAPSHOT.jar
```

* Render will automatically deploy and provide a live HTTPS URL.

---

## **License**

This project is licensed under the **MIT License**.

---
