/** Request for POST /rag/collections/{collectionId}/queries/ask (AskRabbitmqRequestDto). */
export interface AskRagRequest {
  backendId?: string;
  userId?: string;
  collectionName?: string;
  llmModel?: string;
  embedModel?: string;
  question: string;
  options?: Record<string, unknown>;
}

/** RAG ask result from WebSocket /user/queue/ask-result (RagFeedbackResponseDto). */
export interface RagAskResult {
  backend_id?: string;
  user_id?: string;
  status?: string;
  llm_model?: string;
  collection_name?: string;
  response?: string;
  taskId?: string;
}
