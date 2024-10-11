package org.example.Service;

import org.example.model.Customer;
import org.example.model.Order;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private OrderRepository orderRepository;

    private final Map<Long, String> orderStatusMap = new HashMap<>();
    private final List<Notification> notifications = new ArrayList<>();

    @Scheduled(fixedDelay = 500) // Every 0.5 seconds
    public void checkOrderStatus() {
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            Long orderId = order.getOrderId();
            String currentStatus = order.getStatus();

            if (!currentStatus.equals(orderStatusMap.get(orderId))) {
                orderStatusMap.put(orderId, currentStatus);

                Customer customer = order.getCustomer();

                String message = String.format("%s, your order with ID %d has been updated. The new status is %s. This information will be sent to the email address %s.",
                        customer.getName(), orderId, currentStatus, customer.getEmail());

                // Save the notification to the list
                notifications.add(new Notification(customer.getCustomerId(), message));
            }
        }
    }

    public List<String> getNotifications() {
        return notifications.stream().map(Notification::getMessage).collect(Collectors.toList());
    }

    public List<String> getNotificationsForCustomer(Long customerId) {
        return notifications.stream()
                .filter(notification -> notification.getCustomerId().equals(customerId))
                .map(Notification::getMessage)
                .collect(Collectors.toList());
    }

    // Inner class to store notification details
    private static class Notification {
        private final Long customerId;
        private final String message;

        public Notification(Long customerId, String message) {
            this.customerId = customerId;
            this.message = message;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public String getMessage() {
            return message;
        }
    }
}
