package com.project.controllers;

import com.project.models.Notification;
import com.project.repositories.NotificationRepository;
import com.project.services.NotificationService;
import com.project.services.NotificationSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @Autowired
    private NotificationSseService notificationSseService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) throws IOException {
        return notificationService.createNotification(notification);
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/roles")
    public List<Notification> getByRoleNotifications(@RequestParam List<String> roleNames) {
        List<Notification> allNotifications = new ArrayList<>();
        for (String roleName : roleNames) {
            List<Notification> notificationsForRole = notificationService.getByRoleNotifications(roleName);
            allNotifications.addAll(notificationsForRole);
        }
        return allNotifications;
    }

    @DeleteMapping("/{id}")
    public void deleteNotificationById(@PathVariable Long id) {
        notificationService.deleteNotificationById(id);
    }



    @GetMapping("/stream")
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter();
        notificationService.addEmitter(emitter);

        // Set completion, timeout, and error handlers for the emitter
        emitter.onCompletion(() -> notificationService.removeEmitter(emitter));
        emitter.onTimeout(() -> notificationService.removeEmitter(emitter));
        emitter.onError((e) -> notificationService.removeEmitter(emitter));

        // Fetch all notifications from the database and emit them
        List<Notification> notifications = notificationRepository.findAll();
        notifications.forEach(notification -> {
            try {
                emitter.send(notification);
            } catch (IOException e) {
                // Handle the error
                e.printStackTrace();
            }
        });

        return emitter;
    }


}

