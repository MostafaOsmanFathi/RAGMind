package com.ragmind.ragbackend.model;

import jakarta.persistence.*;

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


    @OneToMany(mappedBy = "collection",cascade = CascadeType.ALL)
    private List<CollectionChat> chatHistory;

    @OneToMany(mappedBy = "collection",cascade = CascadeType.ALL)
    private List<CollectionDocuments> collectionDocuments;

    @Column(name = "number_of_docs")
    private Integer numberOfDocs;

    @Column(name = "collection_name")
    private String collectionName;

    public User getUser() {
        return user;
    }

    public Integer getNumberOfDocs() {
        return numberOfDocs;
    }

    public String getCollectionName() {
        return collectionName;
    }

}
