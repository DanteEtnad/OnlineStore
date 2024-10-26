package org.example.controller;

import org.example.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/notifications")
    public List<String> getNotifications() {
        return emailService.getNotifications();
    }

    @GetMapping("/notifications/{customerId}")
    public SseEmitter streamNotificationsForCustomer(@PathVariable Long customerId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5-minute timeout

        new Thread(() -> {
            try {
                List<String> lastNotifications = new ArrayList<>(); // Initialize as empty
                long lastUpdateTime = System.currentTimeMillis();

                while (true) {
                    // Get current notifications
                    List<String> currentNotifications = emailService.getNotificationsForCustomer(customerId);

                    // Get incremental notifications
                    List<String> newNotifications = getNewNotifications(lastNotifications, currentNotifications);

                    // Send if there are new notifications
                    if (!newNotifications.isEmpty()) {
                        emitter.send(newNotifications);
                        lastNotifications = new ArrayList<>(currentNotifications);
                        lastUpdateTime = System.currentTimeMillis();
                    }

                    // Check every 5 seconds
                    Thread.sleep(5000);

                    // Close connection if no changes for more than 5 minutes
                    if (System.currentTimeMillis() - lastUpdateTime > 300000) {
                        emitter.complete();
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    // Return new notifications
    private List<String> getNewNotifications(List<String> lastNotifications, List<String> currentNotifications) {
        if (lastNotifications == null || lastNotifications.isEmpty()) {
            return new ArrayList<>(currentNotifications); // Return all current notifications if previous was empty
        }
        // Find new notifications
        List<String> newNotifications = new ArrayList<>();
        for (int i = lastNotifications.size(); i < currentNotifications.size(); i++) {
            newNotifications.add(currentNotifications.get(i));
        }
        return newNotifications;
    }

}
