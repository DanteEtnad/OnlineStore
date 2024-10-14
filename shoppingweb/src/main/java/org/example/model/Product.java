package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Double price;

    @Column
    private String description;

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public String getProductName() {return productName;}

    public Double getPrice() {return price;}

    public String getDescription() {return description;}
}
