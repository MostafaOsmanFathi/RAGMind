package com.ragmind.ragbackend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendToUser(String principalEmailName, String destination, Object payload) {
        simpMessagingTemplate.convertAndSendToUser(principalEmailName, destination, payload);
    }

    public void syncUserMessages(String principalEmailName, Object payload) {
        sendToUser(principalEmailName, "/queue/ask-result", payload);
    }
}
