package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_transfer")
public class BankTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankTransferId;

    @Column(nullable = false)
    private Long fromAccount;

    @Column(nullable = false)
    private Long toAccount;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status = "pending";

    public Long getBankTransferId() {return this.bankTransferId;}

    public Long getFromAccount() {return this.fromAccount;}

    public Long getToAccount() {return this.toAccount;}

    public double getAmount() {return this.amount;}

    public String getStatus() {return this.status;}

    public void setStatus(String status) {this.status = status;}

    public void setFromAccount(Long fromAccount) {this.fromAccount = fromAccount;}

    public void setToAccount(Long toAccount) {this.toAccount = toAccount;}

    public void setAmount(Double amount) {this.amount = amount;}
}
