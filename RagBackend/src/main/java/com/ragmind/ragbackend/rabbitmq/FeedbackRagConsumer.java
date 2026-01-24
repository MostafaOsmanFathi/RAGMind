package com.ragmind.ragbackend.rabbitmq;

import com.rabbitmq.client.*;
import com.ragmind.ragbackend.config.RabbitmqConnectionInitializerConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FeedbackRagConsumer {

    private final RabbitmqConnectionInitializerConfig rabbitConfig;
    private Channel channel;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String QUEUE_NAME = "feedback.rag.queue";
    private static final String EXCHANGE_NAME = "rag_topic_feedback";
    private static final String ROUTING_KEY = "rag.#";

    public FeedbackRagConsumer(RabbitmqConnectionInitializerConfig rabbitConfig) {
        this.rabbitConfig = rabbitConfig;
    }

    @PostConstruct
    public void start() throws Exception {
        channel = rabbitConfig.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        executor.submit(() -> {
            try {
                System.out.println("FeedbackRagConsumer listening on queue: " + QUEUE_NAME);
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println("Received RAG feedback: " + message);

        // TODO: process the message
    };

    @PreDestroy
    public void stop() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
            executor.shutdownNow();
        } catch (Exception ignored) {}
    }
}
