package org.mobi.forexapplication.repository;

import org.mobi.forexapplication.model.Transaction;
import org.mobi.forexapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUserId(Long userId);
    List<Transaction> findByUserOrderByTimestampDesc(User user);

}
