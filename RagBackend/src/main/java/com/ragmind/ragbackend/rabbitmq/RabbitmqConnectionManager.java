package com.ragmind.ragbackend.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class RabbitmqConnectionManager {

    @Value("${rabbitmq.host.url}")
    private String rabbitmqHost;

    private Connection connection;

    @PostConstruct
    void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitmqHost);
            this.connection = factory.newConnection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RabbitMQ connection", e);
        }
    }

    Channel createChannel() {
        try {
            return connection.createChannel();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RabbitMQ channel", e);
        }
    }

    @PreDestroy
    void shutdown() {
        try {
            if (connection != null) connection.close();
        } catch (Exception ignored) {}
    }
}
