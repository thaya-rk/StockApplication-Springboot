package org.mobi.forexapplication.controller;

import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;
import org.mobi.forexapplication.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(@RequestBody BuySellRequest request){
        portfolioService.buyStock(request);
        return ResponseEntity.ok("Stock brought Successfully");
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestBody BuySellRequest request){
        portfolioService.sellStock(request);
        return ResponseEntity.ok("Stock sold Successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<HoldingResponse>> getHoldings(@PathVariable Long userId) {
        List<HoldingResponse> holdings = portfolioService.getHoldingsByUserId(userId);
        return ResponseEntity.ok(holdings);
    }

}
