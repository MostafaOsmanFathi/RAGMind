package com.ragmind.ragbackend.dto;

import java.util.Date;

public class CollectionDocumentsDTO {

    private Long id;
    private Long collectionId;
    private String docName;
    private String sharedPath;
    private Date addedDate;

    public CollectionDocumentsDTO() {
    }

    public CollectionDocumentsDTO(Long id, Long collectionId, String docName, String sharedPath, Date addedDate) {
        this.id = id;
        this.collectionId = collectionId;
        this.docName = docName;
        this.sharedPath = sharedPath;
        this.addedDate = addedDate;
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

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getSharedPath() {
        return sharedPath;
    }

    public void setSharedPath(String sharedPath) {
        this.sharedPath = sharedPath;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
}
