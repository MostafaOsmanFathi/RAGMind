package com.ragmind.ragbackend.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.ragmind.ragbackend.dto.rabbitmq.AskRabbitmqRequestDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class RagRabbitmqProducer {

    private static final String EXCHANGE_NAME = "rag_topic_worker";
    private static final String EXCHANGE_TYPE = "topic";

    private final RabbitmqConnectionManager connectionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Channel channel;

    public RagRabbitmqProducer(RabbitmqConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @PostConstruct
    void init() {
        try {
            channel = connectionManager.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to init RagRabbitmqProducer", e);
        }
    }

    public void sendAskTask(AskRabbitmqRequestDto dto) {
        try {
            byte[] body = objectMapper.writeValueAsBytes(dto);
            String routingKey = "rag.ask";
            channel.basicPublish(
                    EXCHANGE_NAME,
                    routingKey,
                    null,
                    body
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish RAG task", e);
        }
    }

    @PreDestroy
    void shutdown() {
        try {
            if (channel != null) channel.close();
        } catch (Exception ignored) {
        }
    }
}
