package com.ragmind.ragbackend.rabbitmq;

import com.rabbitmq.client.*;
import com.ragmind.ragbackend.config.RabbitmqConnectionInitializerConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
public class FeedbackDocumentConsumer {

    private final RabbitmqConnectionInitializerConfig rabbitConfig;
    private Channel channel;
    private final String QUEUE_NAME = "feedback.document.queue";
    private final String EXCHANGE_NAME = "doc_topic_feedback";
    private final String ROUTING_KEY = "doc.#";

    public FeedbackDocumentConsumer(RabbitmqConnectionInitializerConfig rabbitConfig) {
        this.rabbitConfig = rabbitConfig;
    }

    @PostConstruct
    public void start() throws IOException, TimeoutException {
        channel = rabbitConfig.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        System.out.println("FeedbackDocumentConsumer is ready and listening...");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    private final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println("FeedbackDocumentConsumer received: " + message);

        // TODO: websocket message with status of the document


    };

    @PreDestroy
    public void shutdown() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
        } catch (Exception ignored) {}
    }
}
