package org.example.controller;

import org.example.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/notifications")
    public List<String> getNotifications() {
        return emailService.getNotifications();
    }

    // Endpoint to get notifications for a specific customer
    @GetMapping("/notifications/{customerId}")
    public List<String> getNotificationsForCustomer(@PathVariable Long customerId) {
        return emailService.getNotificationsForCustomer(customerId);
    }
}
