from abc import ABC, abstractmethod


class AbstractLLM(ABC):
    def __init__(self,llm_url,llm_model_name,llm_api_key):
        self.llm_url = llm_url
        self.llm_model_name = llm_model_name
        self.llm_api_key = llm_api_key


    @abstractmethod
    def chat_query(self,message: list[dict]) -> str:
        pass

    @abstractmethod
    def generate_query(self,message: list[dict]) -> str:
        pass