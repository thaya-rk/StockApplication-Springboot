package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class PaymentController {
    @PostMapping("/api/payment-status")
    public void handlePaymentResponse(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        // Extract required values
        System.out.println("Received FPX payload: " + params);
        String status = params.get("fpx_debitAuthCode").equals("00") ? "success" : "failed";
        String orderNo = params.get("fpx_sellerOrderNo");
        String txnId = params.get("fpx_fpxTxnId");

        // You can log/store full payload here

        // Redirect to Angular payment-status page with status
        String redirectUrl = String.format("http://localhost:4200/payment-status?status=%s&orderNo=%s&transactionId=%s",
                status, orderNo, txnId);

        response.sendRedirect(redirectUrl);
    }
}
