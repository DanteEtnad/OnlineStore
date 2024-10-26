package org.example.controller;

import org.example.model.Order;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Comparator;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Objects;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/bank")
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

    // Get the status of all orders for a specific customer by customerId
    @GetMapping("/status/customer/{customerId}")
    public List<OrderStatusResponse> getOrderStatusesByCustomerId(@PathVariable Long customerId) {
        return orderRepository.findByCustomer_CustomerId(customerId).stream()
                .map(order -> new OrderStatusResponse(order.getOrderId(), order.getStatus()))
                .collect(Collectors.toList());
    }

    // Get detailed information for all orders
    @GetMapping("/orders")
    public List<OrderDetailResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getOrderId))
                .map(order -> new OrderDetailResponse(
                        order.getOrderId(),
                        order.getOrderDate(),
                        order.getQuantity(),
                        order.getStatus(),
                        order.getTotalAmount(),
                        order.getBankTransfer() != null ? order.getBankTransfer().getBankTransferId() : null,
                        order.getCustomer().getCustomerId(),
                        order.getProduct().getProductId()
                ))
                .collect(Collectors.toList());
    }

    // Stream orders for a specific customer by customerId
    @GetMapping("/customer/{customerId}")
    public SseEmitter streamOrdersByCustomerId(@PathVariable Long customerId) {
        SseEmitter emitter = new SseEmitter(3600000L); // Set 1-hour timeout

        new Thread(() -> {
            try {
                // Record the previous list of orders
                List<OrderDetailResponse> lastOrders = null;
                long lastUpdateTime = System.currentTimeMillis();

                while (true) {
                    // Get current orders
                    List<OrderDetailResponse> currentOrders = orderRepository.findByCustomer_CustomerId(customerId).stream()
                            .sorted(Comparator.comparing(Order::getOrderId))
                            .map(order -> new OrderDetailResponse(
                                    order.getOrderId(),
                                    order.getOrderDate(),
                                    order.getQuantity(),
                                    order.getStatus(),
                                    order.getTotalAmount(),
                                    order.getBankTransfer() != null ? order.getBankTransfer().getBankTransferId() : null,
                                    order.getCustomer().getCustomerId(),
                                    order.getProduct().getProductId()
                            ))
                            .collect(Collectors.toList());

                    // Check if the current list of orders is the same as the last sent orders (content comparison, not reference comparison)
                    if (!areOrdersEqual(lastOrders, currentOrders)) {
                        emitter.send(currentOrders); // Send data if there are changes
                        lastOrders = currentOrders; // Update the last order data
                        lastUpdateTime = System.currentTimeMillis(); // Update last update time
                    }

                    // Check for changes every 5 seconds
                    Thread.sleep(5000);

                    // Close connection if there are no updates for more than 30 seconds, without sending any message
                    if (System.currentTimeMillis() - lastUpdateTime > 30000) {
                        emitter.complete(); // Close connection without sending additional messages
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);  // Handle exception and end SSE session
            }
        }).start();

        return emitter;
    }

    /**
     * Custom comparison function to check if two lists of orders are the same in content
     */
    private boolean areOrdersEqual(List<OrderDetailResponse> lastOrders, List<OrderDetailResponse> currentOrders) {
        if (lastOrders == null && currentOrders == null) {
            return true;
        }
        if (lastOrders == null || currentOrders == null || lastOrders.size() != currentOrders.size()) {
            return false;
        }

        // Compare key information of each order
        for (int i = 0; i < lastOrders.size(); i++) {
            OrderDetailResponse lastOrder = lastOrders.get(i);
            OrderDetailResponse currentOrder = currentOrders.get(i);

            if (!Objects.equals(lastOrder.getOrderId(), currentOrder.getOrderId()) ||
                    !Objects.equals(lastOrder.getQuantity(), currentOrder.getQuantity()) ||
                    !Objects.equals(lastOrder.getStatus(), currentOrder.getStatus()) ||
                    !Objects.equals(lastOrder.getTotalAmount(), currentOrder.getTotalAmount()) ||
                    !Objects.equals(lastOrder.getProductId(), currentOrder.getProductId()) ||
                    !Objects.equals(lastOrder.getBankTransferId(), currentOrder.getBankTransferId())) {
                return false; // Lists are considered different if any field differs
            }
        }

        return true; // Lists are considered equal if all fields are the same
    }

    // Internal Classes for Responding to Order Status
    public static class OrderStatusResponse {
        private Long orderId;
        private String status;

        public OrderStatusResponse(Long orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }
        //Getter
        public Long getOrderId() {return orderId;}
        public String getStatus() {return status;}
    }

    public static class OrderDetailResponse {
        private Long orderId;
        private LocalDateTime orderDate;
        private Integer quantity;
        private String status;
        private Double totalAmount;
        private Long bankTransferId;
        private Long customerId;
        private Long productId;

        public OrderDetailResponse(Long orderId, LocalDateTime orderDate, Integer quantity, String status, Double totalAmount, Long bankTransferId, Long customerId, Long productId) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.quantity = quantity;
            this.status = status;
            this.totalAmount = totalAmount;
            this.bankTransferId = bankTransferId;
            this.customerId = customerId;
            this.productId = productId;
        }

        // Getters
        public Long getOrderId() {
            return orderId;
        }

        public LocalDateTime getOrderDate() {
            return orderDate;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public String getStatus() {
            return status;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public Long getBankTransferId() {
            return bankTransferId;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public Long getProductId() {
            return productId;
        }
    }
}
