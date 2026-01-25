package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.entity.Collection;
import com.ragmind.ragbackend.entity.CollectionChat;
import com.ragmind.ragbackend.repository.CollectionChatRepository;
import com.ragmind.ragbackend.repository.CollectionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ChatService {
    final private CollectionChatRepository collectionChatRepository;
    final private CollectionRepository collectionRepository;

    public ChatService(CollectionChatRepository collectionChatRepository, CollectionRepository collectionRepository) {
        this.collectionChatRepository = collectionChatRepository;
        this.collectionRepository = collectionRepository;
    }

    public CollectionChat saveMessage(String message, String role, Long collectionId) {
        try {

            CollectionChat collectionChat = new CollectionChat();
            collectionChat.setMessage(message);
            collectionChat.setRole(role);
            collectionChat.setDate(LocalDate.now());
            Collection collection = collectionRepository.findById(collectionId).orElseThrow();
            collectionChat.setCollection(collection);

            collectionChat = collectionChatRepository.save(collectionChat);

            return collectionChat;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
