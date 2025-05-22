package org.mobi.forexapplication.service;

import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;
import org.mobi.forexapplication.dto.PortfolioSummaryDTO;
import org.mobi.forexapplication.dto.StockStatsDTO;
import org.mobi.forexapplication.dto.TransactionChargesDTO;


import java.util.List;

public interface PortfolioService {
    void buyStock(BuySellRequest request);
    void sellStock(BuySellRequest request);
    List<HoldingResponse> getHoldingsByUserId(Long userId);
    Long getUserIdByUsername(String username);

    PortfolioSummaryDTO getPortfolioSummary(Long userId);
    StockStatsDTO getStockStats(Long userId, Long stockId);
    TransactionChargesDTO calculateTransactionCharges(Long userId, Long stockId, int quantity);

}
