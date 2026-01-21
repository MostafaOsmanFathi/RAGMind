package com.ragmind.ragbackend.controller;

import com.ragmind.ragbackend.dto.request.CreateCollectionRequestDto;
import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.service.CollectionService;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.ragmind.ragbackend.dto.CollectionDto;

import java.util.List;

@RestController
@RequestMapping("/rag/collection")
class CollectionController {

    CollectionService collectionService;

    CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping({"/", ""})
    ResponseEntity<List<CollectionDto>> getAllUserCollections(Authentication authentication,
                                                              @RequestParam(name = "limit", defaultValue = "5") int limit,
                                                              @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        try {
            String email = authentication.getName();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(collectionService.getAllCollection(email, limit, page));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    @GetMapping("/{collectionId}")
    ResponseEntity<?> getUserCollections(@PathVariable Long collectionId, Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(collectionService.getCollectionById(collectionId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("\"message\":Error");
        }
    }

    @PostMapping({"/", ""})
    void addCollection(CreateCollectionRequestDto collectionRequestDto, Authentication authentication) {
        collectionService.addCollection(collectionRequestDto, authentication.getName());
    }

    @DeleteMapping("/{collectionId}")
    void deleteCollection(@PathVariable String collectionId, Authentication authentication) {
        collectionService.deleteCollection(Long.valueOf(collectionId));
    }


}
