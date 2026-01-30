<div align="center">

![PostgreSQL](https://upload.wikimedia.org/wikipedia/commons/2/29/Postgresql_elephant.svg)

# PostgreSQL Database

</div>

This component provides a **containerized PostgreSQL database** that serves as the primary **persistent storage** for the system.
It is an **infrastructure-level service**, not a business-logic component, and is used by other services (e.g., backend APIs) to store and retrieve application data.

---

## Overview

The PostgreSQL container is designed to:

* Provide a reliable relational database
* Persist data across container restarts
* Expose the default PostgreSQL port (`5432`) for internal services
* Support environment-based configuration via build arguments and environment variables

---

## Configuration

| Parameter           | Default Value | Description                      |
| ------------------- | ------------- | -------------------------------- |
| `POSTGRES_DB`       | `appdb`       | Name of the primary database     |
| `POSTGRES_USER`     | `appuser`     | Database user for authentication |
| `POSTGRES_PASSWORD` | `apppassword` | Password for the database user   |

These parameters can be overridden at build or runtime.

---

## Data Persistence

Data is persisted using Docker volumes to ensure durability and prevent data loss on container restarts:

* `/data`
* `/var/lib/postgresql/data`

---

## Networking

* **Exposed port:** `5432`
* Intended for **internal network access only** (backend services)

---

## Docker Image

### Base Image

* `postgres:15.15`

### Key Characteristics

* Production-ready PostgreSQL version 15.15
* Fully configurable via environment variables
* Designed for containerized environments with persistent storage

---

## Operational Notes

* Should run as a **backend-only service**, not exposed to the public internet
* Backups and replication can be configured as needed
* Environment variables should be securely managed

---

## Summary

This PostgreSQL container provides a stable, persistent, and configurable relational database for the system, ensuring reliable data storage and access for all dependent services.
