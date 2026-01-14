package com.ragmind.ragbackend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "collection_documents")
public class CollectionDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Column(name = "doc_name")
    private String docName;

    @Column(name = "shared_path")
    private String sharedPath;

    @Column(name = "added_date")
    private Date addedDate;

    public Collection getCollection() {
        return collection;
    }

    public String getDocName() {
        return docName;
    }

    public String getSharedPath() {
        return sharedPath;
    }

    public Date getAddedDate() {
        return addedDate;
    }
}
