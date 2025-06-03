package org.mobi.forexapplication.controller;

import org.mobi.forexapplication.Exception.GlobalCustomException;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    @Autowired
    private StockService stockService;

    @GetMapping("/default")
    public ResponseEntity<List<Stock>> getAllStocks() {
        try {
            List<Stock> allStocks = stockService.getAllStocks();
            return ResponseEntity.ok(allStocks);
        } catch (Exception e) {
            throw new GlobalCustomException("Failed to fetch stocks: " + e.getMessage());
        }
    }

    
}
