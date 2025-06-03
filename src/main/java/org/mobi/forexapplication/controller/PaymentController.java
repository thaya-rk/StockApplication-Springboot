package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mobi.forexapplication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/api/payment-status")
    public void handlePaymentResponse(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        // Extract required values
        System.out.println("Received FPX payload: " + params);
        String status = params.get("fpx_debitAuthCode").equals("00") ? "success" : "failed";
        String orderNo = params.get("fpx_sellerOrderNo");
        String txnId = params.get("fpx_fpxTxnId");

        try {
            if ("00".equals(params.get("fpx_debitAuthCode"))) {
                status = "success";

                // Convert amount string (e.g., "10.00") to BigDecimal
                BigDecimal amount = new BigDecimal(params.get("fpx_txnAmount"));

                // ✅ Reuse existing method
                System.out.println("Amount to deposit: " + amount);

                accountService.deposit(amount,txnId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Catch is called in payment controller");
            status = "failed";
        }

        // Redirect to Angular payment-status page with status
        String redirectUrl = String.format("http://localhost:4200/payment-status?status=%s&orderNo=%s&transactionId=%s",
                status, orderNo, txnId);

        response.sendRedirect(redirectUrl);
    }
}
