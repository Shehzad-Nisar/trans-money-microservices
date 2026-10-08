# TransMoney 💸

TransMoney is a production-ready, distributed digital wallet and banking transaction platform. It features a scalable **Spring Boot Microservices** backend architecture paired with a highly responsive, modern **React + Vite** frontend.

## 🌟 Key Features

* **Secure Authentication**: JWT-based authentication routed securely through an API Gateway.
* **Account Management**: Create and manage digital banking accounts (Current/Savings) with real-time balance tracking.
* **Idempotent Transfers**: SAGA-pattern based money transfers ensure complete transactional integrity even during network failures. Idempotent API requests guarantee no duplicate transfers.
* **Fraud Detection & OTP**: Asynchronous transaction verification via Kafka. High-risk transactions are automatically flagged for OTP verification or instantly blocked.
* **Real-time Notifications**: Scalable notification consumption layer ready to dispatch email/SMS alerts.
* **Modern Dashboard**: Beautiful, responsive dashboard built with Tailwind CSS, featuring transaction history, visual status indicators, and account insights.

---

## 🏗️ Architecture

### Backend (Spring Boot Microservices)
The backend is split into multiple independent services communicating via REST and asynchronous Kafka events.

* **`api-gateway` (Port 8080)**: The single entry point for the frontend. Handles JWT verification, CORS, and routing to downstream services.
* **`auth-service` (Port 8081)**: Manages user registration and JWT token generation.
* **`account-service` (Port 8082)**: Manages banking accounts, balances, and deduct/credit operations.
* **`transaction-service` (Port 8083)**: Handles transfer orchestration, idempotency, and the SAGA state machine.
* **`fraud-service` (Port 8084)**: Listens to Kafka streams to analyze transactions and trigger OTP or account blocks.
* **`notification-service` (Port 8085)**: Consumes Kafka events (OTP required, transaction completed, refund processed) to simulate email/SMS alerts.

### Frontend (React + Vite + TypeScript)
Located in the `/frontend` directory, built for performance and maintainability.

* **Routing**: React Router v6 with protected and public route layouts.
* **State Management**: Zustand (with local storage persistence) for Auth/User state.
* **Styling**: Tailwind CSS + Lucide React icons.
* **Forms & Validation**: React Hook Form paired with Zod schemas.
* **API Communication**: Axios with central interceptors handling JWT injection and 401 redirects.

---

## 🚀 Getting Started

### Prerequisites
* **Java 17+** and Maven
* **Node.js 18+**
* **Docker** (For PostgreSQL, Redis, and Kafka)

### 1. Start Infrastructure (Databases & Message Brokers)
Ensure you have Docker running, then spin up the required infrastructure (Kafka, Redis, PostgreSQL) using your Docker Compose file (if configured) or run them locally.

### 2. Run the Backend Microservices
Open separate terminals and start each microservice. *Ensure the API Gateway is running!*
```bash
# Example for running a service via Maven wrapper
cd backend/api-gateway
./mvnw spring-boot:run

cd ../auth-service
./mvnw spring-boot:run

# Repeat for account, transaction, fraud, and notification services
```

### 3. Run the Frontend
Open a new terminal and navigate to the frontend directory:
```bash
cd frontend

# Install dependencies
npm install

# Start the Vite development server
npm run dev
```

The frontend will be available at `http://localhost:5173` (or `5174`). It is pre-configured to communicate with the API Gateway at `http://localhost:8080`.

---

## 🛡️ Idempotency & Security

* **CORS**: Globally configured at the API Gateway level to strictly allow trusted frontend origins.
* **Idempotency Keys**: The frontend generates a unique `UUID` per transfer screen load. If a request times out and the user retries, the exact same UUID is sent, ensuring the backend orchestration never double-charges the account.
* **Stateless Auth**: No session state is stored on the servers. The `auth-service` issues a JWT which the frontend persists and the `api-gateway` validates on every secured request.

## 🛠️ Development & Tooling

* **Frontend Build**: Run `npm run build` to output optimized static assets. TypeScript strict mode is fully enforced (`tsc -b`).
* **Error Boundaries**: The frontend features a top-level React Error Boundary that cleanly catches render failures and provides a "Reload" mechanism without breaking the DOM.

---

## 🔐 Secrets & Sensitive Data

> [!CAUTION]
> Never commit real credentials to Git. The files below are already added to `.gitignore`.

### What is sensitive in this project?

| Secret | Location | Status |
|---|---|---|
| `JWT_SECRET` | `auth-service/application.yaml`, `api-gateway/application.yaml` | ✅ Uses `${JWT_SECRET}` env var — **not hardcoded** |
| MySQL root password | `docker-compose.yml`, service `application.yaml` files | ✅ Uses `${MYSQL_ROOT_PASSWORD}` env var |
| MySQL user/password | Service `application.yaml` files | ✅ Uses `${MYSQL_PASSWORD}` env var |
| Frontend API URL | `frontend/src/api/client.ts` | ⚠️ Currently hardcoded to `localhost:8080` — update for production |

### Setup for local development

1. Copy `.env.example` to `.env` in the project root:
   ```bash
   cp .env.example .env
   ```

2. Fill in your real secrets in `.env`. This file is in `.gitignore` and will never be committed.

3. When starting Spring Boot services (via IntelliJ or terminal), set the environment variables. IntelliJ supports loading `.env` files via **Run > Edit Configurations > EnvFile** plugin.

   Or set them directly in your shell:
   ```bash
   export JWT_SECRET="your_secret_here"
   export MYSQL_ROOT_PASSWORD="your_password_here"
   ./mvnw spring-boot:run
   ```

4. For Docker Compose, create a `.env` file in the `backend/` folder alongside `docker-compose.yml`. Docker Compose automatically picks it up:
   ```env
   MYSQL_ROOT_PASSWORD=your_strong_password
   MYSQL_PASSWORD=your_strong_password
   ```

### What is already safe?

- ✅ `JWT_SECRET` was already using `${JWT_SECRET}` placeholder in all YAML files — **no hardcoded secret was ever present**.
- ✅ The frontend stores the JWT in `localStorage` only (via Zustand persist) — **no tokens are ever sent to a backend session**.
- ✅ The API Gateway validates and strips/rewrites the `X-User-Email` header, so frontend code can never spoof another user's identity.

