package org.mobi.forexapplication.controller;

import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;
import org.mobi.forexapplication.dto.PortfolioSummaryDTO;
import org.mobi.forexapplication.dto.StockStatsDTO;
import org.mobi.forexapplication.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return portfolioService.getUserIdByUsername(username);
        }

        throw new RuntimeException("Invalid user principal");
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>> buyStock(@RequestBody BuySellRequest request) {
        Long userId = getCurrentUserId();
        request.setUserId(userId);
        portfolioService.buyStock(request);
        return ResponseEntity.ok(Map.of("message", "Stock bought successfully"));
    }

    @PostMapping("/sell")
    public ResponseEntity<Map<String, String>> sellStock(@RequestBody BuySellRequest request) {
        Long userId = getCurrentUserId();
        request.setUserId(userId);
        portfolioService.sellStock(request);
        return ResponseEntity.ok(Map.of("message", "Stock sold successfully"));
    }

    @GetMapping("/holdings")
    public ResponseEntity<List<HoldingResponse>> getHoldings() {
        Long userId = getCurrentUserId();
        List<HoldingResponse> holdings = portfolioService.getHoldingsByUserId(userId);
        return ResponseEntity.ok(holdings);
    }


    @GetMapping("/summary/{userId}")
    public ResponseEntity<PortfolioSummaryDTO> getPortfolioSummary(@PathVariable Long userId) {
        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/stats/{stockId}")
    public ResponseEntity<StockStatsDTO> getStockStats(@PathVariable Long stockId) {
        Long userId = getCurrentUserId();
        StockStatsDTO stats = portfolioService.getStockStats(userId, stockId);
        return ResponseEntity.ok(stats);
    }



}
