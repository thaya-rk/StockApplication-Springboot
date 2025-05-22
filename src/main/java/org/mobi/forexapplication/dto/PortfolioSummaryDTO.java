// PortfolioSummaryDTO.java
package org.mobi.forexapplication.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioSummaryDTO {
    private BigDecimal portfolioValue;
    private BigDecimal totalPL;
    private BigDecimal portfolioGainPercent;
    private List<ChargeDetailDTO> charges;

    public PortfolioSummaryDTO(BigDecimal portfolioValue, BigDecimal totalPL, BigDecimal portfolioGainPercent, List<ChargeDetailDTO> charges) {
        this.portfolioValue = portfolioValue;
        this.totalPL = totalPL;
        this.portfolioGainPercent = portfolioGainPercent;
        this.charges = charges;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public BigDecimal getTotalPL() {
        return totalPL;
    }

    public void setTotalPL(BigDecimal totalPL) {
        this.totalPL = totalPL;
    }

    public BigDecimal getPortfolioGainPercent() {
        return portfolioGainPercent;
    }

    public void setPortfolioGainPercent(BigDecimal portfolioGainPercent) {
        this.portfolioGainPercent = portfolioGainPercent;
    }

    public List<ChargeDetailDTO> getCharges() { return charges; }
    public void setCharges(List<ChargeDetailDTO> charges) { this.charges = charges; }

}
