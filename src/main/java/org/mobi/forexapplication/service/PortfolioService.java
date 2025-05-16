package org.mobi.forexapplication.service;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;

import java.util.List;


public interface PortfolioService {
    void buyStock(HttpSession session,BuySellRequest request);
    void sellStock(HttpSession session,BuySellRequest request);
    List<HoldingResponse> getHoldingsByUserId(Long userId);


}
