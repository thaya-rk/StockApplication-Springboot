package org.mobi.forexapplication.repository;

import org.mobi.forexapplication.model.Holdings;
import org.mobi.forexapplication.model.Stock;
import org.mobi.forexapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holdings,Long> {
    Optional<Holdings> findByUser_UserIdAndStock_StockId(Long userId, Long stockId);
}