package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.CollectionDocumentDto;
import com.ragmind.ragbackend.dto.CollectionDto;
import com.ragmind.ragbackend.dto.request.AddCollectionDocumentRequest;
import com.ragmind.ragbackend.dto.request.CreateCollectionRequestDto;
import com.ragmind.ragbackend.dto.request.CreateDocumentRequestDto;
import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.entity.CollectionDocuments;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.repository.CollectionChatRepository;
import com.ragmind.ragbackend.repository.CollectionDocumentRepository;
import com.ragmind.ragbackend.repository.CollectionRepository;
import com.ragmind.ragbackend.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final CollectionDocumentRepository documentRepository;
    private final CollectionChatRepository collectionChatRepository;

    CollectionService(CollectionRepository collectionRepository, UserRepository userRepository, CollectionDocumentRepository collectionDocumentRepository, CollectionChatRepository collectionChatRepository) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.documentRepository = collectionDocumentRepository;
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

    public List<CollectionDocumentDto> getAllDocuments(Long collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        return documentRepository.findByCollection_Id(collection.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CollectionDocumentDto getDocument(Long collectionId, Long documentId) {
        CollectionDocuments document = documentRepository
                .findByIdAndCollection_Id(documentId, collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        return toDto(document);
    }

    public void addDocument(Long collectionId, AddCollectionDocumentRequest request) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        CollectionDocuments document = new CollectionDocuments();
        document.setCollection(collection);
        document.setDocName(request.docName());
        document.setSharedPath(request.sharedPath());
        document.setAddedDate(new Date());

        documentRepository.save(document);

        collection.setNumberOfDocs(
                collection.getNumberOfDocs() == null
                        ? 1
                        : collection.getNumberOfDocs() + 1
        );

        //TODO Enqueue Document Task in RabbitMQ Exchanger
    }

    private CollectionDocumentDto toDto(CollectionDocuments document) {
        return new CollectionDocumentDto(
                document.getId(),
                document.getCollection().getId(),
                document.getDocName(),
                document.getSharedPath(),
                document.getAddedDate()
        );
    }

}
