package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.CollectionDto;
import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.repository.CollectionChatRepository;
import com.ragmind.ragbackend.repository.CollectionDocumentRepository;
import com.ragmind.ragbackend.repository.CollectionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final CollectionDocumentRepository collectionDocumentRepository;
    private final CollectionChatRepository collectionChatRepository;

    CollectionService(CollectionRepository collectionRepository, CollectionDocumentRepository collectionDocumentRepository, CollectionChatRepository collectionChatRepository) {
        this.collectionRepository = collectionRepository;
        this.collectionDocumentRepository = collectionDocumentRepository;
        this.collectionChatRepository = collectionChatRepository;
    }


    public List<CollectionDto> getAllCollection(String email, int limit, int skip) {
        int page = skip / limit;
        var result = collectionRepository.findByUser_Email(email, PageRequest.of(page, limit));
        return result.getContent().stream().map(Collection::toDto).toList();
    }

    public CollectionDto getCollectionById(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() ->
                        new RuntimeException(String.format("Collection with Id {%d} not found", collectionId))
                );
        return Collection.toDto(collection);
    }
}
