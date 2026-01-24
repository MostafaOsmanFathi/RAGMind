from typing import Optional
from pydantic import BaseModel, Field


class RagInit(BaseModel):
    backend_id: str = Field(alias="backendId")
    user_id: str = Field(alias="userId")
    collection_name: str = Field(alias="collectionName")
    llm_model: str = Field(alias="llmModel")
    embed_model: Optional[str] = Field(None, alias="embedModel")
    taskId: Optional[str] = None


class RAGWorkerMessage(RagInit):
    question: str
    options: dict = Field(default_factory=dict)


class DocumentWorkerMessage(RagInit):
    action: str  # add, remove
    file_path: str = Field(alias="filePath")


class FeedbackBase(BaseModel):
    backend_id: Optional[str] = Field(None, alias="backendId")
    user_id: Optional[str] = Field(None, alias="userId")
    status: Optional[str] = None  # success or failed
    llm_model: Optional[str] = Field(None, alias="llmModel")
    collection_name: Optional[str] = Field(None, alias="collectionName")


class DocFeedback(FeedbackBase):
    action: Optional[str] = None   # add, remove, update
    result: Optional[str] = None
    taskId: str


class RAGFeedback(FeedbackBase):
    response: Optional[str] = None
    taskId: Optional[str] = None
