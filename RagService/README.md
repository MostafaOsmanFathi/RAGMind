<div align="center">

![RAG](./../docs/favicon.svg)

# RAG Worker System

</div>

This component provides a **containerized RAG (Retrieval-Augmented Generation) Worker System** that handles document ingestion and query processing for LLMs.
It is an **infrastructure-level service** that works alongside RabbitMQ, Ollama, and the database to provide scalable question-answering and document management capabilities.

---

## Overview

The RAG Worker System is designed to:

* Consume tasks from RabbitMQ queues
* Handle concurrent RAG queries and document ingestion
* Interface with local LLM models (Mistral, Phi3) and embedding functions (NomicEmbed)
* Store and query vectorized document embeddings using ChromaDB
* Provide feedback to the backend on task success or failure

---

## Main Flows

The system has **two main worker threads**, each handling a separate type of task:

### 1. RAG Ask Flow

1. Listen to the RabbitMQ queue for RAG query tasks.
2. Upon receiving a task:

   * Validate the message format.
   * Load the ChromaDB collection corresponding to the request (user-specific isolation).
   * Start the RAG pipeline (detailed below).
   * Get the response from the LLM pipeline.
   * Send the response back via the RabbitMQ feedback exchanger.

### 2. Document Task Flow

1. Listen to a separate RabbitMQ queue for document ingestion tasks.
2. Upon receiving a task:

   * Verify if the document exists in the shared storage.
   * If present, process it through the RAG pipeline.
   * Send the task response via the RabbitMQ feedback exchanger.

---

## File and Collection Management

To maintain **isolation and prevent conflicts**:

* For each request, extract the following identifiers:

  * `user_email`
  * `collection_id`
  * `LLM model name`
  * `embedding model name`
* Combine these into a string and generate a **hash**. This hash is used as the **collection name**.
* Shared storage is organized as follows:

  * Each user has a folder.
  * Inside each user folder, subfolders are created for each collection (named by the hash).
  * This ensures no path conflicts or overwrites even if collection names are repeated across users.
* Documents are retrieved from the backend through the shared storage system.

---

## Startup Configuration

* The container supports specifying **concurrent workers** at startup:

  * `concurrent_raq_workers` (default: 3)
  * `concurrent_doc_workers` (default: 10)
* Arguments can be passed via the command line or CMD in Dockerfile:

```
CMD ["3", "10"]
```

* The system loads environment variables from a `.env` file for runtime configuration.

---

## Worker Tasks

### RAG Query Worker

* Receives `RAGWorkerMessage` from RabbitMQ
* Selects embedding function and LLM model
* Queries the vector database and generates responses
* Sends `RAGFeedback` to the backend
* Handles success/failure acknowledgments

### Document Worker

* Receives `DocumentWorkerMessage` from RabbitMQ
* Adds documents to the vector database via RAG system
* Sends `DocFeedback` to backend
* Handles success/failure acknowledgments

---

## System Components

| Component                | Purpose                                                        |
| ------------------------ | -------------------------------------------------------------- |
| `RagSystem`              | Core class managing embedding, LLM, and vector DB interactions |
| `ChromadbManger`         | Handles vector storage and querying                            |
| `NomicEmbed`             | Provides text embedding functions                              |
| `MistralLLM` / `Phi3LLM` | LLM models for text generation and reasoning                   |
| `DefaultPrompt`          | Manages prompt templates and query expansion                   |
| RabbitMQ                 | Message broker for task distribution                           |

---

## Docker Image

### Base Image

* `python:3.12.12`

### Key Characteristics

* Installs Python dependencies from `requirements.txt`
* Supports environment configuration for shared storage and ChromaDB paths
* Entrypoint runs the main worker script (`main.py`)
* Persists vector DB and shared file storage via volumes

### Environment Variables

| Variable                          | Purpose                              |
| --------------------------------- | ------------------------------------ |
| `SHARED_STORAGE_File_PATH`        | Location for shared file uploads     |
| `CHROMADB_PERSISTENT_CLIENT_PATH` | Path for persistent ChromaDB storage |
| `PYTHONUNBUFFERED`                | Ensures real-time logging            |

### Docker Volumes

* `SHARED_STORAGE_File_PATH`
* `CHROMADB_PERSISTENT_CLIENT_PATH`

---

## Operational Notes

* Designed to scale horizontally via multiple workers
* Integrates seamlessly with RabbitMQ for task distribution
* Interfaces with Ollama container for local LLM execution
* Feedback mechanism ensures backend can track task results
* Provides **user-level collection isolation** using hashed folder structure
* Suitable for staging and production deployments with proper resource planning

---

## Summary

The RAG Worker System is a **robust, containerized framework** for document ingestion and LLM-based query processing. It abstracts the complexity of embedding, vector DB management, and LLM inference while providing reliable feedback and task orchestration in a scalable microservice environment.
