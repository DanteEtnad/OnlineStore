package org.example.repository;

import org.example.model.OrderWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderWarehouseRepository extends JpaRepository<OrderWarehouse, Long> {
    List<OrderWarehouse> findByOrder_OrderId(Long orderId);
}