package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.entity.CollectionChat;
import com.ragmind.ragbackend.repository.CollectionChatRepository;
import com.ragmind.ragbackend.repository.CollectionRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    final private CollectionChatRepository collectionChatRepository;
    final private CollectionRepository collectionRepository;

    public ChatService(CollectionChatRepository collectionChatRepository, CollectionRepository collectionRepository) {
        this.collectionChatRepository = collectionChatRepository;
        this.collectionRepository = collectionRepository;
    }

    public boolean saveMessage(String message, String role, Long collectionId) {
        try {

            CollectionChat collectionChat = new CollectionChat();
            collectionChat.setMessage(message);
            collectionChat.setRole(role);
            Collection collection = collectionRepository.findById(collectionId).orElseThrow();
            collectionChat.setCollection(collection);
            collectionChatRepository.save(collectionChat);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
