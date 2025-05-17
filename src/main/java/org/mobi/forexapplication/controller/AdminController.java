package org.mobi.forexapplication.controller;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StockService stockService;

    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> adminDashboard() {
        System.out.println("url hit");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in.");
        }

        // Get username (works if principal is a String or UserDetails)
        String username = auth.getName();
        System.out.println("Username: " + username);

        // Get roles/authorities as a comma separated string
        String roles = auth.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .reduce((a, b) -> a + ", " + b)
                .orElse("No roles");

        System.out.println("Roles: " + roles);

        return ResponseEntity.ok("Welcome Admin: " + username);
    }


    @PostMapping("/stocks")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        Stock createdStock = stockService.addStock(stock);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @PutMapping("/stocks")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Stock> updateStockPrice(@RequestBody StockUpdateRequest updateRequest) {
        Stock updatedStock = stockService.updateStockPrice(updateRequest.getId(), updateRequest.getNewPrice());
        return new ResponseEntity<>(updatedStock, HttpStatus.OK);
    }

    @DeleteMapping("/stocks")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteStock(@RequestBody StockDeleteRequest request) {
        stockService.deleteStock(request.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
