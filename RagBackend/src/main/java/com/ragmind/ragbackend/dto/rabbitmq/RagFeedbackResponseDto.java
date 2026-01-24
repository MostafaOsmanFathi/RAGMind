package com.ragmind.ragbackend.dto.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RagFeedbackResponseDto {

    @JsonProperty("backend_id")
    private String backendId;

    @JsonProperty("user_id")
    private String userId;

    private String status;

    @JsonProperty("llm_model")
    private String llmModel;

    @JsonProperty("collection_name")
    private String collectionName;

    private String response;

    private String taskId;

    public RagFeedbackResponseDto() {}

    public RagFeedbackResponseDto(String backendId, String userId, String status, String llmModel,
                          String collectionName, String response, String taskId) {
        this.backendId = backendId;
        this.userId = userId;
        this.status = status;
        this.llmModel = llmModel;
        this.collectionName = collectionName;
        this.response = response;
        this.taskId = taskId;
    }

    public String getBackendId() { return backendId; }
    public void setBackendId(String backendId) { this.backendId = backendId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLlmModel() { return llmModel; }
    public void setLlmModel(String llmModel) { this.llmModel = llmModel; }

    public String getCollectionName() { return collectionName; }
    public void setCollectionName(String collectionName) { this.collectionName = collectionName; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    @Override
    public String toString() {
        return "ChatMessageDto{" +
                "backendId='" + backendId + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", llmModel='" + llmModel + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", response='" + response + '\'' +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}
