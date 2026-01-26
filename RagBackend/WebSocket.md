# RAGMind WebSocket Documentation

RAGMind supports **real-time communication** using WebSocket with the **STOMP protocol** and SockJS for fallback transport. This allows clients to receive instant AI responses and notifications.

---

## WebSocket Endpoint

```
/ws
```

* Supports **STOMP protocol**
* Uses **SockJS** for browser compatibility and fallback
* **JWT token required** for authentication in the `Authorization` header

### Connection Headers Example

```
Authorization: Bearer <ACCESS_TOKEN>
```

---

## STOMP Configuration (Backend)

The backend Spring Boot configuration uses the following setup:

### Register STOMP Endpoints

* Endpoint: `/ws`
* Allowed Origins: `*` (can be restricted in production)
* SockJS enabled

### Message Broker Configuration

* **Application destination prefix:** `/app`
* **Simple broker destinations:** `/queue`
* **User-specific destination prefix:** `/user`

### Message Sending Methods

| Method                                              | Description                                      | Destination                     |
| --------------------------------------------------- | ------------------------------------------------ | ------------------------------- |
| `syncUserMessages(principalEmailName, payload)`     | Sends AI-generated answers to a specific user    | `/user/queue/ask-result`        |
| `syncUserNotification(principalEmailName, payload)` | Sends real-time notifications to a specific user | `/user/queue/sync-notification` |

---

## Client Subscription Guide

Clients can subscribe to queues to receive messages in real time.

### Available Queues

| Queue                           | Description                                          |
| ------------------------------- | ---------------------------------------------------- |
| `/user/queue/ask-result`        | Receive AI-generated answers for submitted questions |
| `/user/queue/sync-notification` | Receive notifications, updates, or alerts            |

### Steps to Connect

1. Connect to the `/ws` endpoint using a STOMP client (e.g., STOMP.js, SockJS).
2. Include your JWT token in the `Authorization` header.
3. Subscribe to the desired queues (`/user/queue/ask-result` or `/user/queue/sync-notification`).
4. Handle messages received from the queues.

---

## Notes

* JWT tokens are required to validate users.
* The backend ensures **user-specific messages** are delivered only to the correct recipient.
* SockJS ensures compatibility with browsers that do not support native WebSockets.

---

This document is intended to be a standalone reference for developers integrating with RAGMind's WebSocket API.
