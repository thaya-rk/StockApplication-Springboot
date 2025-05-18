package org.mobi.forexapplication.serviceImpl;

import jakarta.transaction.Transactional;
import org.mobi.forexapplication.dto.BuySellRequest;
import org.mobi.forexapplication.dto.HoldingResponse;
import org.mobi.forexapplication.model.Holdings;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.model.Transaction;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.HoldingRepository;
import org.mobi.forexapplication.repository.StockRepository;
import org.mobi.forexapplication.repository.TransactionRepository;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.mobi.forexapplication.security.JwtUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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


}
