package org.mobi.forexapplication.controller;

import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;
import org.mobi.forexapplication.service.PortfolioService;
import org.mobi.forexapplication.security.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    private Long extractUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid JWT token");
        }
        return jwtUtil.getUserIdFromToken(token);
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>> buyStock(@RequestBody BuySellRequest request,
                                                        @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromAuthHeader(authHeader);
        System.out.println("Extracted userId: " + userId);
        request.setUserId(userId);
        portfolioService.buyStock(request);
        return ResponseEntity.ok(Map.of("message", "Stock bought successfully"));
    }

    @PostMapping("/sell")
    public ResponseEntity<Map<String, String>> sellStock(@RequestBody BuySellRequest request,
                                                         @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromAuthHeader(authHeader);
        request.setUserId(userId);
        portfolioService.sellStock(request);
        return ResponseEntity.ok(Map.of("message", "Stock sold successfully"));
    }

    @GetMapping("/holdings")
    public ResponseEntity<List<HoldingResponse>> getHoldings(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromAuthHeader(authHeader);
        List<HoldingResponse> holdings = portfolioService.getHoldingsByUserId(userId);
        return ResponseEntity.ok(holdings);
    }
}