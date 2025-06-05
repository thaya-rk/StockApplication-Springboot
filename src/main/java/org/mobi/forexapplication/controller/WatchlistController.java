package org.mobi.forexapplication.controller;

import org.mobi.forexapplication.Exception.GlobalCustomException;
import org.mobi.forexapplication.dto.ApiResponse;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.service.StockService;
import org.mobi.forexapplication.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    @Autowired
    private PortfolioController portfolioController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private WatchListService watchListService;

    private User getCurrentUser() {
        Long userId = portfolioController.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalCustomException("User not found"));
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<List<Stock>>> getAllStocks() {
        try {
            List<Stock> allStocks = stockService.getAllStocks();
            return ResponseEntity.ok(new ApiResponse<>("All stocks fetched successfully", allStocks));
        } catch (Exception e) {
            throw new GlobalCustomException("Failed to fetch stocks: " + e.getMessage());
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<Stock>>> getWatchlistedStocks() {
        User user = getCurrentUser();
        List<Stock> watchlistedStocks = watchListService.getWatchlistedStocks(user);
        return ResponseEntity.ok(new ApiResponse<>("Watchlist stocks fetched successfully", watchlistedStocks));
    }

    @PostMapping("/favorites/{stockId}")
    public ResponseEntity<ApiResponse<String>> addToWatchlist(@PathVariable Long stockId) {
        User user = getCurrentUser();
        watchListService.addToWatchlist(user, stockId);
        return ResponseEntity.ok(new ApiResponse<>("Stock added to watchlist.", null));
    }

    @DeleteMapping("/favorites/{stockId}")
    public ResponseEntity<ApiResponse<String>> removeFromWatchlist(@PathVariable Long stockId) {
        User user = getCurrentUser();
        watchListService.removeFromWatchlist(user, stockId);
        return ResponseEntity.ok(new ApiResponse<>("Stock removed from watchlist.", null));
    }
}

