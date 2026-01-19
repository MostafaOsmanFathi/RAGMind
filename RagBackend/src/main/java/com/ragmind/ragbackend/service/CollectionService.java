package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.CollectionDto;
import com.ragmind.ragbackend.entity.CollectionChat;
import com.ragmind.ragbackend.repository.CollectionChatRepository;
import com.ragmind.ragbackend.repository.CollectionDocumentRepository;
import com.ragmind.ragbackend.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Service
class CollectionService {
    private final CollectionRepository collectionRepository;
    private final CollectionDocumentRepository collectionDocumentRepository;
    private final CollectionChatRepository collectionChatRepository;

    CollectionService(CollectionRepository collectionRepository, CollectionDocumentRepository collectionDocumentRepository, CollectionChatRepository collectionChatRepository) {
        this.collectionRepository = collectionRepository;
        this.collectionDocumentRepository = collectionDocumentRepository;
        this.collectionChatRepository = collectionChatRepository;
    }


    public List<CollectionDto> getAllCollection(String email){
        return null;
    }

}
