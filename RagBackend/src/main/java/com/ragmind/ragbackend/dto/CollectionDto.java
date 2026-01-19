package com.ragmind.ragbackend.dto;


public record CollectionDto(
        Long id,
        String collectionName,
        Integer numberOfDocs
) {

}
