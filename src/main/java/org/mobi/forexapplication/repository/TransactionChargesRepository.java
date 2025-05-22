package org.mobi.forexapplication.repository;

import org.mobi.forexapplication.model.Transaction_charges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionChargesRepository extends JpaRepository<Transaction_charges,Long> {
    List<Transaction_charges> findByTransaction_User_UserId(Long userId);
    List<Transaction_charges> findByTransaction_Stock_StockId(Long stockId);
}
