package org.mobi.forexapplication.repository;

import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist,Long> {
    List<Watchlist> findByUser(User user);
    boolean existsByUserAndStock(User user, Stock stock);
    void deleteByUserAndStock(User user, Stock stock);

}
