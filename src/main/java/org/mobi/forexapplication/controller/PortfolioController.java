package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;
import org.mobi.forexapplication.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>>  buyStock(@RequestBody BuySellRequest request, HttpSession session){
        portfolioService.buyStock(session,request);
        return ResponseEntity.ok(Map.of("message", "Stock bought successfully"));    }

    @PostMapping("/sell")
    public ResponseEntity<Map<String, String>> sellStock(@RequestBody BuySellRequest request, HttpSession session){
        portfolioService.sellStock(session, request);
        return ResponseEntity.ok(Map.of("message", "Stock sold successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<HoldingResponse>> getHoldings(@PathVariable Long userId) {
        List<HoldingResponse> holdings = portfolioService.getHoldingsByUserId(userId);
        return ResponseEntity.ok(holdings);
    }

}
