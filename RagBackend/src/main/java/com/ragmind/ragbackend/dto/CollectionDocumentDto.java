package com.ragmind.ragbackend.dto;

import java.util.Date;

public record CollectionDocumentDto(
        Long id,
        Long collectionId,
        String docName,
        String sharedPath,
        Date addedDate
) {}
