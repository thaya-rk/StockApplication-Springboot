package org.mobi.forexapplication.dto;

public class StockUpdateRequest {
    private Long id;
    private Double newPrice;

    public Double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
