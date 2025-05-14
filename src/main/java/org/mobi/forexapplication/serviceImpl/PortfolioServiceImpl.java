package org.mobi.forexapplication.serviceImpl;

import org.mobi.forexapplication.dto.BuySellRequest;
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
    public void buyStock(BuySellRequest request){
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock Not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal quantity = BigDecimal.valueOf(request.getQuantity());
        BigDecimal currentPrice = BigDecimal.valueOf(stock.getStockPrice());

        BigDecimal totalPrice = currentPrice.multiply(quantity);

        if (user.getDematBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setDematBalance(user.getDematBalance().subtract(totalPrice));
        userRepository.save(user);

        Optional<Holdings> Optionalholdings = Optional.ofNullable(holdingRepository.findByUser_UserIdAndStock_StockId(request.getUserId(), request.getStockId())
                .orElse(null));

        if (Optionalholdings.isPresent()) {
            Holdings holdings =Optionalholdings.get();
            holdings.setQuantity(holdings.getQuantity() + request.getQuantity());

            //formula = newAvg = (oldavg * oldqty) + (newPrice * newqty) /oldqty+newqty
            BigDecimal left = holdings.getAvgBuyPrice().multiply(BigDecimal.valueOf(holdings.getQuantity()));
            BigDecimal right = BigDecimal.valueOf(stock.getStockPrice()).multiply(BigDecimal.valueOf(request.getQuantity()));
            int deno = holdings.getQuantity() + request.getQuantity();

            BigDecimal newAvg = left.add(right).divide(BigDecimal.valueOf(deno),2,RoundingMode.HALF_EVEN);
            holdings.setAvgBuyPrice(newAvg);
            holdingRepository.save(holdings);

        } else {
            Holdings holding = new Holdings();
            holding.setUser(user);
            holding.setStock(stock);

            holding.setQuantity(request.getQuantity());
            holding.setAvgBuyPrice(currentPrice);

            holdingRepository.save(holding);
        }

        Transaction txn = new Transaction(user, stock, "BUY", request.getQuantity(), currentPrice);
        transactionRepository.save(txn);
    }

    @Override
    public void sellStock(BuySellRequest request){
        Holdings holdings = holdingRepository.findByUser_UserIdAndStock_StockId(request.getUserId(), request.getStockId())
                .orElseThrow(() -> new RuntimeException("No holdings found"));

        if (holdings.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough quantity to sell");
        }

        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal quantity = BigDecimal.valueOf(request.getQuantity());
        BigDecimal currentPrice = BigDecimal.valueOf(stock.getStockPrice());
        BigDecimal totalSellPrice = currentPrice.multiply(quantity);

        user.setDematBalance(user.getDematBalance().add(totalSellPrice));
        userRepository.save(user);

        int remainingQty = holdings.getQuantity() - request.getQuantity();
        if (remainingQty == 0) {
            holdingRepository.delete(holdings);
        } else {
            holdings.setQuantity(remainingQty);
            holdingRepository.save(holdings);
        }

        Transaction txn = new Transaction(user, stock, "SELL", request.getQuantity(), currentPrice);
        transactionRepository.save(txn);
    }
}
