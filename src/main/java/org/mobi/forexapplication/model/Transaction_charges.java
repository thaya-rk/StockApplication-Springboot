package org.mobi.forexapplication.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_charges")
public class Transaction_charges {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(precision = 19, scale = 4)
    private BigDecimal brokerage = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    private BigDecimal stampDuty = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    private BigDecimal transactionTax = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    private BigDecimal sebiCharges = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    private BigDecimal gst = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalCharges = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public BigDecimal getTransactionTax() {
        return transactionTax;
    }

    public void setTransactionTax(BigDecimal transactionTax) {
        this.transactionTax = transactionTax;
    }

    public BigDecimal getSebiCharges() {
        return sebiCharges;
    }

    public void setSebiCharges(BigDecimal sebiCharges) {
        this.sebiCharges = sebiCharges;
    }

    public BigDecimal getGst() {
        return gst;
    }

    public void setGst(BigDecimal gst) {
        this.gst = gst;
    }

    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
