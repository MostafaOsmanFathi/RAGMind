package com.ragmind.ragbackend.dto;


import com.ragmind.ragbackend.entity.Collection;

public record CollectionDto(
        Long id,
        String collectionName,
        Integer numberOfDocs
) {
    public CollectionDto(Collection collection) {
        this(collection.getId(), collection.getCollectionName(), collection.getNumberOfDocs());
    }
}
