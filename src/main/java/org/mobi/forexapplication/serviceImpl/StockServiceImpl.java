package org.mobi.forexapplication.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.repository.HoldingRepository;
import org.mobi.forexapplication.repository.StockRepository;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Stock addStock(Stock stock) {
        stock.setCreatedAt(LocalDateTime.now());
        stock.setUpdatedAt(LocalDateTime.now());
        return stockRepository.save(stock);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Stock updateStockPrice(Long id, Double newPrice) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with ID: " + id));
        stock.setStockPrice(newPrice);
        stock.setUpdatedAt(LocalDateTime.now());
        return stockRepository.save(stock);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock not found with ID: " + id));

        if (holdingRepository.existsByStock_StockId(id)) {
            throw new IllegalStateException("This stock is currently held by users and cannot be deleted.");
        }

        stockRepository.deleteById(id);
    }


    @Override
    public List<Stock> getAllStocks()
    {
        return stockRepository.findAll();
    }
}
