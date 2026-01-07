#!/bin/bash

ollama serve &

sleep 2

ollama pull mistral
ollama pull phi-3
ollama pull nomic-embed-text
