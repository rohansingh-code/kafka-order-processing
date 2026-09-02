# Kafka Real-Time Order Processing System

## Project Overview
This project demonstrates a real-time event-driven order processing pipeline built with Spring Boot and Apache Kafka. 

The system exposes REST APIs to create and retrieve orders. When an order is successfully created and persisted in the database (H2 in-memory), the backend publishes an `OrderCreatedEvent` to a Kafka topic named `orders`. 
Multiple independent Kafka consumers subscribe to this topic to process the events asynchronously without blocking the main API thread.

## Architecture

```text
                    Client
                      |
                      | POST /api/orders
                      v
              +----------------+
              | Spring Boot    |
              | Order Service  |
              +----------------+
                      |
                      | OrderCreated Event
                      v
              +----------------+
              | Kafka Producer |
              +----------------+
                      |
                      v
              +----------------+
              | Kafka Broker   |
              | orders topic   |
              +----------------+
                    /     \
                   /       \
                  v         v
        +---------------+  +----------------+
        | Order         |  | Notification   |
        | Analytics     |  | Consumer       |
        | Consumer      |  |                |
        +---------------+  +----------------+
                |                  |
                v                  v
          Analytics Data      Notification Log
```

## Technologies Used
- **Java 21**
- **Spring Boot 3.4.x** (Web, Data JPA, Validation)
- **Spring Kafka**
- **Apache Kafka** (Dockerized, KRaft mode)
- **H2 Database** (In-memory)
- **Maven**
- **Docker Compose**

## Features
- **REST APIs**: Endpoints to create and retrieve orders.
- **Data Persistence**: Stores orders in an H2 in-memory database using Spring Data JPA.
- **Asynchronous Event Publishing**: Publishes an event to Kafka after order creation.
- **Independent Consumers**: 
  - `AnalyticsConsumer` computes total orders and total revenue.
  - `NotificationConsumer` simulates sending notifications for created orders.
- **Error Handling**: Handles invalid order payloads with structured error responses.

## How Kafka Is Used
- **Producer**: Uses `KafkaTemplate` to publish JSON serialized `OrderCreatedEvent` objects.
- **Topic**: Messages are sent to the `orders` topic.
- **Consumers & Consumer Groups**: 
  - We define two consumers with different `groupId`s (`analytics-group` and `notification-group`). 
  - Because they have different group IDs, Kafka delivers a copy of each event to *both* consumers independently.
- **Event-Driven Communication**: The system decoupling ensures the main API responds quickly while downstream processes (analytics, notifications) handle the event in the background.

## API Documentation

### Create Order

```http
POST /api/orders
Content-Type: application/json

{
  "userId": "U101",
  "product": "Laptop",
  "quantity": 1,
  "amount": 75000
}
```

### Get Order

```http
GET /api/orders/{orderId}
```

### Get All Orders

```http
GET /api/orders
```

## Running the Project

### Prerequisites
- Docker & Docker Compose
- Java 21+

### 1. Start Kafka Infrastructure
Run the following command from the project root to start a single-node Kafka instance in KRaft mode:

```bash
docker compose up -d
```

### 2. Start the Spring Boot Application
Run the application using Maven wrapper:

```bash
./mvnw spring-boot:run
```

## Example Flow

1. Client sends a `POST /api/orders` request.
2. The `OrderService` saves the order and triggers the `KafkaOrderProducer`.
3. The event is published to the `orders` topic.
4. The `AnalyticsConsumer` receives it and updates its metrics (logged to console).
5. The `NotificationConsumer` receives it and logs a simulated notification.

## Future Improvements
- **Dead Letter Topics (DLQ)**: For capturing failed event processing.
- **Retry Mechanisms**: To retry publishing or consuming messages on transient errors.
- **Schema Registry**: Using Avro/Protobuf for structured and versioned schemas instead of plain JSON.
- **Persistent Database**: Swap H2 with PostgreSQL for production workloads.
- **Monitoring**: Add Prometheus/Grafana to monitor Kafka and Spring Boot metrics.
