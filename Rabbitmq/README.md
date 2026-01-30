<div align="center">

![RabbitMQ](https://upload.wikimedia.org/wikipedia/commons/7/71/RabbitMQ_logo.svg)

# RabbitMQ Message Broker

</div>

This component provides a **containerized RabbitMQ message broker** that manages **asynchronous messaging** within the system.
It is an **infrastructure-level service**, not a business-logic component, and is used by other services to exchange messages reliably and decoupled.

---

## Overview

The RabbitMQ container is designed to:

* Provide a message broker for asynchronous communication
* Persist queues and configuration data
* Expose standard AMQP and management ports
* Include a default administrative user for management access

---

## Default Credentials

| Username | Password |
| -------- | -------- |
| `guest`  | `guest`  |

> ⚠️ The default credentials should be changed in production environments for security.

---

## Networking

* **Exposed ports:**

  * `5672` → AMQP client connections
  * `15672` → Management UI
* Intended for **internal network access only**

---

## Data Persistence

* Persistent data is stored in the Docker volume:

```
/var/lib/rabbitmq
```

This ensures queue durability across container restarts.

---

## Docker Image

### Base Image

* `rabbitmq:3.13-management`

### Key Characteristics

* Includes RabbitMQ server with management plugin
* Supports durable queues and message persistence
* Lightweight, production-ready container

---

## Operational Notes

* Should run as a **backend-only service**
* Secure the default `guest` user for production
* Designed to be used with backend services that require asynchronous messaging

---

## Summary

This RabbitMQ container provides a stable, persistent, and manageable message broker for the system, enabling decoupled and reliable communication between services.
