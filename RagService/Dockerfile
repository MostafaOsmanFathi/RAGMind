FROM python:3.12.12

ARG CHROMADB_PATH=/app/chromadb/data/

ARG SHARED_STORAGE_File_PATH=/app/shared/files


COPY requirements.txt .

RUN pip install -r requirements.txt

COPY . .


ENV SHARED_STORAGE_File_PATH=$SHARED_STORAGE_File_PATH

ENV CHROMADB_PERSISTENT_CLIENT_PATH=$CHROMADB_PATH

VOLUME ["$CHROMADB_PATH", "$SHARED_STORAGE_File_PATH"]

ENTRYPOINT ["python3", "main.py"]

# concurrent RAG workers, concurrent DOC workers
CMD ["3", "10"]
