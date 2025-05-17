package org.mobi.forexapplication.dto;


public class BuySellRequest {
    private Long stockId;
    private int quantity;
    private Long userId;


    public BuySellRequest() {}

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

}
