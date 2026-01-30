<div align="center">

![Ollama](https://ollama.com/public/ollama.png)

# Ollama Model Runtime

</div>

This component provides a **containerized Ollama runtime** responsible for **serving and managing local LLM models**.
It is an **infrastructure and platform-level service**, not a business-logic component, and is used by other services (e.g. backend APIs) to perform inference or embedding generation.

---

## Overview

The Ollama runtime container is designed to:

* Run the Ollama model server
* Automatically pull required models at startup
* Persist downloaded models across container restarts
* Expose a local HTTP API for inference and embeddings

This setup ensures that model availability is **deterministic, reproducible, and environment-agnostic**.

---

## Responsibilities

* Hosting LLMs locally using Ollama
* Managing model lifecycle (pull-on-start)
* Exposing a stable inference endpoint
* Decoupling model runtime from application services

---

## Startup Flow

```
Container Start
  │
  ├── Start Ollama server (background)
  │
  ├── Wait until Ollama API is ready
  │
  ├── Pull required models (if not present)
  │
  └── Keep Ollama server running (foreground)
```

---

## Default Models

By default, the container is configured to ensure the following models are available:

* `nomic-embed-text` (text embeddings)
* `mistral` (general-purpose LLM)
* `phi3` (lightweight reasoning model)

Additional or alternative models can be specified at runtime via container arguments.

---

## Entrypoint Behavior

The custom entrypoint script performs the following actions:

1. Starts the Ollama server in the background
2. Polls the Ollama API until it becomes available
3. Pulls only the models passed as arguments
4. Keeps the Ollama process alive as the main container process

This ensures:

* No race conditions on startup
* Models are always present before serving requests
* Graceful container lifecycle management

---

## Networking

* **Exposed port:** `11434`
* Provides an HTTP API consumed by internal services
* Intended for **internal network access only** (not public-facing)

---

## Data Persistence

Model data is persisted using a Docker volume:

```
/root/.ollama
```

This prevents re-downloading models on every container restart and significantly reduces startup time.

---

## Docker Image

### Base Image

* `ollama/ollama:latest`

### Key Characteristics

* Lightweight runtime focused on local inference
* Supports CPU and GPU acceleration (environment-dependent)
* Designed for containerized and orchestrated environments

---

## Operational Notes

* Intended to run behind a backend service (e.g. RAG or inference API)
* Should not be exposed directly to the public internet
* Model selection should be controlled via deployment configuration
* Suitable for development, staging, and production (with resource planning)

---

## Summary

This Ollama runtime container provides a reliable and reproducible way to host and manage local LLM models. It abstracts model management away from application code while enabling scalable, low-latency inference within the system architecture.
