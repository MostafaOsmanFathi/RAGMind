package com.ragmind.ragbackend.dto.request;

public record AddCollectionDocumentRequest(
        String docName,
        String sharedPath
) {}

