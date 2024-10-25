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

    // 根据 customerId 获取该客户所有订单的状态
    @GetMapping("/status/customer/{customerId}")
    public List<OrderStatusResponse> getOrderStatusesByCustomerId(@PathVariable Long customerId) {
        return orderRepository.findByCustomer_CustomerId(customerId).stream()
                .map(order -> new OrderStatusResponse(order.getOrderId(), order.getStatus()))
                .collect(Collectors.toList());
    }

    // 获取所有订单的详细信息
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



    @GetMapping("/customer/{customerId}")
    public SseEmitter streamOrdersByCustomerId(@PathVariable Long customerId) {
        SseEmitter emitter = new SseEmitter(3600000L); // 设置1小时超时

        new Thread(() -> {
            try {
                // 记录上一次的订单列表
                List<OrderDetailResponse> lastOrders = null;
                long lastUpdateTime = System.currentTimeMillis();

                while (true) {
                    // 获取当前订单
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

                    // 检查当前订单列表是否与上次发送的订单相同（内容比较而不是引用比较）
                    if (!areOrdersEqual(lastOrders, currentOrders)) {
                        emitter.send(currentOrders); // 如果有变化则发送数据
                        lastOrders = currentOrders; // 更新最后一次的订单数据
                        lastUpdateTime = System.currentTimeMillis(); // 更新最后的更新时间
                    }

                    // 每 5 秒检查一次变化
                    Thread.sleep(5000);

                    // 如果超过30秒没有更新，直接关闭连接，不发送任何消息
                    if (System.currentTimeMillis() - lastUpdateTime > 30000) {
                        emitter.complete(); // 关闭连接，不发送额外消息
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);  // 处理异常并结束 SSE 会话
            }
        }).start();

        return emitter;
    }

    /**
     * 自定义比较函数，比较两个订单列表的内容是否相同
     */
    private boolean areOrdersEqual(List<OrderDetailResponse> lastOrders, List<OrderDetailResponse> currentOrders) {
        if (lastOrders == null && currentOrders == null) {
            return true;
        }
        if (lastOrders == null || currentOrders == null || lastOrders.size() != currentOrders.size()) {
            return false;
        }

        // 比较每个订单的关键信息
        for (int i = 0; i < lastOrders.size(); i++) {
            OrderDetailResponse lastOrder = lastOrders.get(i);
            OrderDetailResponse currentOrder = currentOrders.get(i);

            if (!Objects.equals(lastOrder.getOrderId(), currentOrder.getOrderId()) ||
                    !Objects.equals(lastOrder.getQuantity(), currentOrder.getQuantity()) ||
                    !Objects.equals(lastOrder.getStatus(), currentOrder.getStatus()) ||
                    !Objects.equals(lastOrder.getTotalAmount(), currentOrder.getTotalAmount()) ||
                    !Objects.equals(lastOrder.getProductId(), currentOrder.getProductId()) ||
                    !Objects.equals(lastOrder.getBankTransferId(), currentOrder.getBankTransferId())) {
                return false; // 如果任意字段不同，则认为列表不同
            }
        }

        return true; // 所有字段相同，认为列表相等
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
