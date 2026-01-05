import os
import chromadb
from openai import OpenAI
from chromadb.utils import embedding_functions
import hashlib


class RagSystem:
    db_collection_root = 'chromadb_root'

    def __init__(self, db_collection_name: str, api_key: str):
        self.db_collection_name = db_collection_name
        self.api_key = api_key
        self.openai_ef = embedding_functions.OpenAIEmbeddingFunction(api_key=api_key,
                                                                     model_name="text-embedding-3-small")

        self.chroma_client = chromadb.PersistentClient(path=f'{RagSystem.db_collection_root}')
        self.collection = self.chroma_client.get_or_create_collection(self.db_collection_name)

        self.llm_client = OpenAI(api_key=self.api_key)
        self.n_results_query = 2

    @classmethod
    def generate_file_id(cls, file_path: str) -> str:
        """
        Compute a short, deterministic file ID.
        """
        hash_func = hashlib.md5()  # fast, small
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                hash_func.update(chunk)
        return hash_func.hexdigest()  # 32-char hex string

    def _chunk_document(self, doc: str, file_id: str, chunk_size: int = 150, chunk_overlap: int = 15) -> list[dict]:
        words = doc.split()
        chunks = []
        step = chunk_size - chunk_overlap
        for cnt, i in enumerate(range(0, len(words), step)):
            chunk_text = " ".join(words[i:i + chunk_size])
            chunks.append({'id': f'{file_id}_chunk_{cnt + 1}', 'text': chunk_text})
        return chunks

    def _embed_document(self, chunk_text):
        response = self.llm_client.embeddings.create(input=chunk_text, model="text-embedding-3-small")
        embedding = response.data[0].embedding
        return embedding

    def add_document(self, document_path: str) -> bool:
        try:
            file_hash_id = RagSystem.generate_file_id(document_path)
            file_name = os.path.basename(document_path)

            with open(document_path, 'r', encoding='utf-8') as f:
                text = f.read()

            chunks = self._chunk_document(text, file_hash_id)
            for chunk in chunks:
                chunk['embedding'] = self._embed_document(chunk['text'])

            self.collection.upsert(
                ids=[chunk['id'] for chunk in chunks],
                documents=[chunk['text'] for chunk in chunks],
                embeddings=[chunk['embedding'] for chunk in chunks],
                metadatas=[{'file_name': file_name, 'file_path': document_path, 'chunk_id': chunk['id'],
                            'file_hash_id': file_hash_id} for chunk in
                           chunks]
            )
            print(f"Inserted {len(chunks)} chunks from {file_name} with hash_id {file_hash_id}")

            return True
        except Exception as e:
            print(e)
            return False

    def _query_document(self, question: str):
        query_embedding = self._embed_document(question)
        results = self.collection.query(
            query_embeddings=[query_embedding],
            n_results=self.n_results_query,
            include=['documents', 'metadatas', 'ids']
        )
        # relevant_chunks = [doc for sublist in results["documents"] for doc in sublist]
        relevant_chunks = [
            f"{meta['file_name']}: {doc}" for doc, meta in zip(results['documents'][0], results['metadatas'][0])
        ]

        return relevant_chunks

    def prompt_llm(self, question: str, context: str) -> str:
        # System prompt: instructions to stay grounded in the retrieved documents
        prompt = f"""
    You are an AI assistant with access to private documents. Answer user questions **using ONLY the content provided below**.
    Follow these rules:
    1. Use only the information provided in the documents. Do not make assumptions or use external knowledge.
    2. If the answer is not found in the documents, say: "I could not find relevant information in the documents."
    3. Provide concise, clear, and accurate answers.
    4. You may reference the source or file name if useful.

    Context / Relevant document chunks:
    {context}
    """

        # noinspection PyTypeChecker
        response = self.llm_client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {
                    "role": "system",
                    "content": prompt
                },
                {
                    "role": "user",
                    "content": question
                }
            ]
        )

        return response.choices[0].message["content"]

    def ask_question(self, question: str) -> str:
        try:
            relevant_chunks = self._query_document(question)

            llm_response = self.prompt_llm(question, "\n".join(relevant_chunks))
            return llm_response
        except Exception as e:
            print(e)
            return ""



