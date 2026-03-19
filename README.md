# AI Customer Support Platform

Production-style backend for a multi-tenant AI customer support SaaS built with Java, Spring Boot, PostgreSQL, Docker, and OpenAI integration.

## Overview

This project simulates a real customer support platform used by multiple business clients. Each tenant has isolated conversations, persisted chat history, memory-aware prompt building, usage tracking, and an LLM integration layer designed to be replaceable.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker and Docker Compose
- OpenAI API
- Spring Cache

## Core Features

- Multi-tenant conversation isolation with `tenantId`
- Chat orchestration endpoint backed by OpenAI
- Conversation memory with recent-history plus lightweight relevance retrieval
- Usage tracking for token consumption and latency
- Flyway-managed relational schema
- Health endpoint and actuator metrics exposure

## Project Structure

```text
src/main/java/io/pablo/aicustomersupport
|-- chat
|-- common
|-- conversation
|-- health
|-- llm
|-- memory
|-- message
|-- tenant
`-- usage
```

## Local Development

### Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop
- OpenAI API key

### Run with Docker Compose

1. Copy the env template:

```powershell
Copy-Item .env.example .env
```

2. Set `OPENAI_API_KEY` in `.env`.

3. Start the stack:

```powershell
docker compose up --build
```

4. Verify the API:

```powershell
curl http://localhost:8080/api/v1/health
```

### Run without Docker

1. Start PostgreSQL locally and create a database named `ai_support`.

2. Export environment variables:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/ai_support"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:OPENAI_API_KEY="your-openai-api-key"
```

3. Run the application:

```powershell
mvn spring-boot:run
```

## Build and Test

```powershell
mvn test
```

Package a runnable jar:

```powershell
mvn clean package
```

## Main API Endpoints

- `POST /api/v1/tenants`
- `GET /api/v1/tenants`
- `POST /api/v1/tenants/{tenantId}/conversations`
- `GET /api/v1/tenants/{tenantId}/conversations`
- `GET /api/v1/tenants/{tenantId}/conversations/{conversationId}/messages`
- `POST /api/v1/tenants/{tenantId}/conversations/{conversationId}/chat`
- `GET /api/v1/health`

## Example Chat Flow

Create a tenant:

```json
{
  "name": "Acme Support",
  "externalKey": "acme-support"
}
```

Create a conversation:

```json
{
  "customerId": "cust-123",
  "subject": "Refund request"
}
```

Send a chat message:

```json
{
  "message": "My order arrived damaged. What should I do?",
  "model": "gpt-4o-mini"
}
```

## Demo Walkthrough

This is the fastest way to demonstrate the project in a portfolio review or interview.

### 1. Start the platform

```powershell
docker compose up --build
```

Wait until the application is reachable on `http://localhost:8080`.

### 2. Verify service health

```powershell
Invoke-RestMethod -Method GET `
  -Uri "http://localhost:8080/api/v1/health"
```

Expected response:

```json
{
  "status": "UP"
}
```

### 3. Create a tenant

```powershell
$tenant = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/v1/tenants" `
  -ContentType "application/json" `
  -Body '{"name":"Acme Support","externalKey":"acme-support"}'

$tenant
```

Example response:

```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "name": "Acme Support",
  "externalKey": "acme-support",
  "status": "ACTIVE",
  "createdAt": "2026-03-19T20:45:00Z"
}
```

### 4. Create a conversation

```powershell
$conversation = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/v1/tenants/$($tenant.id)/conversations" `
  -ContentType "application/json" `
  -Body '{"customerId":"cust-123","subject":"Refund request"}'

$conversation
```

Example response:

```json
{
  "id": "22222222-2222-2222-2222-222222222222",
  "tenantId": "11111111-1111-1111-1111-111111111111",
  "customerId": "cust-123",
  "subject": "Refund request",
  "status": "OPEN",
  "createdAt": "2026-03-19T20:46:00Z"
}
```

### 5. Send a chat message

```powershell
$chat = Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/v1/tenants/$($tenant.id)/conversations/$($conversation.id)/chat" `
  -ContentType "application/json" `
  -Body '{"message":"My order arrived damaged. What should I do?","model":"gpt-4o-mini"}'

$chat
```

Example response:

```json
{
  "tenantId": "11111111-1111-1111-1111-111111111111",
  "conversationId": "22222222-2222-2222-2222-222222222222",
  "userMessage": {
    "id": "33333333-3333-3333-3333-333333333333",
    "role": "USER",
    "content": "My order arrived damaged. What should I do?",
    "messageOrder": 1,
    "modelName": null,
    "createdAt": "2026-03-19T20:47:00Z"
  },
  "assistantMessage": {
    "id": "44444444-4444-4444-4444-444444444444",
    "role": "ASSISTANT",
    "content": "I'm sorry that happened. Please share your order number and a photo of the damage so I can help with the next steps.",
    "messageOrder": 2,
    "modelName": "gpt-4o-mini",
    "createdAt": "2026-03-19T20:47:02Z"
  },
  "usage": {
    "promptTokens": 420,
    "completionTokens": 62,
    "totalTokens": 482,
    "responseTimeMs": 1180
  }
}
```

### 6. Inspect persisted conversation history

```powershell
Invoke-RestMethod -Method GET `
  -Uri "http://localhost:8080/api/v1/tenants/$($tenant.id)/conversations/$($conversation.id)/messages"
```

This confirms that the platform is not just generating responses, but also persisting the tenant-scoped conversation state for future memory retrieval.

### What This Demo Proves

- The API is reachable and healthy
- Multi-tenant routing is working
- Conversation state is persisted in PostgreSQL
- The chat orchestration flow calls the LLM provider successfully
- Usage metadata is captured alongside the assistant response
- The project is runnable locally as a realistic backend service, not just a code sample

## Deployment Notes

### Docker

- `Dockerfile` builds the application jar in a Maven builder stage and runs it on a slim JRE base image.
- `docker-compose.yml` provisions PostgreSQL and the Spring Boot app together for local development.

### AWS

Recommended production path:

- App: ECS Fargate or App Runner
- Database: Amazon RDS for PostgreSQL
- Cache: ElastiCache Redis when moving beyond in-memory cache
- Secrets: AWS Secrets Manager or SSM Parameter Store
- Monitoring: CloudWatch logs, metrics, alarms

### GCP

Recommended production path:

- App: Cloud Run
- Database: Cloud SQL for PostgreSQL
- Cache: Memorystore Redis
- Secrets: Secret Manager
- Monitoring: Cloud Logging and Cloud Monitoring

## Evolution Path

- Replace keyword-based retrieval with embeddings plus pgvector
- Add authentication and tenant authorization
- Add async events for analytics and billing
- Split into chat, memory, and usage services when scale justifies it
