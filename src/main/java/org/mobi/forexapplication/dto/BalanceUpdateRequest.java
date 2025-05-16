package org.mobi.forexapplication.dto;

import java.math.BigDecimal;

public class BalanceUpdateRequest {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
