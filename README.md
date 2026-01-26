# RAGMind

## Project Overview

RAGMind is a private-document chat assistant leveraging Retrieval-Augmented Generation (RAG) to answer questions based on your own documents with AI-powered context understanding.

It allows users to upload documents and ask questions, with responses generated from the content of the documents rather than general AI knowledge.

## Key Features

* Upload and query **text documents** (future support for PDF, DOCX, etc.)
* **AI-powered answers** using retrieval-augmented generation
* **Microservices architecture** with asynchronous communication
* **Vector search** using ChromaDB
* **Embeddings** via nomic-embed-text
* **LLM inference** with Mistral phi-3
* Designed for **high concurrency** with Java Spring Boot
* **JWT Access & Refresh tokens** for secure and efficient user authentication

## Architecture & Scalability

* Microservices architecture decoupled via **RabbitMQ**
* **Scalable to handle a large number of concurrent users** using Java Spring Boot
* **Authentication & Security:**

  * JWT Access Tokens for quick request validation
  * JWT Refresh Tokens for secure session management
* Services Overview:

  * `rag-backend` – Retrieval + generation API
  * `rag-service` – Orchestration of retrieval & generation
  * `rabbitmq` – Message broker for async communication
  * `reverse-proxy` – Routes requests to services
  * **Frontend (future)**: Angular-based chat UI

![Architecture Diagram Placeholder](./docs/architecture_diagram.png)

## Tech Stack

| Layer / Component | Tech / Framework                               |
| ----------------- | ---------------------------------------------- |
| LLM / AI Models   | Mistral phi-3                                  |
| Embeddings        | nomic-embed-text                               |
| Vector Database   | ChromaDB                                       |
| Message Broker    | RabbitMQ                                       |
| Backend           | Java Spring Boot (scalable, JWT Auth) + Python |
| Frontend          | Angular (planned)                              |
| Containerization  | Docker Compose                                 |
| API Documentation | [See API Docs](./RagBackend/API.md)            |

## Setup / Dev Environment

* **Docker Compose** based setup for all microservices
* Services connected via RabbitMQ for async messaging
* Backend APIs documented via **Swagger** ([link to Swagger docs](./RagBackend/API.md))

## Services / Modules

* Each service has its own README with setup and usage details:

  * [RagBackend README](./RagBackend/README.md)
  * [RagService README](./RagService/README.md)
  * [ReverseProxy README](./ReverseProxy/README.md)
  * [RabbitMQ README](./RabbitMQ/README.md)

## Usage

* Upload **text documents**
* Ask questions and get answers based on your documents
* Future updates will support multi-format uploads and a full Angular frontend

## Future Improvements

* Support for **PDF, DOCX, and other document formats**
* Complete **Angular-based frontend**
* Additional LLM and embedding model support
