package org.mobi.forexapplication.repository;

import org.mobi.forexapplication.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {
    boolean existsByTickerSymbol(String tickerSymbol);

}
