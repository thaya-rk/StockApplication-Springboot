package org.mobi.forexapplication.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.mobi.forexapplication.dto.ChecksumRequest;
import org.mobi.forexapplication.service.AccountService;
import org.mobi.forexapplication.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private AccountService accountService;


    @PostMapping("/generate-checksum")
    public ResponseEntity<Map<String, String>> generateChecksum(@RequestBody ChecksumRequest request) {
        String minifiedString = String.join("|", request.getAmount(), request.getSellerOrderNo(), request.getSubMID()
        );

        String param1 = request.getMid(); // MID as encryption key
        String param2 = request.getTid(); // TID as salt

        String checksum = EncryptionUtil.encryptPayload(minifiedString, param1, param2);
        Map<String, String> response = new HashMap<>();
        System.out.println("The generated checksum is " + checksum);
        response.put("checksum", checksum);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment-status")
    public void handlePaymentResponse(@RequestParam Map<String, String> params,
                                      HttpServletResponse response) throws IOException {

        String debitAuthCode = params.getOrDefault("fpx_debitAuthCode", "");
        String debitAuthCodeString = params.getOrDefault("fpx_debitAuthCodeString", "");
        String orderNo = params.getOrDefault("fpx_sellerOrderNo", "MISSING");
        String txnId = params.getOrDefault("fpx_fpxTxnId", "");
        BigDecimal amount = new BigDecimal(params.getOrDefault("fpx_txnAmount", "0"));
        String status = "00".equals(debitAuthCode) ? "success" : "failed";

        System.out.printf("➤ FPX Code: %s (%s)\n", debitAuthCode, debitAuthCodeString);
        System.out.printf("➤ Status: %s, OrderNo: %s, TxnId: %s, Amount: %s\n", status, orderNo, txnId, amount);

        if ("success".equals(status)) {
            try {
                String[] split = orderNo.split("U");
                if (split.length == 2) {
                    String userPart = split[1].split("_")[0];
                    Long userId = Long.parseLong(userPart);
                    accountService.depositToUser(amount, txnId, userId);
                } else {
                    System.err.println("❗ Invalid order number format: " + orderNo);
                    status = "failed";
                    accountService.recordFailedTransaction(txnId, orderNo, amount);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("❗ Deposit error, marking as failed.");
                status = "failed";
                accountService.recordFailedTransaction(txnId, orderNo, amount);
            }
        } else {
            accountService.recordFailedTransaction(txnId, orderNo, amount);
        }

        String redirectUrl = UriComponentsBuilder
                .fromHttpUrl("http://localhost:4200/payment-status")
                .queryParam("status", status)
                .queryParam("orderNo", orderNo)
                .queryParam("transactionId", txnId)
                .queryParam("code", debitAuthCode)
                .queryParam("msg", debitAuthCodeString)
                .build()
                .encode()
                .toUriString();

        System.out.println("⇢ Redirecting to: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}