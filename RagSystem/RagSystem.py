import os
import chromadb
import ollama
from chromadb.utils import embedding_functions
import hashlib
from ollama import chat

class RagSystem:
    db_collection_root = 'chromadb_root'

    def __init__(self, db_collection_name: str,n_results_query:int=2):
        self.db_collection_name = db_collection_name

        self.chroma_client = chromadb.PersistentClient(path=f'{RagSystem.db_collection_root}')
        self.collection = self.chroma_client.get_or_create_collection(self.db_collection_name)
        self.llm_client=ollama.Client()

        self.n_results_query = n_results_query

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

    def add_document(self, document_path: str) -> bool:
        try:
            file_hash_id = RagSystem.generate_file_id(document_path)
            file_name = os.path.basename(document_path)

            with open(document_path, 'r', encoding='utf-8') as f:
                text = f.read()

            existing_docs = self.collection.get(include=["metadatas"])
            already_exists = False
            for metadata in existing_docs['metadatas']:
                if metadata['file_hash_id'] == file_hash_id:
                        already_exists = True
                        break
                if already_exists:
                    break

            if already_exists:
                print(f"=====Document {document_path} already exists with hash_id {file_hash_id}")
                return True

            chunks = self._chunk_document(text, file_hash_id)

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

    def _query_document(self, question: str):
        results = self.collection.query(
            query_texts=[question],
            n_results=3,
            include=['documents', 'metadatas']  # remove deprecated 'ids'
        )

        relevant_chunks = [
            f"{meta['file_name']}: {doc}" for doc, meta in zip(results['documents'][0], results['metadatas'][0])
        ]

        return relevant_chunks


    def _query_llm_model(self,message:list[dict]):
        response = chat(
            model="mistral",
            messages=message,
            options={
                "num_ctx": 2048,  # reduce KV cache cost
                "num_predict": 128,  # limit output length
                "batch_size": 128,  # CPU-friendly
                "temperature": 0.7
            }
        )
        return response["message"].get("content")

    def _prompt_llm(self, question: str, context: str) -> str:
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
        result=self._query_llm_model([{'role':'system','content':prompt},{'role':'user','content':question}])
        return result


    def _multiple_query_expansion(self, question: str,expand_by=3) -> str:
        query_expansion_prompt = f"""
        You are an assistant that rewrites user queries to improve retrieval from a document collection. 
        Your task is to expand the query with context cues so that the search system retrieves the most relevant documents, including small or overlooked documents.

        Rules:
        1. Preserve the original intent of the query.
        2. Add context keywords, document types, or hints to make it clear which document is most relevant.
        3. Include synonyms, related terms, or clarifying details that help match the right document.
        4. Keep the expanded query concise and relevant — no extra unrelated words.
        5. Output only the expanded query, nothing else.
        6. You will make exactly {expand_by} extra queries.
        
        User query: "{question}"
        """
        result=self._query_llm_model([
            {'role': 'system', 'content':query_expansion_prompt},
            {'role': 'user', 'content':'answer on system query'}
        ])
        return result


    def _query_hypothetical_answers(self, question: str) -> str:
        query_hypothetical_answers_prompt = f"""
        You are an AI assistant tasked with generating multiple plausible ways topics or concepts could be described in documents. 
        Do not provide a definitive answer. Instead, imagine **all possible contexts, variations, or phrasings** in which the information might appear.

        You will be given **a list of questions or topics**. For each one, generate 2 hypothetical answers in the same order.

        Rules:
        1. For each topic or question, provide **exactly 2 different hypothetical answers** that could plausibly exist in documents.
        2. Use synonyms, alternate phrasings, and related concepts when appropriate.
        3. Return only the hypothetical answers as separate bullet points or lines, **grouped by question in order**.
        """

        result=self._query_llm_model([{'role':'system','content':query_hypothetical_answers_prompt},{'role':'user','content':question}])
        return result

    def ask_question(self, question: str) -> str:
        try:

            multiple_queries=question+self._multiple_query_expansion(question)

            answers=self._query_hypothetical_answers(multiple_queries)

            search_with=multiple_queries+' \n'+answers

            relevant_chunks = self._query_document(search_with)

            llm_response = self._prompt_llm(question, "\n".join(relevant_chunks))
            return llm_response

        except Exception as e:
            raise e
