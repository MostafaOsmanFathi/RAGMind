package com.ragmind.ragbackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseConnectionInitializerConfig {

    private final DataSource dataSource;

    public DatabaseConnectionInitializerConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void waitForDatabase() {
        int trials = 10;
        int delayMs = 3000;

        while (trials > 0) {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("Connected to database!");
                break;
            } catch (SQLException e) {
                trials--;
                System.out.println(
                        "Database not ready yet, retrying in "
                                + (delayMs / 1000)
                                + " seconds... (" + trials + " retries left)"
                );

                if (trials == 0) {
                    throw new RuntimeException("Failed to connect to database after retries", e);
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
}