from typing import Optional

from pydantic import BaseModel, Field


class RagInit(BaseModel):
    backend_id: str
    user_id: str
    collection_name: str
    llm_model: str
    embed_model: Optional[str] = None


class RAGWorkerMessage(RagInit):
    question: str
    options: dict = Field(default_factory=dict)

class DocumentWorkerMessage(RagInit):
    action: str # add, remove
    file_path:str


class FeedbackBase(BaseModel):
    backend_id: Optional[str] = None
    user_id: Optional[str] = None
    status: Optional[str] = None  # success or failed
    llm_model: Optional[str] = None
    collection_name: Optional[str] = None

class DocFeedback(FeedbackBase):
    action: Optional[str] = None   # add, remove, update
    result: Optional[str] = None

class RAGFeedback(FeedbackBase):
    response: Optional[str] = None
