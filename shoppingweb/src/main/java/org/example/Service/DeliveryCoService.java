package org.example.Service;

import org.example.model.Order;
import org.example.model.OrderWarehouse;
import org.example.model.Warehouse;
import org.example.repository.OrderRepository;
import org.example.repository.OrderWarehouseRepository;
import org.example.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class DeliveryCoService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderWarehouseRepository orderWarehouseRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    private final Random random = new Random();

    @Transactional
    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    public void processPaidOrders() {
        // Step 1: Find all orders with status 'paid' or those in transit
        List<Order> orders = orderRepository.findByStatusIn(List.of("paid", "packaged", "on the way", "delivered"));

        for (Order order : orders) {
            Long orderId = order.getOrderId();

            if (order.getStatus().equals("paid")) {
                Long productId = order.getProduct().getProductId();

                // Step 2: Find all OrderWarehouse entries for the given orderId
                List<OrderWarehouse> orderWarehouses = orderWarehouseRepository.findByOrder_OrderId(orderId);

                for (OrderWarehouse orderWarehouse : orderWarehouses) {
                    String warehouseId = orderWarehouse.getWarehouse().getWarehouseId();
                    Integer quantity = orderWarehouse.getQuantity();

                    // Step 3: Find the corresponding Warehouse entry and update stock level
                    Warehouse warehouse = warehouseRepository.findByWarehouseId(warehouseId).orElseThrow(() ->
                            new IllegalArgumentException("Warehouse not found for id: " + warehouseId));
                    if (warehouse.getProduct().getProductId().equals(productId)) {
                        warehouse.setStockLevel(warehouse.getStockLevel() - quantity);
                        warehouseRepository.save(warehouse);
                    }
                }

                // Update order status to 'packaged'
                order.setStatus("packaged");
                orderRepository.save(order);
            } else {
                // 5% chance to lose the package
                if (random.nextInt(100) < 5) {
                    order.setStatus("lost");
                    orderRepository.save(order);
                    continue;
                }

                // Update the status to the next stage
                switch (order.getStatus()) {
                    case "packaged" -> order.setStatus("on the way");
                    case "on the way" -> order.setStatus("delivered");
                    case "delivered" -> order.setStatus("finished");
                }

                orderRepository.save(order);
            }
        }
    }
}
