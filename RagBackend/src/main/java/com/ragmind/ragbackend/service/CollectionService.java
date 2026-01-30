package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.CollectionDocumentDto;
import com.ragmind.ragbackend.dto.CollectionDto;
import com.ragmind.ragbackend.dto.rabbitmq.DocumentRabbitmqRequestDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
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
    private final RabbitmqService rabbitmqService;

    CollectionService(CollectionRepository collectionRepository, UserRepository userRepository, CollectionDocumentRepository collectionDocumentRepository, CollectionChatRepository collectionChatRepository, RabbitmqService rabbitmqService) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.documentRepository = collectionDocumentRepository;
        this.collectionChatRepository = collectionChatRepository;
        this.rabbitmqService = rabbitmqService;
    }


    public List<CollectionDto> getAllCollection(String email) {
        List<Collection> result = collectionRepository.findByUser_Email(email);
        return result.stream().map(Collection::toDto).toList();
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
        collection.setCollectionName(createDocumentRequestDto.getCollectionName());
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

    public void addDocument(Long collectionId, String documentPath, String docName, String collectionName, Authentication authentication) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));

        CollectionDocuments document = new CollectionDocuments();
        document.setCollection(collection);
        document.setDocName(docName);
        document.setSharedPath(documentPath);
        document.setAddedDate(new Date());

        CollectionDocuments saved = documentRepository.save(document);

        collection.setNumberOfDocs(
                collection.getNumberOfDocs() == null
                        ? 1
                        : collection.getNumberOfDocs() + 1
        );
        collectionRepository.save(collection);

        DocumentRabbitmqRequestDto rabbitmqRequestDto = new DocumentRabbitmqRequestDto();
        rabbitmqRequestDto.setAction("add");
        rabbitmqRequestDto.setFilePath(documentPath);
        rabbitmqRequestDto.setUserId(authentication.getName());
        rabbitmqRequestDto.setCollectionName(collectionName);
        rabbitmqRequestDto.setEmbedModel("nomic-embed-text");
        rabbitmqRequestDto.setLlmModel("mistral");
        rabbitmqRequestDto.setTaskId(saved.getId().toString());
        //TODO make llm models and embedder configurable

        //TODO get static Backend id from docker
        rabbitmqRequestDto.setBackendId("backend-1");

        rabbitmqService.sendDocumentTask(rabbitmqRequestDto);
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
