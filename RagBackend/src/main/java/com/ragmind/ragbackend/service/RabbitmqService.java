package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.dto.rabbitmq.AskRabbitmqRequestDto;
import com.ragmind.ragbackend.dto.rabbitmq.DocumentRabbitmqRequestDto;
import com.ragmind.ragbackend.rabbitmq.DocumentRabbitmqProducer;
import com.ragmind.ragbackend.rabbitmq.RagRabbitmqProducer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class RabbitmqService {
    final private DocumentRabbitmqProducer documentRabbitmqProducer;
    final private RagRabbitmqProducer ragRabbitmqProducer;
    final private ChatService chatService;

    public RabbitmqService(DocumentRabbitmqProducer documentRabbitmqProducer, RagRabbitmqProducer ragRabbitmqProducer, ChatService chatService) {
        this.documentRabbitmqProducer = documentRabbitmqProducer;
        this.ragRabbitmqProducer = ragRabbitmqProducer;
        this.chatService = chatService;
    }


    public void sendDocumentTask(DocumentRabbitmqRequestDto documentRabbitmqRequestDto) {
        documentRabbitmqProducer.sendDocumentTask(documentRabbitmqRequestDto);
    }

    public void sendAskTask(AskRabbitmqRequestDto askRabbitmqRequestDto) {
        chatService.saveMessage(askRabbitmqRequestDto.getQuestion(), "user", Long.valueOf(askRabbitmqRequestDto.getCollectionName()));
        ragRabbitmqProducer.sendAskTask(askRabbitmqRequestDto);
        //TODO update frontend client websocket with new message
    }

}
