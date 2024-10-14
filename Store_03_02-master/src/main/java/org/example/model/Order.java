package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "bank_transfer_id", referencedColumnName = "bankTransferId")
    private BankTransfer bankTransfer;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String status = "pending";

    // Getter for orderId
    public Long getOrderId() {
        return orderId;
    }

    // Getter for product
    public Product getProduct() {
        return product;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Getter for customer
    public Customer getCustomer() {
        return customer;
    }

    // Getter for totalamount
    public Double getTotalAmount() {return totalAmount;}

    public BankTransfer getBankTransfer() {return bankTransfer;}

    // Setter
    public void setStatus(String status) {
        this.status = status;
    }

    public void setProduct(Product product) {this.product = product;}

    public void setQuantity(Integer quantity) {this.quantity = quantity;}

    public void setTotalAmount(Double totalAmount) {this.totalAmount = totalAmount;}

    public void setBankTransfer(BankTransfer bankTransfer) {this.bankTransfer = bankTransfer;}

}
