package org.mobi.forexapplication.dto;

import java.math.BigDecimal;

public class TransactionChargesDTO {
    private BigDecimal brokerage;
    private BigDecimal stampDuty;
    private BigDecimal transactionTax;
    private BigDecimal sebiCharges;
    private BigDecimal gst;
    private BigDecimal totalCharges;

    // Getters and Setters

    // Constructors
    public TransactionChargesDTO() {}

    public TransactionChargesDTO(BigDecimal brokerage, BigDecimal stampDuty, BigDecimal transactionTax,
                                 BigDecimal sebiCharges, BigDecimal gst, BigDecimal totalCharges) {
        this.brokerage = brokerage;
        this.stampDuty = stampDuty;
        this.transactionTax = transactionTax;
        this.sebiCharges = sebiCharges;
        this.gst = gst;
        this.totalCharges = totalCharges;
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
}
