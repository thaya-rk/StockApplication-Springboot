package org.mobi.forexapplication.controller;

import org.mobi.forexapplication.Exception.GlobalCustomException;
import org.mobi.forexapplication.dto.StockDeleteRequest;
import org.mobi.forexapplication.dto.StockUpdateRequest;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StockService stockService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(auth) || !auth.isAuthenticated()) {
            throw new GlobalCustomException("Unauthorized access. Please log in.");
        }

        String username = auth.getName();

        return ResponseEntity.ok("Welcome Admin: " + username);
    }

    @PostMapping("/stocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        if (stock == null || stock.getTickerSymbol() == null || stock.getStockPrice() == null) {
            throw new GlobalCustomException("Invalid stock data.");
        }
        Stock createdStock = stockService.addStock(stock);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @PutMapping("/stocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Stock> updateStockPrice(@RequestBody StockUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() == null || updateRequest.getNewPrice() == null) {
            throw new GlobalCustomException("Invalid update request.");
        }
        Stock updatedStock = stockService.updateStockPrice(updateRequest.getId(), updateRequest.getNewPrice());
        return new ResponseEntity<>(updatedStock, HttpStatus.OK);
    }

    @DeleteMapping("/stocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStock(@RequestBody StockDeleteRequest request) {
        if (request == null || request.getId() == null) {
            throw new GlobalCustomException("Invalid delete request.");
        }
        stockService.deleteStock(request.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
