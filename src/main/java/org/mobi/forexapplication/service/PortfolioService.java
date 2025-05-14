package org.mobi.forexapplication.service;

import org.mobi.forexapplication.dto.BuySellRequest;

public interface PortfolioService {
    void buyStock(BuySellRequest request);
    void sellStock(BuySellRequest request);
}
