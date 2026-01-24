package com.ragmind.ragbackend.dto.rabbitmq;

public class DocumentRabbitmqRequestDto extends RagInitDto {
    private String action;
    private String filePath;
    private String taskId;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}