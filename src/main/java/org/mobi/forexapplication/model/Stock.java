package org.mobi.forexapplication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;           // Stock Id

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "stock_price", nullable = false)
    private Double stockPrice;      // Stock Price

    @Size(max = 100)
    @Column(name = "company_name")
    private String companyName;     // Stock name

    @Size(max = 50)
    @Column(name = "ticker_symbol", nullable = false)
    private String tickerSymbol;    // Stock Display name

    @Size(max = 50)
    @Column(name = "exchange")
    private String exchange;        // Stock-Exchange

    @Size(max = 250)
    @Column(name = "sector")
    private String sector;          // Sector of the stock

    @Min(value = 0)
    @Column(name = "ipo_qty")
    private Integer ipoQty;         // Initial offering quantity to users

    @Size(max = 255)
    @Column(name = "imageurl")
    private String imageURL;        // Stock image

    @Size(max = 500)
    @Column(name = "description")
    private String description;            // Stock description

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;    // Created date

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    // Updated date

    // Lifecycle callback methods for auto-updating `updatedAt`
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Integer getIpoQty() {
        return ipoQty;
    }

    public void setIpoQty(Integer ipoQty) {
        this.ipoQty = ipoQty;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
