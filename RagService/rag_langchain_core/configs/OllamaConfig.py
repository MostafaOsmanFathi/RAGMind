import os

from langchain_ollama import ChatOllama, OllamaEmbeddings
from langchain_text_splitters import TokenTextSplitter

from configBase import ConfigBase

class OllamaConfig(ConfigBase):
    def __init__(self, model_name:str='mistral',validate_model_on_init:bool=True):
        text_splitter = TokenTextSplitter(chunk_size=500, chunk_overlap=100)

        llm = ChatOllama(model=model_name,
                         validate_model_on_init=validate_model_on_init,
                         temperature=.8,
                         num_predict=256,
                         base_url=os.getenv('OLLAMA_URL','http://localhost:11434/')
                         )

        embeddings = OllamaEmbeddings(model="nomic-embed-text")

        super().__init__(llm,embeddings,text_splitter)