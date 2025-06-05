package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mobi.forexapplication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/api/payment-status")
    public void handlePaymentResponse(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        System.out.println("Received FPX payload: " + params);

        String debitAuthCode = params.get("fpx_debitAuthCode");
        String status = "00".equals(debitAuthCode) ? "success" : "failed";
        String orderNo = params.get("fpx_sellerOrderNo");
        String txnId = params.get("fpx_fpxTxnId");
        System.out.println("The response status from the fpx: " + status + " with order no: " + orderNo);

        try {
            if ("00".equals(debitAuthCode)) {
                BigDecimal amount = new BigDecimal(params.get("fpx_txnAmount"));
                System.out.println("Amount to deposit: " + amount);

                String[] split = orderNo.split("U");
                if (split.length == 2) {
                    String userPart = split[1].split("_")[0];
                    Long userId = Long.parseLong(userPart);
                    System.out.println("Extracted User ID: " + userId);

                    accountService.depositToUser(amount, txnId, userId);
                } else {
                    System.out.println(Arrays.toString(split));
                    System.out.println("Invalid order number format. Cannot extract user ID.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in payment controller");
            status = "failed";
        }

        // Redirect to frontend status page
        String redirectUrl = String.format("http://localhost:4200/payment-status?status=%s&orderNo=%s&transactionId=%s",
                status, orderNo, txnId);
        response.sendRedirect(redirectUrl);
    }
}
