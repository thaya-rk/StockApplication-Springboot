// StockStatsDTO.java
package org.mobi.forexapplication.dto;

import java.math.BigDecimal;

public class StockStatsDTO {
    private String companyName;
    private int quantity;
    private BigDecimal currentPrice;
    private BigDecimal totalValue; // quantity * currentPrice
    private BigDecimal pl;         // profit/loss absolute
    private BigDecimal plPercent;  // profit/loss %
    private BigDecimal totalCharges;

    public StockStatsDTO(String companyName, int quantity, BigDecimal currentPrice, BigDecimal totalValue, BigDecimal pl, BigDecimal plPercent,BigDecimal totalCharges) {
        this.companyName = companyName;
        this.quantity = quantity;
        this.currentPrice = currentPrice;
        this.totalValue = totalValue;
        this.pl = pl;
        this.plPercent = plPercent;
        this.totalCharges=totalCharges;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getPl() {
        return pl;
    }

    public void setPl(BigDecimal pl) {
        this.pl = pl;
    }

    public BigDecimal getPlPercent() {
        return plPercent;
    }

    public void setPlPercent(BigDecimal plPercent) {
        this.plPercent = plPercent;
    }

    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }
}
