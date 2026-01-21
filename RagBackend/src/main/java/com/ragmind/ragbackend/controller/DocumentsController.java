package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.dto.request.AddCollectionDocumentRequest;
import com.ragmind.ragbackend.service.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag/collection/{collectionId}/documents")
class DocumentsController {

    private final CollectionService collectionService;

    DocumentsController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping({"", "/"})
    ResponseEntity<?> getAllDocuments(@PathVariable Long collectionId) {
        try {
            return ResponseEntity.ok(
                    collectionService.getAllDocuments(collectionId)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{documentId}")
    ResponseEntity<?> getDocument(
            @PathVariable Long collectionId,
            @PathVariable Long documentId
    ) {
        try {
            return ResponseEntity.ok(
                    collectionService.getDocument(collectionId, documentId)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    ResponseEntity<?> addDocument(
            @PathVariable Long collectionId,
            @RequestBody AddCollectionDocumentRequest request
    ) {
        try {
            collectionService.addDocument(collectionId, request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
