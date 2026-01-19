package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.entity.CollectionDocuments;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rag/collection/{collectionId}/documents")
class DocumentsController {

    @GetMapping({"/", ""})
    List<CollectionDocuments> getAllDocuments(@PathVariable String collectionId) {
        return null;
    }

    @GetMapping("/{documentId}")
    CollectionDocuments getDocument(@PathVariable String collectionId, @PathVariable String documentId) {
        return null;
    }

    @PostMapping({"/", ""})
    void addDocument(@PathVariable String collectionId) {

    }


}
