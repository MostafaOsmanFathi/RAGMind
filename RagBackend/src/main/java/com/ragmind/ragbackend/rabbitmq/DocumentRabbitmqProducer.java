package com.ragmind.ragbackend.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.ragmind.ragbackend.config.RabbitmqConnectionInitializerConfig;
import com.ragmind.ragbackend.dto.rabbitmq.DocumentRabbitmqRequestDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class DocumentRabbitmqProducer {

    private static final String EXCHANGE_NAME = "doc_topic_worker";
    private static final String EXCHANGE_TYPE = "topic";

    private final RabbitmqConnectionInitializerConfig connectionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Channel channel;

    public DocumentRabbitmqProducer(RabbitmqConnectionInitializerConfig connectionManager) {
        this.connectionManager = connectionManager;
    }

    @PostConstruct
    void init() {
        try {
            channel = connectionManager.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to init DocumentRabbitmqProducer", e);
        }
    }

    public void sendDocumentTask(DocumentRabbitmqRequestDto dto) {
        try {
            byte[] body = objectMapper.writeValueAsBytes(dto);

            channel.basicPublish(
                    EXCHANGE_NAME,
                    "document.add",
                    null,
                    body
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish document task", e);
        }
    }

    @PreDestroy
    void shutdown() {
        try {
            if (channel != null) channel.close();
        } catch (Exception ignored) {}
    }
}
