package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String warehouseId;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "productId")
    private Product product;

    @Column(nullable = false)
    private Integer stockLevel;

    // Getter for id
    public Long getId() {
        return id;
    }

    // Getter for warehouseId
    public String getWarehouseId() {
        return warehouseId;
    }

    // Getter for product
    public Product getProduct() {
        return product;
    }

    // Getter for stockLevel
    public Integer getStockLevel() {
        return stockLevel;
    }

    // Setter for stockLevel
    public void setStockLevel(Integer stockLevel) {
        this.stockLevel = stockLevel;
    }
}

