from abc import ABC, abstractmethod
from chromadb.utils import embedding_functions

class AbstractEmbed(ABC):
    def __init__(self,llm_model_name):
        self.llm_model_name = llm_model_name
        self.embedding_fn=None

    @abstractmethod
    def get_chromadb_embade_function(self)-> embedding_functions.EmbeddingFunction:
        return self.embedding_fn