package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.CollectionChatDTO;
import com.ragmind.ragbackend.dto.rabbitmq.AskRabbitmqRequestDto;
import com.ragmind.ragbackend.dto.rabbitmq.DocumentRabbitmqRequestDto;
import com.ragmind.ragbackend.entity.CollectionChat;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.rabbitmq.DocumentRabbitmqProducer;
import com.ragmind.ragbackend.rabbitmq.RagRabbitmqProducer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class RabbitmqService {
    final private DocumentRabbitmqProducer documentRabbitmqProducer;
    final private RagRabbitmqProducer ragRabbitmqProducer;
    final private ChatService chatService;
    final private WebSocketService webSocketService;

    public RabbitmqService(DocumentRabbitmqProducer documentRabbitmqProducer, RagRabbitmqProducer ragRabbitmqProducer, ChatService chatService, WebSocketService webSocketService) {
        this.documentRabbitmqProducer = documentRabbitmqProducer;
        this.ragRabbitmqProducer = ragRabbitmqProducer;
        this.chatService = chatService;
        this.webSocketService = webSocketService;
    }


    public void sendDocumentTask(DocumentRabbitmqRequestDto documentRabbitmqRequestDto) {
        documentRabbitmqProducer.sendDocumentTask(documentRabbitmqRequestDto);
    }


    public void sendAskTask(AskRabbitmqRequestDto askRabbitmqRequestDto, Authentication authentication) {
        CollectionChat collectionChat = chatService.saveMessage(askRabbitmqRequestDto.getQuestion(), "user", Long.valueOf(askRabbitmqRequestDto.getCollectionName()));
        CollectionChatDTO collectionChatDTO = chatService.toDto(collectionChat);
        ragRabbitmqProducer.sendAskTask(askRabbitmqRequestDto);
        webSocketService.syncUserMessages(authentication.getName(), collectionChatDTO);
    }

}
