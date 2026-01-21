package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.CollectionDto;
import com.ragmind.ragbackend.dto.request.CreateCollectionRequestDto;
import com.ragmind.ragbackend.dto.request.CreateDocumentRequestDto;
import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.repository.CollectionChatRepository;
import com.ragmind.ragbackend.repository.CollectionDocumentRepository;
import com.ragmind.ragbackend.repository.CollectionRepository;
import com.ragmind.ragbackend.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final CollectionDocumentRepository collectionDocumentRepository;
    private final CollectionChatRepository collectionChatRepository;

    CollectionService(CollectionRepository collectionRepository, UserRepository userRepository, CollectionDocumentRepository collectionDocumentRepository, CollectionChatRepository collectionChatRepository) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
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

    public CollectionDto addCollection(CreateCollectionRequestDto createDocumentRequestDto, String userEmail) {
        Collection collection = new Collection();
        collection.setCollectionName(createDocumentRequestDto.getName());
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ValidationException("User don't exists"));
        collection.setUser(user);
        collection.setChatHistory(new ArrayList<>());
        collection.setCollectionDocuments(new ArrayList<>());
        collection.setNumberOfDocs(0);
        collectionRepository.save(collection);

        return new CollectionDto(collection);
    }

    public boolean deleteCollection(Long id) {
        if (!collectionRepository.existsById(id)) {
            return false;
        }
        collectionRepository.deleteById(id);
        return true;
    }

}
