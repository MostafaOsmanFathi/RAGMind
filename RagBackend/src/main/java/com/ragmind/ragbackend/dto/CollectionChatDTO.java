package com.ragmind.ragbackend.dto;

import java.time.LocalDate;

public class CollectionChatDTO {

    private Long id;
    private Long collectionId;
    private String message;
    private String role;
    private LocalDate date;

    public CollectionChatDTO() {
    }

    public CollectionChatDTO(Long id, Long collectionId, String message, String role, LocalDate date) {
        this.id = id;
        this.collectionId = collectionId;
        this.message = message;
        this.role = role;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
