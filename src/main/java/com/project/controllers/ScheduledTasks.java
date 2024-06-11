package com.project.controllers;

import com.project.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 300000, initialDelay = 1000) // Runs every 5 minutes, with initial delay of 1 second
    public void scheduledCheckProducts() {
        notificationService.checkProducts();
    }
}

