FROM python:3.12.12

ARG CHROMADB_PATH=/app/chromadb/data/

ARG SHARED_STORAGE_File_PATH=/app/shared/files

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir --upgrade pip \
    && pip install --no-cache-dir -r requirements.txt

COPY prebuild_tiktoken.py .
RUN python prebuild_tiktoken.py

COPY . ./RagService


ENV SHARED_STORAGE_File_PATH=$SHARED_STORAGE_File_PATH

ENV CHROMADB_PERSISTENT_CLIENT_PATH=$CHROMADB_PATH

ENV PYTHONUNBUFFERED: 1

VOLUME ["$CHROMADB_PATH", "$SHARED_STORAGE_File_PATH"]

ENTRYPOINT ["python3", "-m" ,"RagService.main"]

# concurrent RAG workers, concurrent DOC workers
CMD ["3", "10"]
