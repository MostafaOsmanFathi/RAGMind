from abc import ABC, abstractmethod
from chromadb.utils import embedding_functions

class AbstractEmbade(ABC):
    def __init__(self,llm_url,llm_model_name,llm_api_key):
        self.llm_url = llm_url
        self.llm_model_name = llm_model_name
        self.llm_api_key = llm_api_key


    @abstractmethod
    def get_chromadb_embade_function(self)-> embedding_functions.EmbeddingFunction:
        pass