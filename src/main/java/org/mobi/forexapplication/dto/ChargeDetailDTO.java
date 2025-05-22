package org.mobi.forexapplication.dto;

import java.math.BigDecimal;

public class ChargeDetailDTO {
    private String chargeType;
    private BigDecimal amount;

    public ChargeDetailDTO(String chargeType, BigDecimal amount) {
        this.chargeType = chargeType;
        this.amount = amount;
    }

    // Getters and setters
    public String getChargeType() { return chargeType; }
    public void setChargeType(String chargeType) { this.chargeType = chargeType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
