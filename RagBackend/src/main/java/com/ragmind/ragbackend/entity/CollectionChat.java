package com.ragmind.ragbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "collection_chat")
public class CollectionChat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    private String message;
    private String role;
    private LocalDate date;

    public Collection getCollection() {
        return collection;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getDate() {
        return date;
    }
}
