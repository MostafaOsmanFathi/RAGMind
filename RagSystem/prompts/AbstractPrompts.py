from abc import ABC, abstractmethod
from typing import List


class AbstractPrompts(ABC):
    def __init__(self):
        pass


    @abstractmethod
    def query_hypothetical_answers(self,question) -> str:
        pass


    @abstractmethod
    def multiple_query(self,question,expand_by=3) -> str:
        pass

    @abstractmethod
    def answer_context(self,question,context: str) -> str:
        pass

    @staticmethod
    def _parse_prompt(prompt: str, question:str) -> List[dict]:
        return [{'role': 'system', 'content': prompt}, {'role': 'user', 'content': question}]
