FROM docker.n8n.io/n8nio/n8n:latest

LABEL org.opencontainers.image.authors="Mostafa Osman <mostafa.osman.fathi@gmail.com>" \
      org.opencontainers.image.source="https://github.com/MostafaOsmanFathi/RAGMind" \
      org.opencontainers.image.version="v1.10" \
      org.opencontainers.image.licenses="MIT"

LABEL org.opencontainers.image.title="ragmind-n8n" \
      org.opencontainers.image.description="Custom n8n automation service with preloaded and auto-activated workflows for RAGMind"

USER root
COPY ./workflows/RagMindWorkFlow.json /workflows/RagMindWorkFlow.json
RUN chown -R node:node /workflows

USER node

VOLUME ["/home/node/.n8n"]

ENTRYPOINT ["sh", "-c", "n8n import:workflow --input=/workflows/RagMindWorkFlow.json --activate && n8n start"]
