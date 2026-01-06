from abc import ABC, abstractmethod


class AbstractLLM(ABC):
    def __init__(self,llm_model_name):
        self.llm_model_name = llm_model_name

    @abstractmethod
    def chat_query(self,message: list[dict]) -> str:
        pass

    @abstractmethod
    def generate_query(self,prompt: str) -> str:
        pass