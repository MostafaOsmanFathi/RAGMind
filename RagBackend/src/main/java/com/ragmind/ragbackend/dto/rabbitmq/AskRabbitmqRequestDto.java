package com.ragmind.ragbackend.dto.rabbitmq;

import java.util.HashMap;
import java.util.Map;

public class AskRabbitmqRequestDto extends RagInitDto {
    private String question;
    private Map<String, Object> options = new HashMap<>();
    private String taskId;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
