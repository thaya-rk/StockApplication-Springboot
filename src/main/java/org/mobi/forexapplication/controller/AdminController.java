package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.dto.StockDeleteRequest;
import org.mobi.forexapplication.dto.StockUpdateRequest;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private StockService stockService;

    @GetMapping()
    public ResponseEntity<String> adminDashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        System.out.println(user.getRole());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in.");
        }

        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only.");
        }

        return ResponseEntity.ok("Welcome Admin: " + user.getUsername());
    }

    @PostMapping("/stocks")
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        System.out.println("Added stock");
        Stock createdStock = stockService.addStock(stock);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @PutMapping("/stocks")
    public ResponseEntity<Stock> updateStockPrice(@RequestBody StockUpdateRequest updateRequest) {
        Stock updatedStock = stockService.updateStockPrice(updateRequest.getId(), updateRequest.getNewPrice());
        return new ResponseEntity<>(updatedStock, HttpStatus.OK);
    }


    @DeleteMapping("/stocks")
    public ResponseEntity<Void> deleteStock(@RequestBody StockDeleteRequest request) {
        stockService.deleteStock(request.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

