package org.example.controller;

import org.example.model.Order;
import org.example.Service.OrderWarehouseService;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/order-allocation")
public class OrderAllocationController {

    @Autowired
    private OrderWarehouseService orderWarehouseService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/allocate/{orderId}")
    public ResponseEntity<Map<String, String>> allocateWarehouseForOrder(@PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Order not found for ID: " + orderId);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Order order = optionalOrder.get();
        try {
            orderWarehouseService.allocateWarehouseForOrder(order);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Order warehouse allocation completed for order ID: " + orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Allocation failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "An error occurred: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
