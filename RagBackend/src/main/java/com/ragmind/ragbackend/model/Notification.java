package com.ragmind.ragbackend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_notification")
    private User user;

    @Column(name = "notification_message")
    private String notificationMessage;

    @Column(name = "creation_date")
    private Date creationDate;

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
