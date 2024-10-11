package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Getter for customerId
    public Long getCustomerId() {
        return customerId;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }
}
