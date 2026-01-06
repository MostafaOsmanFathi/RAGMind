from abc import ABC, abstractmethod


class AbstractPrompts(ABC):
    def __init__(self):
        pass

    @abstractmethod
    def _query_answers(self, question: str)->str:
        pass


    @abstractmethod
    def _query_hypothetical_answers(self, question: str) -> str:
        pass


    @abstractmethod
    def _multiple_query_answers(self, question: str) -> str:
        pass

    @abstractmethod
    def _answer_context(self, question:str,context: str) -> str:
        pass
