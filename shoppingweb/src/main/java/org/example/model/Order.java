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
    private Customer customer;  // Customer object

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "productId")
    private Product product;  // Product object

    @ManyToOne
    @JoinColumn(name = "bank_transfer_id", referencedColumnName = "bankTransferId")
    private BankTransfer bankTransfer;  // Bank transfer object

    @Column(nullable = false)
    private LocalDateTime orderDate;  // Order date

    @Column(nullable = false)
    private Integer quantity;  // Order quantity

    @Column(nullable = false)
    private Double totalAmount;  // Order total amount

    @Column(nullable = false)
    private String status = "pending";  // Order status

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

    // Getter for quantity
    public Integer getQuantity() {
        return quantity;
    }

    // Getter for customer
    public Customer getCustomer() {
        return customer;
    }

    // Getter for totalAmount
    public Double getTotalAmount() {
        return totalAmount;
    }

    // Getter for bankTransfer
    public BankTransfer getBankTransfer() {
        return bankTransfer;
    }

    // Getter for orderDate
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Setter for product
    public void setProduct(Product product) {
        this.product = product;
    }

    // Setter for quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Setter for totalAmount
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Setter for bankTransfer
    public void setBankTransfer(BankTransfer bankTransfer) {
        this.bankTransfer = bankTransfer;
    }

    // Setter for orderDate
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    // Setter for customer
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
