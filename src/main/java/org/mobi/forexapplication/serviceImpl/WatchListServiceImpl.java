package org.mobi.forexapplication.serviceImpl;

import jakarta.transaction.Transactional;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.model.Watchlist;
import org.mobi.forexapplication.repository.StockRepository;
import org.mobi.forexapplication.repository.WatchlistRepository;
import org.mobi.forexapplication.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WatchListServiceImpl implements WatchListService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getWatchlistedStocks(User user) {
        return watchlistRepository.findByUser(user)
                .stream()
                .map(Watchlist::getStock)
                .collect(Collectors.toList());
    }

    public void addToWatchlist(User user, Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        if (!watchlistRepository.existsByUserAndStock(user, stock)) {
            Watchlist watchlist = new Watchlist();
            watchlist.setUser(user);
            watchlist.setStock(stock);
            watchlistRepository.save(watchlist);
        }
    }

    public void removeFromWatchlist(User user, Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        watchlistRepository.deleteByUserAndStock(user, stock);
    }
}
