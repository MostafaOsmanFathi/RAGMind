FROM postgres:15.15

LABEL org.opencontainers.image.authors="Mostafa Osman <mostafa.osman.fathi@gmail.com>" \
      org.opencontainers.image.source="https://github.com/MostafaOsmanFathi/RAGMind" \
      org.opencontainers.image.version="v1.10" \
      org.opencontainers.image.licenses="MIT"

LABEL org.opencontainers.image.title="pg-database" \
      org.opencontainers.image.description="PostgreSQL database for storing application and RAG metadata"

ARG POSTGRES_DB=appdb
ARG POSTGRES_USER=appuser
ARG POSTGRES_PASSWORD=apppassword

ENV POSTGRES_DB=${POSTGRES_DB}
ENV POSTGRES_USER=${POSTGRES_USER}
ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}

EXPOSE 5432

VOLUME [ "/data" ]

VOLUME ["/var/lib/postgresql/data"]