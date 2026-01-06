from RagSystem.embade_functions.AbstractEmbed import AbstractEmbed
import os
from chromadb.utils import embedding_functions

class OllamaEmbed(AbstractEmbed):
    def __init__(self,llm_model_name , ollama_url: str = None):
        super().__init__(llm_model_name)
        self.ollama_url = ollama_url

        if self.ollama_url is None:
            self.embedding_fn=embedding_functions.OllamaEmbeddingFunction(model_name=self.llm_model_name)
        else:
            self.embedding_fn=embedding_functions.OllamaEmbeddingFunction(model_name=self.llm_model_name,url=self.ollama_url)

    def get_chromadb_embed_function(self) -> embedding_functions.EmbeddingFunction:
        if self.embedding_fn is None:
            raise Exception('OllamaEmbed not implemented')
        return self.embedding_fn
