package org.mobi.forexapplication.controller;

import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
