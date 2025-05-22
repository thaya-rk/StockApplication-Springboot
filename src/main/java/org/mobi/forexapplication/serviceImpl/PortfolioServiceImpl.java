package org.mobi.forexapplication.serviceImpl;

import jakarta.transaction.Transactional;
import org.mobi.forexapplication.dto.*;
import org.mobi.forexapplication.model.*;
import org.mobi.forexapplication.repository.*;
import org.mobi.forexapplication.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionChargesRepository transactionChargesRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    @Transactional
    public void buyStock(BuySellRequest request) {

        Long userId = request.getUserId();
        User user = getUserById(userId);


        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        BigDecimal quantity = BigDecimal.valueOf(request.getQuantity());
        BigDecimal currentPrice = BigDecimal.valueOf(stock.getStockPrice());
        BigDecimal totalPrice = currentPrice.multiply(quantity);

        if (user.getDematBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient balance to buy stock");
        }

        // Deduct balance
        user.setDematBalance(user.getDematBalance().subtract(totalPrice));
        userRepository.save(user);

        Holdings holdings = holdingRepository.findByUser_UserIdAndStock_StockId(user.getUserId(), stock.getStockId())
                .orElse(null);

        if (holdings != null) {
            BigDecimal existingQty = BigDecimal.valueOf(holdings.getQuantity());
            BigDecimal totalQty = existingQty.add(quantity);

            BigDecimal existingTotal = holdings.getAvgBuyPrice().multiply(existingQty);
            BigDecimal newTotal = currentPrice.multiply(quantity);

            BigDecimal newAvgPrice = existingTotal.add(newTotal).divide(totalQty, 2, RoundingMode.HALF_EVEN);

            holdings.setQuantity(totalQty.intValue());
            holdings.setAvgBuyPrice(newAvgPrice);
            holdingRepository.save(holdings);
        } else {
            Holdings newHolding = new Holdings();
            newHolding.setUser(user);
            newHolding.setStock(stock);
            newHolding.setQuantity(quantity.intValue());
            newHolding.setAvgBuyPrice(currentPrice);
            holdingRepository.save(newHolding);
        }

        // Record transaction
        Transaction txn = new Transaction(user, stock, "BUY", quantity.intValue(), currentPrice);
        transactionRepository.save(txn);
    }

    @Override
    @Transactional
    public void sellStock(BuySellRequest request) {
        Long userId = request.getUserId();
        User user = getUserById(userId);


        Holdings holdings = holdingRepository.findByUser_UserIdAndStock_StockId(user.getUserId(), request.getStockId())
                .orElseThrow(() -> new RuntimeException("No holdings found for the stock"));

        int sellQty = request.getQuantity();
        if (holdings.getQuantity() < sellQty) {
            throw new RuntimeException("Not enough quantity to sell");
        }

        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        BigDecimal currentPrice = BigDecimal.valueOf(stock.getStockPrice());
        BigDecimal totalPrice = currentPrice.multiply(BigDecimal.valueOf(sellQty));

        // Update holdings
        holdings.setQuantity(holdings.getQuantity() - sellQty);
        holdingRepository.save(holdings);

        // Add balance to user
        user.setDematBalance(user.getDematBalance().add(totalPrice));
        userRepository.save(user);

        // Record transaction
        Transaction txn = new Transaction(user, stock, "SELL", sellQty, currentPrice);
        transactionRepository.save(txn);
    }

    @Override
    public List<HoldingResponse> getHoldingsByUserId(Long userId) {
        return holdingRepository.findByUser_UserId(userId)
                .stream()
                .map(holding -> {
                    Stock stock = holding.getStock();
                    BigDecimal currentPrice = BigDecimal.valueOf(stock.getStockPrice());
                    BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(holding.getQuantity()));
                    return new HoldingResponse(
                            stock.getCompanyName(),
                            holding.getQuantity(),
                            holding.getAvgBuyPrice(),
                            currentPrice,
                            currentValue
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return user.getUserId();
    }

    private BigDecimal calculateBrokerage(Long userId) {
        // Brokerage is 20 INR per transaction (buy or sell)
        List<Transaction> transactions = transactionRepository.findByUser_UserId(userId);
        int transactionCount = transactions.size();
        return BigDecimal.valueOf(transactionCount).multiply(BigDecimal.valueOf(20));
    }

    private BigDecimal calculateStampCharges(Long userId) {
        // Stamp charges are 0.015% on buy side only
        List<Transaction> transactions = transactionRepository.findByUser_UserId(userId);
        BigDecimal totalBuyAmount = transactions.stream()
                .filter(tx -> "BUY".equalsIgnoreCase(tx.getType()))
                .map(Transaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 0.015% = 0.00015
        return totalBuyAmount.multiply(BigDecimal.valueOf(0.00015));
    }

    private BigDecimal calculateTransactionCharges(Long userId) {
        // NSE Transaction charges 0.00297% on total txn amount (both buy and sell)
        List<Transaction> transactions = transactionRepository.findByUser_UserId(userId);
        BigDecimal totalTxnAmount = transactions.stream()
                .map(Transaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 0.00297% = 0.0000297
        return totalTxnAmount.multiply(BigDecimal.valueOf(0.0000297));
    }

    private BigDecimal calculateSebICharges(Long userId) {
        // SEBI charges ₹10 per crore (₹10/1,00,00,000) on total txn amount
        List<Transaction> transactions = transactionRepository.findByUser_UserId(userId);
        BigDecimal totalTxnAmount = transactions.stream()
                .map(Transaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal crore = BigDecimal.valueOf(10000000);  // 1 crore
        BigDecimal ratePerCrore = BigDecimal.TEN; // ₹10

        BigDecimal charges = totalTxnAmount.divide(crore, 10, RoundingMode.HALF_UP).multiply(ratePerCrore);
        return charges;
    }




    @Override
    public PortfolioSummaryDTO getPortfolioSummary(Long userId) {
        List<Holdings> holdings = holdingRepository.findByUser_UserId(userId);

        BigDecimal portfolioValue = BigDecimal.ZERO;
        BigDecimal totalPL = BigDecimal.ZERO;

        for (Holdings holding : holdings) {
            BigDecimal currentPrice = BigDecimal.valueOf(holding.getStock().getStockPrice());
            BigDecimal quantity = BigDecimal.valueOf(holding.getQuantity());
            BigDecimal currentValue = currentPrice.multiply(quantity);
            BigDecimal investedValue = holding.getAvgBuyPrice().multiply(quantity);

            portfolioValue = portfolioValue.add(currentValue);
            totalPL = totalPL.add(currentValue.subtract(investedValue));
        }

        BigDecimal gainPercent = portfolioValue.compareTo(BigDecimal.ZERO) > 0
                ? totalPL.divide(portfolioValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // Example: Calculate individual charges here
        BigDecimal brokerage = calculateBrokerage(userId);        // ₹20 per transaction
        BigDecimal stampCharges = calculateStampCharges(userId);  // 0.015% on buy side
        BigDecimal transactionCharges = calculateTransactionCharges(userId); // NSE: 0.00297%
        BigDecimal sebiCharges = calculateSebICharges(userId);    // ₹10 / crore
        BigDecimal gst = (brokerage.add(transactionCharges).add(sebiCharges))
                .multiply(BigDecimal.valueOf(0.18));   // 18% GST on (brokerage + SEBI + txn charges)

        List<ChargeDetailDTO> charges = List.of(
                new ChargeDetailDTO("Brokerage", brokerage),
                new ChargeDetailDTO("Stamp Charges", stampCharges),
                new ChargeDetailDTO("Transaction Charges", transactionCharges),
                new ChargeDetailDTO("SEBI Charges", sebiCharges),
                new ChargeDetailDTO("GST", gst)
        );

        return new PortfolioSummaryDTO(portfolioValue, totalPL, gainPercent, charges);
    }



    @Override
    public StockStatsDTO getStockStats(Long userId, Long stockId) {
        Holdings holding = holdingRepository.findByUser_UserIdAndStock_StockId(userId, stockId)
                .orElseThrow(() -> new RuntimeException("No holdings found for the stock"));

        BigDecimal currentPrice = BigDecimal.valueOf(holding.getStock().getStockPrice());
        BigDecimal quantity = BigDecimal.valueOf(holding.getQuantity());
        BigDecimal totalValue = currentPrice.multiply(quantity);

        BigDecimal investedValue = holding.getAvgBuyPrice().multiply(quantity);
        BigDecimal pl = totalValue.subtract(investedValue);

        BigDecimal plPercent = investedValue.compareTo(BigDecimal.ZERO) > 0
                ? pl.divide(investedValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return new StockStatsDTO(
                holding.getStock().getCompanyName(),
                holding.getQuantity(),
                currentPrice,
                totalValue,
                pl,
                plPercent
        );
    }

    @Override
    public TransactionChargesDTO calculateTransactionCharges(Long userId, Long stockId, int quantity) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        BigDecimal stockPrice = BigDecimal.valueOf(stock.getStockPrice());
        BigDecimal transactionValue = stockPrice.multiply(BigDecimal.valueOf(quantity));

        BigDecimal brokerage = transactionValue.multiply(new BigDecimal("0.005")); // 0.5%
        BigDecimal stampDuty = transactionValue.multiply(new BigDecimal("0.001")); // 0.1%
        BigDecimal transactionTax = transactionValue.multiply(new BigDecimal("0.002")); // 0.2%
        BigDecimal sebiCharges = transactionValue.multiply(new BigDecimal("0.0001")); // 0.01%
        BigDecimal gst = (brokerage.add(sebiCharges)).multiply(new BigDecimal("0.18")); // 18% on brokerage + sebi

        BigDecimal totalCharges = brokerage.add(stampDuty).add(transactionTax).add(sebiCharges).add(gst);

        return new TransactionChargesDTO(brokerage, stampDuty, transactionTax, sebiCharges, gst, totalCharges);
    }



}
