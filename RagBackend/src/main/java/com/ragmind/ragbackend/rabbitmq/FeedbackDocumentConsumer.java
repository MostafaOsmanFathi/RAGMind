package com.ragmind.ragbackend.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.ragmind.ragbackend.config.RabbitmqConnectionInitializerConfig;
import com.ragmind.ragbackend.dto.NotificationDTO;
import com.ragmind.ragbackend.dto.rabbitmq.DocumentFeedbackResponseDto;
import com.ragmind.ragbackend.dto.rabbitmq.RagFeedbackResponseDto;
import com.ragmind.ragbackend.service.NotificationService;
import com.ragmind.ragbackend.service.WebSocketService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
public class FeedbackDocumentConsumer {

    private final RabbitmqConnectionInitializerConfig rabbitConfig;
    private WebSocketService webSocketService;
    private NotificationService notificationService;
    private Channel channel;
    private final String QUEUE_NAME = "feedback.document.queue";
    private final String EXCHANGE_NAME = "doc_topic_feedback";
    private final String ROUTING_KEY = "doc.#";

    public FeedbackDocumentConsumer(RabbitmqConnectionInitializerConfig rabbitConfig, WebSocketService webSocketService, NotificationService notificationService) {
        this.rabbitConfig = rabbitConfig;
        this.webSocketService = webSocketService;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void start() throws IOException, TimeoutException {
        channel = rabbitConfig.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        System.out.println("FeedbackDocumentConsumer is ready and listening...");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    private final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println("FeedbackDocumentConsumer received: " + message);

        ObjectMapper mapper = new ObjectMapper();
        DocumentFeedbackResponseDto feedback = mapper.readValue(message, DocumentFeedbackResponseDto.class);
        webSocketService.syncUserMessages(feedback.getUserId(), feedback);

        NotificationDTO notificationDTO = new NotificationDTO();

        String messageContent = "Document %s completed with status: %s (Collection ID: %s)"
                .formatted(
                        feedback.getAction(),
                        feedback.getStatus(),
                        feedback.getCollectionName()
                );

        notificationDTO.setNotificationMessage(messageContent);

        webSocketService.syncUserNotification(feedback.getUserId(), notificationDTO);
        notificationService.addNotificationToUser(feedback.getUserId(), messageContent);
    };

    @PreDestroy
    public void shutdown() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
        } catch (Exception ignored) {
        }
    }
}
