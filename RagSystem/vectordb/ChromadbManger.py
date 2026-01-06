import chromadb
from . import vectordb_utils
from .ChromadbClient import ChromadbClient
import os


class ChromadbManger:
    def __init__(self,db_collection_name: str,n_results_query:int=2,embedding_fun=None):
        self.db_collection_name = db_collection_name
        self.n_results_query = n_results_query
        self.embedding_fun = embedding_fun

        self.chroma_client = ChromadbClient().load_client()

        if embedding_fun is None:
            self.collection = self.chroma_client.get_or_create_collection(self.db_collection_name)
        else:
            self.collection = self.chroma_client.get_or_create_collection(self.db_collection_name,embedding_function=embedding_fun)



    def check_doc_exists(self, document_path: str,file_hash_id:str) -> bool:


        existing_docs = self.collection.get(include=["metadatas"])
        already_exists = False
        for metadata in existing_docs['metadatas']:
            if metadata['file_hash_id'] == file_hash_id:
                already_exists = True
                break

        if already_exists:
            print(f"Document {document_path} already exists with hash_id {file_hash_id}")

        return already_exists

    def add_document(self, document_path: str) -> bool:
        try:
            file_hash_id = vectordb_utils.generate_file_id(document_path)
            file_name = os.path.basename(document_path)

            if self.check_doc_exists(document_path,file_hash_id):
                return True

            with open(document_path, 'r', encoding='utf-8') as f:
                text = f.read()

            chunks = vectordb_utils.chunk_document(text, file_hash_id)

            # Upsert to Chroma
            self.collection.upsert(
                ids=[chunk['id'] for chunk in chunks],
                documents=[chunk['text'] for chunk in chunks],
                metadatas=[{
                    'file_name': file_name,
                    'file_path': document_path,
                    'chunk_id': chunk['id'],
                    'file_hash_id': file_hash_id
                } for chunk in chunks]
            )
            print(f"Inserted {len(chunks)} chunks from {file_name} with hash_id {file_hash_id}")
            return True
        except Exception as e:
            print("Error inserting document:", e)
            return False

    def query_document(self, question: str):
        results = self.collection.query(
            query_texts=[question],
            n_results=self.n_results_query,
            include=['documents', 'metadatas']  # remove deprecated 'ids'
        )

        relevant_chunks = [
            f"{meta['file_name']}: {doc}" for doc, meta in zip(results['documents'][0], results['metadatas'][0])
        ]

        return relevant_chunks