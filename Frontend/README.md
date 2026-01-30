# RAGMind Frontend

Angular frontend for **RAGMind**: a RAG (Retrieval-Augmented Generation) application for managing document collections and chatting with AI over your data. This document describes the app architecture, authentication (access/refresh tokens), WebSocket usage, and how the pieces fit together.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Authentication](#authentication)
- [HTTP & Auth Interceptor](#http--auth-interceptor)
- [Route Guards & Session Restoration](#route-guards--session-restoration)
- [WebSocket (STOMP)](#websocket-stomp)
- [Features Overview](#features-overview)
- [Environment & Proxy](#environment--proxy)
- [Docker](#docker)
- [Scripts Reference](#scripts-reference)

---

## Tech Stack

- **Angular** 21 (standalone components, signals, inject)
- **RxJS** 7.8
- **STOMP over SockJS** (`@stomp/stompjs`, `sockjs-client`) for real-time RAG answers
- **Tailwind CSS** for styling
- **Vitest** for unit tests

---

## Getting Started

### Prerequisites

- Node.js (LTS)
- npm 10.x (or the project’s `packageManager`)

### Install & run

```bash
npm install
npm start
```

- Dev server: **http://localhost:4200**
- Development build uses `environment.development.ts` (API at `http://localhost:8080`) and `proxy.conf.json` when needed.

### Build for production

```bash
npm run build
```

Output: `dist/Frontend/browser/`. Production uses `environment.ts` (e.g. `https://localhost/api`).

### Tests

```bash
npm test
```

Runs Vitest unit tests.

---

## Project Structure

```
src/
├── app/
│   ├── app.config.ts          # Router, HttpClient, auth interceptor
│   ├── app.routes.ts          # Route definitions and auth guards
│   ├── app.ts                 # Root component (nav, footer, outlet)
│   ├── config/
│   │   └── api-config.ts      # API_BASE_URL, WS_BASE_URL injection tokens
│   ├── guards/
│   │   └── auth.guard-guard.ts   # Protects routes; tries refresh then redirects to /login
│   ├── interceptors/
│   │   └── auth.interceptor.ts   # Adds Bearer accessToken to API requests
│   ├── model/                    # DTOs: UserModel, CollectionModel, ChatRecordModel, etc.
│   ├── pages/                    # Route components: landing, login, register, collections, collection, etc.
│   ├── components/               # Shared: nav, footer
│   └── services/
│       ├── auth-service.ts       # Login, refresh, session, currentUser
│       ├── websocket-service.ts  # STOMP connection, /user/queue/ask-result
│       ├── collection-service.ts # Collections, documents, ask, chat history
│       └── user-service.ts       # /user/me, update, change-password
├── environments/
│   ├── environment.ts            # Production API URL
│   └── environment.development.ts
├── main.ts
└── styles.scss
```

---

## Authentication

The app uses **JWT-style access and refresh tokens** provided by the backend.

### Tokens and storage

- **Access token**: Short-lived; sent on every API request and when opening the WebSocket.
- **Refresh token**: Long-lived; used only to get a new access token (and optionally a new refresh token).

Storage:

- **`currentUser`** (localStorage): JSON object with user fields plus `accessToken` and `refreshToken` (as returned by login/refresh).
- **`refreshToken`** (localStorage): Dedicated key for the refresh token so the guard can restore session even if `currentUser` is missing or outdated.

On **login**, the backend returns a user object that includes `accessToken` and `refreshToken`. The app:

1. Saves the full user (including tokens) in `currentUser`.
2. Saves the refresh token again in `refreshToken`.

On **refresh** (see below), the new tokens are written back to both places.

### Auth flow

1. **Login** (`AuthService.login(email, password)`):  
   `POST /auth/login` → response with `accessToken`, `refreshToken`, user fields → store in localStorage, set `currentUser` and `isAuthenticated` in memory.

2. **Refresh** (`AuthService.refreshToken()`):  
   `POST /auth/refreshtoken` with `Authorization: Bearer <refreshToken>` → new `accessToken` (and optionally `refreshToken`) → update `currentUser` and `refreshToken` in localStorage and in-memory state.

3. **Session restoration** (`AuthService.tryRefreshFromStoredToken()`):  
   Used when the app thinks the user is not authenticated (e.g. page reload, or guard run). If a stored refresh token exists, calls refresh; on success, fetches `/user/me` with the new access token and rebuilds `currentUser` (with tokens) and sets authenticated. Returns `true`/`false` so the guard can allow or redirect to login.

4. **Logout** (`AuthService.logout()`):  
   Clears `currentUser` and `refreshToken` from localStorage and sets authenticated to false.

The app does **not** automatically retry failed HTTP requests with a refreshed token (e.g. on 401). Protected routes rely on the guard and `tryRefreshFromStoredToken` so that by the time the user hits a protected page, a valid access token is available.

---

## HTTP & Auth Interceptor

- **File**: `src/app/interceptors/auth.interceptor.ts`
- **Registered in**: `app.config.ts` via `provideHttpClient(withInterceptors([authInterceptor]))`.

Behavior:

1. Reads the current **access token** from `AuthService.currentUserValue` or, if needed, from `localStorage.currentUser`.
2. If there is no token, the request is sent unchanged.
3. If the request URL is for the API (starts with `API_BASE_URL` or `/api/` or `/api`), the interceptor clones the request and sets:
   - `Authorization: Bearer <accessToken>`.

Only API requests get the header; other requests are left as-is.

---

## Route Guards & Session Restoration

- **File**: `src/app/guards/auth.guard-guard.ts`
- **Used in**: `app.routes.ts` for `collections`, `create-collection`, `settings` (and their children).

Flow:

1. On the server (SSR), the guard allows the route (`true`).
2. In the browser:
   - If `AuthService` says the user is **authenticated**, the guard allows the route.
   - If not, it calls `AuthService.tryRefreshFromStoredToken()`:
     - If that returns `true`, the user is considered logged in and the route is allowed.
     - If `false`, the guard redirects to `/login` and denies the route.

So: **refresh token is used to restore session** when the user is not yet marked authenticated (e.g. after reload or when the access token was never or no longer in memory). The guard does not handle 401 on individual HTTP calls; it only ensures that when you enter a protected page, the app has tried to restore session from the stored refresh token.

---

## WebSocket (STOMP)

Real-time RAG answers are delivered over **STOMP over SockJS**, with authentication via the **access token**.

### Configuration

- **Base URL**: Derived from the same base as the HTTP API in `api-config.ts`: `WS_BASE_URL` = API base + `/ws` (e.g. `http://localhost:8080/ws`). SockJS uses HTTP(S), not `ws(s):`.
- **Auth**: When connecting, the STOMP client sends the current **access token** in the STOMP connect headers: `Authorization: Bearer <accessToken>`.

### Service: `WebsocketService`

- **File**: `src/app/services/websocket-service.ts`
- **Connect**: `WebsocketService.connect()` uses the current user’s `accessToken` from `AuthService.currentUserValue`. If there is no token, it does not connect and reports an error state.
- **Transport**: SockJS + STOMP (`@stomp/stompjs`, `sockjs-client`). Reconnect delay 5s.
- **Subscription**: After connection, the client subscribes to **`/user/queue/ask-result`** (user-specific queue). Each message is expected to be a RAG feedback payload (e.g. `RagFeedbackResponseDto` from the backend).
- **Mapping**: Incoming messages are mapped to `AskResultMessage`: `{ collectionId, record: ChatRecordModel }`. `collection_name` in the payload is treated as the collection id; `response` (or similar) as the assistant message text; `taskId` or `backend_id` as the record id.
- **Observables**:
  - `askResult$`: Emits each `AskResultMessage` so the collection page can append the assistant reply to the right collection’s chat.
  - `connectionState$`: `'connected' | 'disconnected' | 'error'`.

The **collection page** subscribes to `askResult$`, filters by `collectionId`, and pushes `record` into the chat UI. The user’s question is sent via HTTP (`CollectionService.askQuestion`); the answer is received only via this WebSocket.

### Token lifecycle and WebSocket

- The WebSocket is opened with the **access token** at connect time. There is no automatic refresh of the token on the existing WebSocket connection; if the backend closes the connection when the access token expires, the client will reconnect (and would use whatever access token is current at that time, e.g. after a refresh triggered elsewhere, like the guard).

---

## Features Overview

- **Landing**: Public home.
- **Login / Register**: Email/password auth; on success, tokens and user stored, redirect to `/collections`.
- **Collections** (protected): List of RAG collections; uses `CollectionService.getAllCollections()` and auth guard.
- **Collection** (protected): Single collection view:
  - Loads collection details and documents via `CollectionService`.
  - Loads chat history via `CollectionService.getChatHistory()`.
  - Sends questions via `CollectionService.askQuestion()` (HTTP).
  - Receives answers via `WebsocketService.askResult$` (STOMP), filtered by collection id.
  - Can upload documents via `CollectionService.addDocument()`.
- **Create collection** (protected): Add a new collection (name); then refresh collections and user stats.
- **Settings** (protected): User profile and account actions via `UserService` (e.g. getMe, updateUser, changePassword).

All protected routes use the same auth guard and thus the same session-restoration logic (refresh token) before allowing access.

---

## Environment & Proxy

- **Development**: `src/environments/environment.development.ts` sets `apiUrl: 'http://localhost:8080'`. No path prefix.
- **Production**: `src/environments/environment.ts` sets `apiUrl: 'https://localhost/api'` (or your production API base).

`api-config.ts` reads `environment.apiUrl` for `API_BASE_URL` and builds `WS_BASE_URL` by appending `/ws`.

**Proxy** (`proxy.conf.json`): Used by `ng serve` to proxy `/api` to `https://localhost/api` with path rewrite. So when the app is served by Angular dev server and calls go to `/api`, they are forwarded to the backend. Development can still use `environment.development.ts` with `http://localhost:8080` for direct backend URLs if you prefer.

---

## Docker

The repo includes a multi-stage **Dockerfile**:

1. **Build**: Node LTS Alpine → `npm install` → `npm run build` (production). Note: the script uses `--prod`; with Angular 21 you may use `npm run build` with production configuration (see `angular.json`).
2. **Run**: Copy `dist/Frontend/browser` into a second stage; serve with `serve` on port **5000**.

So the container serves the built static frontend only; the browser still talks to your API and WebSocket endpoints as configured in the environment (e.g. same host or separate API URL).

---

## Scripts Reference

| Script     | Command        | Description                          |
|-----------|----------------|--------------------------------------|
| `start`   | `ng serve`     | Dev server (proxy from angular.json) |
| `build`   | `ng build`     | Production build → `dist/`           |
| `watch`   | `ng build --watch --configuration development` | Development watch build |
| `test`    | `ng test`      | Unit tests (Vitest)                  |

---

## Summary

- **Auth**: Access token for API and WebSocket; refresh token stored and used by the guard to restore session (e.g. after reload).
- **HTTP**: Auth interceptor adds `Bearer <accessToken>` to API requests.
- **Guards**: Protected routes use the auth guard, which tries `tryRefreshFromStoredToken()` before redirecting to `/login`.
- **WebSocket**: STOMP over SockJS to `WS_BASE_URL`, with `Authorization: Bearer <accessToken>` in connect headers; subscription to `/user/queue/ask-result` for RAG answers; collection page consumes `askResult$` and shows them in the correct collection chat.

For backend contract details (login/refresh payloads, RAG endpoints, WebSocket message shape), refer to the backend API and STOMP documentation.
