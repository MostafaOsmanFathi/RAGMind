package com.ragmind.ragbackend.service;

import com.ragmind.ragbackend.entity.Notification;
import com.ragmind.ragbackend.entity.User;
import com.ragmind.ragbackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class NotificationService {
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public NotificationService(UserService userService, NotificationRepository notificationRepository) {
        this.userService = userService;
        this.notificationRepository = notificationRepository;
    }

    public void addNotificationToUser(String userEmail, String message) {
        Notification notification = new Notification();
        User user = userService.getUserByEmail(userEmail);

        notification.setNotificationMessage(message);
        notification.setUser(user);
        notification.setCreationDate(Date.from(Instant.now()));
        notificationRepository.save(notification);
    }
}
