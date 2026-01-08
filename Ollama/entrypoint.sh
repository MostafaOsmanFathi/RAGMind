#!/bin/bash
set -e

MODELS="$@"

# Start Ollama server in background
ollama serve &
OLLAMA_PID=$!

# Wait until Ollama is ready
until ollama list >/dev/null 2>&1; do
  echo "Waiting for Ollama server..."
  sleep 1
done

# Pull only requested models
for model in $MODELS; do
  echo "Ensuring model: $model"
  ollama pull "$model"
done

# Keep Ollama in foreground
wait $OLLAMA_PID
