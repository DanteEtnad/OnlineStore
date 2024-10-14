package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bank")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double balance = 0.00;

    public Long getAccountId() {return accountId;}

    public double getBalance() {return balance;}

    public String getName() {return name;}

    public String getAccountType() {return accountType;}

    public void setBalance(double trans) {
        this.balance = this.balance + trans;
    }

    public void setAccountType(String accountType) {this.accountType = accountType;}

    public void setName(String name) {this.name = name;}
}
