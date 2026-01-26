package com.ragmind.ragbackend.dto.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentFeedbackResponseDto {

    @JsonProperty("backend_id")
    private String backendId;

    @JsonProperty("user_id")
    private String userId;

    private String status;

    @JsonProperty("llm_model")
    private String llmModel;

    @JsonProperty("collection_name")
    private String collectionName;

    private String action;

    private String result;

    @JsonProperty("taskId")
    private String taskId;

    public DocumentFeedbackResponseDto() {}

    public DocumentFeedbackResponseDto(
            String backendId,
            String userId,
            String status,
            String llmModel,
            String collectionName,
            String action,
            String result,
            String taskId
    ) {
        this.backendId = backendId;
        this.userId = userId;
        this.status = status;
        this.llmModel = llmModel;
        this.collectionName = collectionName;
        this.action = action;
        this.result = result;
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

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    @Override
    public String toString() {
        return "DocumentFeedbackResponseDto{" +
                "backendId='" + backendId + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", llmModel='" + llmModel + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", action='" + action + '\'' +
                ", result='" + result + '\'' +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}
