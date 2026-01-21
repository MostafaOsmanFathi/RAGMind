package com.ragmind.ragbackend.dto;

import java.util.Date;

public class NotificationDTO {

    private Long id;
    private Long userId;
    private String notificationMessage;
    private Date creationDate;

    public NotificationDTO() {
    }

    public NotificationDTO(Long id, Long userId, String notificationMessage, Date creationDate) {
        this.id = id;
        this.userId = userId;
        this.notificationMessage = notificationMessage;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
