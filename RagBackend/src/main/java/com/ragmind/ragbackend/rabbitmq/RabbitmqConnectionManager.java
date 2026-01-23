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

    @Value("${rabbitmq.host.username}")
    private String rabbitmqUsername;

    @Value("${rabbitmq.host.password}")
    private String rabbitmqPassword;

    @Value("${rabbitmq.host.port}")
    private Integer rabbitmqPort;

    private Connection connection;

    @PostConstruct
    void init() {
        int trials = 10;
        int delayMs = 3000; // 3 seconds between retries

        while (trials > 0) {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(rabbitmqHost);
                factory.setPort(rabbitmqPort);
                factory.setUsername(rabbitmqUsername);
                factory.setPassword(rabbitmqPassword);

                this.connection = factory.newConnection();
                System.out.println("Connected to RabbitMQ!");
                break; // success, exit loop
            } catch (Exception e) {
                trials--;
                System.out.println("RabbitMQ not ready yet, retrying in " + (delayMs/1000) + " seconds... (" + trials + " retries left)");
                if (trials == 0) {
                    throw new RuntimeException("Failed to create RabbitMQ connection after retries", e);
                }
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
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
        } catch (Exception ignored) {
        }
    }
}
