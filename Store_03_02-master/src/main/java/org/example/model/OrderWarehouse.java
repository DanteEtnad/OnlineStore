package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_warehouse")
public class OrderWarehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "orderId")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", referencedColumnName = "warehouseId")
    private Warehouse warehouse;

    @Column(nullable = false)
    private Integer quantity;

    // Getter for warehouse
    public Warehouse getWarehouse() {
        return warehouse;
    }

    // Getter for order
    public Order getOrder() {
        return order;
    }

    // Getter for quantity
    public Integer getQuantity() {
        return quantity;
    }
}
