package org.example.controller;

import org.example.model.Order;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // Get the status of all orders
    @GetMapping("/status")
    public List<OrderStatusResponse> getAllOrderStatuses() {
        return orderRepository.findAll().stream()
                .map(order -> new OrderStatusResponse(order.getOrderId(), order.getStatus()))
                .collect(Collectors.toList());
    }

    // Get the status of a specific order based on the order ID
    @GetMapping("/status/{orderId}")
    public OrderStatusResponse getOrderStatusById(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new IllegalArgumentException("Order not found for id: " + orderId));
        return new OrderStatusResponse(order.getOrderId(), order.getStatus());
    }

    // Internal Classes for Responding to Order Status
    public static class OrderStatusResponse {
        private Long orderId;
        private String status;

        public OrderStatusResponse(Long orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

    }
}
