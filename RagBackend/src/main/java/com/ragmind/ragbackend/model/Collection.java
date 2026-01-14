package com.ragmind.ragbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "collection")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
