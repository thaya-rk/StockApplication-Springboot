package org.mobi.forexapplication.serviceImpl;

import jakarta.servlet.http.HttpSession;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public void buyStock(HttpSession session,BuySellRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Long userId = user.getUserId();
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal quantity = BigDecimal.valueOf(request.getQuantity());
        BigDecimal currentPrice = BigDecimal.valueOf(stock.getStockPrice());
        BigDecimal totalPrice = currentPrice.multiply(quantity);

        if (user.getDematBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct balance and save user
        user.setDematBalance(user.getDematBalance().subtract(totalPrice));
        userRepository.save(user);

        Holdings holdings = holdingRepository.findByUser_UserIdAndStock_StockId(userId, request.getStockId()).orElse(null);

        if (holdings != null) {
            BigDecimal existingQty = BigDecimal.valueOf(holdings.getQuantity());
            BigDecimal newQty = quantity;
            BigDecimal totalQty = existingQty.add(newQty);

            BigDecimal existingTotal = holdings.getAvgBuyPrice().multiply(existingQty);
            BigDecimal newTotal = currentPrice.multiply(newQty);

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

        // Save transaction
        Transaction txn = new Transaction(user, stock, "BUY", quantity.intValue(), currentPrice);
        transactionRepository.save(txn);
    }

    @Override
    public void sellStock(HttpSession session,BuySellRequest request) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Long userId = user.getUserId();        if (userId == null) {
            throw new RuntimeException("User not logged in");
        }
        Holdings holdings = holdingRepository.findByUser_UserIdAndStock_StockId(userId, request.getStockId())
                .orElseThrow(() -> new RuntimeException("No holdings found"));

        int sellQty = request.getQuantity();

        if (holdings.getQuantity() < sellQty) {
            throw new RuntimeException("Not enough quantity to sell");
        }

        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

         user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal quantity = BigDecimal.valueOf(sellQty);
        BigDecimal currentPrice = BigDecimal.valueOf(stock.getStockPrice());
        BigDecimal totalSellPrice = currentPrice.multiply(quantity);

        // Add funds back to user
        user.setDematBalance(user.getDematBalance().add(totalSellPrice));
        userRepository.save(user);

        int remainingQty = holdings.getQuantity() - sellQty;

        if (remainingQty == 0) {
            holdingRepository.delete(holdings);
        } else {
            holdings.setQuantity(remainingQty);
            holdingRepository.save(holdings);
        }

        // Save transaction
        Transaction txn = new Transaction(user, stock, "SELL", sellQty, currentPrice);
        transactionRepository.save(txn);
    }

    @Override
    public List<HoldingResponse> getHoldingsByUserId(Long userId) {
        List<Holdings> holdings = holdingRepository.findByUser_UserId(userId);
        List<HoldingResponse> responses = new ArrayList<>();

        for (Holdings h : holdings) {
            BigDecimal currentPrice = BigDecimal.valueOf(h.getStock().getStockPrice());
            BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(h.getQuantity()));

            responses.add(new HoldingResponse(
                    h.getStock().getCompanyName(),
                    h.getQuantity(),
                    h.getAvgBuyPrice(),
                    currentPrice,
                    currentValue
            ));
        }

        return responses;
    }




}
