package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.dto.BalanceUpdateRequest;
import org.mobi.forexapplication.model.Transaction;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(HttpSession session){
        BigDecimal balance=accountService.getBalance();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/ledger")
    public ResponseEntity<List<Transaction>> getLedger(){
        List<Transaction> ledger=accountService.getLedger();
        return ResponseEntity.ok(ledger);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(){
        User user=accountService.getProfile();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User updatedInfo) {
        User updatedUser = accountService.updateProfile(updatedInfo);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody BalanceUpdateRequest request){
        accountService.deposit(request.getAmount());
        return ResponseEntity.ok("Deposit Successful");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody BalanceUpdateRequest request){
        boolean success=accountService.withdraw(request.getAmount());
        if(success){
            return ResponseEntity.ok("withdraw Successful");
        }
        else return ResponseEntity.badRequest().body("Insufficient balance");
    }
}
