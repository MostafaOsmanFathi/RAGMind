from langchain_ollama import ChatOllama, OllamaEmbeddings
from langchain_text_splitters import TokenTextSplitter

from .configBase import ConfigBase

#TODO need to be tested
class OllamaCloudConfig(ConfigBase):
    def __init__(self, model_name:str='gpt-oss:120b-cloud', validate_model_on_init:bool=True, small_llm:str='phi-3-mini-cloud'):
        text_splitter = TokenTextSplitter(chunk_size=500, chunk_overlap=100)

        llm = ChatOllama(
            model=model_name,
            validate_model_on_init=validate_model_on_init,
            temperature=0.8,
            num_predict=512,
            base_url='https://api.ollama.com',
        )

        embeddings = OllamaEmbeddings(
            model="nomic-embed-text-7b-cloud"
        )

        if small_llm is not None:
            small_llm = ChatOllama(
                model=small_llm,
                validate_model_on_init=validate_model_on_init,
                temperature=0.8,
                num_predict=256,
                base_url='https://api.ollama.com'
            )

        super().__init__(llm, embeddings, text_splitter, small_llm)
