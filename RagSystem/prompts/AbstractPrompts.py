from abc import ABC, abstractmethod


class AbstractPrompts(ABC):
    def __init__(self):
        pass


    @abstractmethod
    def _query_hypothetical_answers(self) -> str:
        pass


    @abstractmethod
    def _multiple_query_answers(self,expand_by=3) -> str:
        pass

    @abstractmethod
    def _answer_context(self,context: str) -> str:
        pass
