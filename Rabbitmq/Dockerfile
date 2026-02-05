FROM rabbitmq:3.13-management

LABEL org.opencontainers.image.authors="Mostafa Osman <mostafa.osman.fathi@gmail.com>" \
      org.opencontainers.image.source="https://github.com/MostafaOsmanFathi/RAGMind" \
      org.opencontainers.image.version="v1.10" \
      org.opencontainers.image.licenses="MIT"

LABEL org.opencontainers.image.title="rabbitmq" \
      org.opencontainers.image.description="RabbitMQ message broker for asynchronous communication between RAG services"

EXPOSE 5672 15672

VOLUME ["/var/lib/rabbitmq"]

CMD ["rabbitmq-server"]
