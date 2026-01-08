FROM ollama/ollama:latest

COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

EXPOSE 11434

VOLUME ["/root/.ollama"]

ENTRYPOINT ["/entrypoint.sh"]
CMD ["nomic-embed-text", "mistral", "phi3"]
