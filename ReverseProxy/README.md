# ![Nginx](https://upload.wikimedia.org/wikipedia/commons/c/c5/Nginx_logo.svg) Nginx Reverse Proxy

This component provides an **Nginx reverse proxy** that acts as an **infrastructure layer** within the system architecture.
It is **not a core business service**, but a supporting gateway responsible for routing, security, and performance optimization.

---

## Overview

The Nginx reverse proxy serves as the **single entry point** for all external traffic. Its responsibilities include:

* HTTPS termination (TLS 1.2 / 1.3)
* Redirecting HTTP traffic to HTTPS
* Routing requests to frontend and backend services
* Caching frontend responses
* Supporting WebSocket connections for backend APIs
* Providing consistent access logging

---

## Request Flow

```
Client
  │
  │  HTTP (80) → Redirect
  ▼
HTTPS (443)
  │
  ├── /        → Frontend Service (cached)
  │
  └── /api/    → Backend API Service (WebSocket-enabled)
```

---

## Service Routing

| Path    | Target Service     | Description                                          |
| ------- | ------------------ | ---------------------------------------------------- |
| `/`     | `frontend:5000`    | Frontend application with response caching           |
| `/api/` | `rag-backend:8080` | Backend API with WebSocket and large payload support |

---

## Caching Configuration (Frontend)

The reverse proxy enables response caching for frontend requests to improve performance and reduce backend load.

* **Cache path:** `/var/cache/nginx/frontend`
* **Cache zone:** `frontend_cache`
* **Maximum size:** 1GB
* **Cache duration:**

  * `200`, `302` → 10 minutes
  * `404` → 1 minute
* **Stale cache usage:** Enabled for upstream errors and timeouts
* **Cache bypass conditions:**

  * Requests containing `Authorization` headers
  * Requests containing session cookies

A debug header is always included in responses:

```
X-Proxy-Cache: HIT | MISS | BYPASS | EXPIRED
```

---

## Security

* HTTPS enabled with custom TLS certificates
* Supported protocols:

  * TLSv1.2
  * TLSv1.3
* All HTTP traffic is permanently redirected to HTTPS

Certificates are mounted inside the container at:

```
/etc/nginx/nginx-certs/
```

---

## Backend API Configuration

The `/api/` endpoint is optimized for backend services requiring long-lived connections and large request bodies.

* WebSocket upgrade support
* Disabled request and response buffering
* Maximum request body size: **100MB**
* Extended read and send timeouts for persistent connections

---

## Docker Image

The reverse proxy runs inside a lightweight Alpine-based Nginx container.

### Key Characteristics

* Base image: `nginx:stable-alpine3.23`
* Persistent cache volume:

```
/var/cache/nginx
```

* Exposed ports:

  * `80` (HTTP)
  * `443` (HTTPS)

---

## Operational Notes

* Intended for use in **containerized environments** (e.g., Docker Compose)
* Service names rely on internal container networking
* Optimized for **production-like deployments**, not local development
* Acts purely as an infrastructure component and does not contain business logic

---

## Summary

This Nginx reverse proxy provides a secure, scalable, and performance-oriented gateway for the system. It simplifies traffic management while keeping frontend and backend services isolated and focused on their respective responsibilities.
