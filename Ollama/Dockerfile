FROM ollama/ollama:latest

LABEL org.opencontainers.image.authors="Mostafa Osman <mostafa.osman.fathi@gmail.com>" \
      org.opencontainers.image.source="https://github.com/MostafaOsmanFathi/RAGMind" \
      org.opencontainers.image.version="v1.10" \
      org.opencontainers.image.licenses="MIT"

LABEL org.opencontainers.image.title="ollama" \
      org.opencontainers.image.description="Ollama runtime service for running local LLM models used by the RAG system"


COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

EXPOSE 11434

VOLUME ["/root/.ollama"]

ENTRYPOINT ["/entrypoint.sh"]
CMD ["nomic-embed-text", "mistral", "phi3"]
