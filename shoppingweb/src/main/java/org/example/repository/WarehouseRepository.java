package org.example.repository;

import org.example.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Warehouse> findByWarehouseId(String warehouseId);

    List<Warehouse> findByProduct_ProductIdOrderByStockLevelDesc(Long productId);
}
