package org.mobi.forexapplication.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "stock_id", nullable = true)
    private Stock stock;

    @Column(nullable = true)
    private Integer quantity;

    @Column(name = "total_amt", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false, length = 10)
    private String type; // BUY or SELL


    @Column(nullable = false, length = 10)
    private String status; // SUCCESS, FAILED, PENDING

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Transaction(User user, Stock stock, String type, Integer quantity, BigDecimal price) {
        this.user = user;
        this.stock = stock;
        this.type = type;
        this.quantity = quantity;
        this.totalAmount = price.multiply(BigDecimal.valueOf(quantity));
        this.status = "SUCCESS"; // You can change this as per your logic
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(User user, String type, BigDecimal amount, LocalDateTime transactionDate) {
        this.user = user;
        this.type = type;
        this.totalAmount = amount;
        this.status = "SUCCESS";
        this.transactionDate = transactionDate;
    }

    public Transaction() {

    }

    @PrePersist
    public void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
