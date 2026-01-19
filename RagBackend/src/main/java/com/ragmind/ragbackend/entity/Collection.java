package com.ragmind.ragbackend.entity;

import com.ragmind.ragbackend.dto.CollectionDto;
import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;

import java.util.List;

@Entity
@Table(name = "collection")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<CollectionChat> chatHistory;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<CollectionDocuments> collectionDocuments;

    @Column(name = "number_of_docs")
    private Integer numberOfDocs;

    @Column(name = "collection_name")
    private String collectionName;

    public static CollectionDto toDto(@NonNull Collection collection) {
        return new CollectionDto(collection.getId(), collection.getCollectionName(), collection.getNumberOfDocs());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CollectionChat> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<CollectionChat> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public List<CollectionDocuments> getCollectionDocuments() {
        return collectionDocuments;
    }

    public void setCollectionDocuments(List<CollectionDocuments> collectionDocuments) {
        this.collectionDocuments = collectionDocuments;
    }

    public Integer getNumberOfDocs() {
        return numberOfDocs;
    }

    public void setNumberOfDocs(Integer numberOfDocs) {
        this.numberOfDocs = numberOfDocs;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
