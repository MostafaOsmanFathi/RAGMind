package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.dto.request.AddCollectionDocumentRequest;
import com.ragmind.ragbackend.service.CollectionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/rag/collection/{collectionId}/documents")
class DocumentsController {

    private final CollectionService collectionService;

    @Value("${app.files.shared-dir}")
    private String sharedStorageDir;

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
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        try {
            Path dirPath = Paths.get(sharedStorageDir).resolve(authentication.getName()).resolve(collectionId.toString());
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path filePath = dirPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(filePath.toFile());
            collectionService.addDocument(collectionId,filePath.toString(),file.getOriginalFilename(),collectionId.toString(),authentication);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not save file: " + e.getMessage());
        }
    }
}
