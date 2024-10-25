package org.example.Service;

import org.example.model.Order;
import org.example.model.OrderWarehouse;
import org.example.model.Warehouse;
import org.example.repository.OrderWarehouseRepository;
import org.example.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderWarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private OrderWarehouseRepository orderWarehouseRepository;

    @Transactional
    public void allocateWarehouseForOrder(Order order) {
        Long productId = order.getProduct().getProductId();
        int quantityNeeded = order.getQuantity();

        // Find all warehouses that contain the product
        List<Warehouse> warehouses = warehouseRepository.findByProduct_ProductIdOrderByStockLevelDesc(productId);

        int quantityAllocated = 0;

        for (Warehouse warehouse : warehouses) {
            if (quantityAllocated >= quantityNeeded) {
                break;
            }

            int availableStock = warehouse.getStockLevel();
            if (availableStock > 0) {
                int quantityToAllocate = Math.min(availableStock, quantityNeeded - quantityAllocated);

                // Create an entry in the order_warehouse table without modifying warehouse stock level yet
                OrderWarehouse orderWarehouse = new OrderWarehouse();
                orderWarehouse.setOrder(order);
                orderWarehouse.setWarehouse(warehouse);
                orderWarehouse.setQuantity(quantityToAllocate);
                orderWarehouseRepository.save(orderWarehouse);

                quantityAllocated += quantityToAllocate;
            }
        }

        if (quantityAllocated < quantityNeeded) {
            throw new IllegalArgumentException("Not enough stock available to fulfill the order.");
        }
    }
}
