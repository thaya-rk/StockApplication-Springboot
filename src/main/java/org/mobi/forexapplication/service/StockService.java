package org.mobi.forexapplication.service;

import org.mobi.forexapplication.model.Stock;

import java.util.List;

public interface StockService {
    Stock addStock(Stock stock);
    Stock updateStockPrice(Long id, Double newPrice);
    void deleteStock(Long id);
    List<Stock> getAllStocks();

}
