# RAGMind Backend

![Spring Boot](https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/spring-boot-icon.png)

RAGMind is a backend system for managing AI-driven RAG (Retrieval-Augmented Generation) pipelines with user-isolated document storage and real-time WebSocket notifications.

---

## Overview

RAGMind supports:

* **RAG Ask Tasks**: AI query handling with user-specific data isolation.
* **Document Management Tasks**: Uploading and querying documents per user.
* **Real-time Communication**: WebSocket + STOMP for instant responses and notifications.
* **Shared Storage Management**: Ensures no path conflicts and secure data isolation for each user.

### Architecture Flow

#### RAG Ask Task Flow

1. Listen to the RabbitMQ queue for RAG ask tasks.
2. When a task arrives:

   * Validate the message format.
   * Load the **ChromaDB collection** specific to the user (isolated per user and collection).
   * Start the RAG pipeline and generate a response.
   * Send the response back via the RabbitMQ feedback exchanger.

#### Document Task Flow

1. Listen to the RabbitMQ queue for incoming document tasks.
2. When a document task arrives:

   * Check if the document exists in **shared storage**.
   * If available, feed it into the RAG pipeline.
   * Send back the response through the RabbitMQ feedback exchanger.

### Shared Storage Management

* Each request contains key identifiers: **userEmail**, **collectionId**, **modelName**, **embedModelName**.
* These identifiers are combined and hashed to generate a unique **collection folder name**.
* Backend structure:

  ```
  /shared-storage/
      user@example.com/
          <hashed-collection-name>/
              document1.pdf
              document2.pdf
  ```
* Guarantees:

  * No conflicts between users.
  * No overridden data even if collection names repeat.
  * User isolation is strictly maintained.

---

## API Documentation

> Scroll down for code samples, example requests, and responses.

**Base URL:** `http://localhost:8080`

### Authentication

* HTTP Bearer token authentication.

### Endpoints Overview

#### RAG Queries

* `POST /rag/collections/{collectionId}/queries/ask` - Ask a question
* `GET /rag/collections/{collectionId}/queries/chat-history` - Get chat history

#### Document Management

* `GET /rag/collection/{collectionId}/documents/` - List documents
* `POST /rag/collection/{collectionId}/documents/` - Upload document
* `GET /rag/collection/{collectionId}/documents/{documentId}` - Get a specific document

#### Collections

* `GET /rag/collection/` - List user collections
* `POST /rag/collection/` - Add a new collection
* `GET /rag/collection/{collectionId}` - Get specific collection
* `DELETE /rag/collection/{collectionId}` - Delete collection

#### Authentication

* `POST /auth/signup` - User signup
* `POST /auth/login` - User login
* `POST /auth/refreshtoken` - Refresh JWT token

---

## WebSocket Documentation

RAGMind supports **real-time communication** using WebSocket with the **STOMP protocol** and SockJS fallback.

### Endpoint

```
/ws
```

* JWT token required in `Authorization` header

### Available Queues

| Queue                           | Description                                          |
| ------------------------------- | ---------------------------------------------------- |
| `/user/queue/ask-result`        | Receive AI-generated answers for submitted questions |
| `/user/queue/sync-notification` | Receive notifications, updates, or alerts            |

### Client Steps

1. Connect to `/ws` using STOMP/SockJS.
2. Include JWT in `Authorization` header.
3. Subscribe to queues.
4. Handle incoming messages.

### Notes

* JWT ensures only authorized users receive messages.
* Each user receives messages isolated from others.
* SockJS ensures browser compatibility.

---

This README provides a **quick-start guide** for developers integrating with RAGMind, including **task flows**, **shared storage management**, **REST API usage**, and **real-time WebSocket integration**.
