package org.mobi.forexapplication.service;

import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.model.User;

import java.util.List;

public interface WatchListService {
    List<Stock> getWatchlistedStocks(User user);
    void addToWatchlist(User user, Long stockId);
    void removeFromWatchlist(User user, Long stockId);
}
