package org.mobi.forexapplication.service;

import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;

import java.util.List;

public interface PortfolioService {
    void buyStock(BuySellRequest request);
    void sellStock(BuySellRequest request);
    List<HoldingResponse> getHoldingsByUserId(Long userId);
    Long getUserIdByUsername(String username);
}
