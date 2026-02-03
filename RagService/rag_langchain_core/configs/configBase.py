import hashlib
import os
from abc import ABC,abstractmethod

from langchain_chroma import Chroma
from langchain_core.documents import Document
from langchain_core.embeddings import Embeddings
from langchain_core.language_models import BaseChatModel
from langchain_text_splitters import TextSplitter


class ConfigBase(ABC):
    def __init__(self,llm:BaseChatModel,embeddings:Embeddings,text_splitter:TextSplitter,small_llm:BaseChatModel=None):
        self.llm = llm
        self.embeddings = embeddings
        self.text_splitter = text_splitter
        self.small_llm = small_llm

        if self.small_llm is None:
            self.small_llm = llm

    def generate_file_id(self,file_path: str) -> str:
        """
        Compute a short, deterministic file ID.
        """
        hash_func = hashlib.md5()  # fast, small
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                hash_func.update(chunk)
        return hash_func.hexdigest()  # 32-char hex string

    def check_doc_exists_lc(self,collection: Chroma, document_path: str) -> bool:
        file_hash_id = self.generate_file_id(document_path)
        existing_docs = collection.get(include=["metadatas"])

        for metadata in existing_docs['metadatas']:
            # metadata is a dict with your custom fields
            if metadata is not None and metadata.get('file_hash_id') == file_hash_id:
                print(f"Document {document_path} already exists with hash_id {file_hash_id}")
                return True

        return False

    def chunks_meta_data(self,obj):
        obj["chunks"] = [
            Document(
                page_content=chunk,
                metadata={
                    "file_name": obj["file_name"],
                    "file_path": obj["file_path"],
                    "file_hash_id": obj["file_hash_id"],
                    "chunk_id": idx,
                },
            )
            for idx, chunk in enumerate(obj["chunks"])
        ]
        return obj

    def add_documents_to_store(self,obj):
        if obj["chunks"]:
            obj['vector_store'].add_documents(obj["chunks"])
        return obj

    def get_file_hash(self,obj):
        obj["file_hash_id"] = self.generate_file_id(obj["file_path"])
        return obj

    def check_exists(self,obj):
        obj["exists"] = self.check_doc_exists_lc(obj['vector_store'], obj["file_path"])
        return obj

    def extract_file_name(self,obj):
        obj["file_name"] = os.path.basename(obj["file_path"])
        return obj

    def split_into_chunks(self,obj):
        if not obj["exists"]:
            with open(obj["file_path"], "r") as f:
                content = f.read()
            obj["chunks"] = self.text_splitter.split_text(content)
        else:
            obj["chunks"] = []
        return obj

