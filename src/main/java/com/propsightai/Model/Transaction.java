package com.propsightai.Model;

import com.propsightai.Role.PurposeType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "TransactionID"
    )
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User buyerOrRenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PropertyID", nullable = false)
    private Property property;

    @Column(
            name = "Amount",
            nullable = false
    )
    private Double amount; // price of property / rent amount

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurposeType purpose; // SALE or RENT

    @Column(name = "TransactionDate", nullable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    public void prePersist() {
        this.transactionDate = LocalDateTime.now();
    }

    //getters / setters


    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public PurposeType getPurpose() {
        return purpose;
    }

    public Double getAmount() {
        return amount;
    }

    public Property getProperty() {
        return property;
    }

    public User getBuyerOrRenter() {
        return buyerOrRenter;
    }

    public Integer getId() {
        return id;
    }

    public void setBuyerOrRenter(User buyerOrRenter) {
        this.buyerOrRenter = buyerOrRenter;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPurpose(PurposeType purpose) {
        this.purpose = purpose;
    }

}

