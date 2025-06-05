package org.mobi.forexapplication.service;

import org.mobi.forexapplication.model.Transaction;
import org.mobi.forexapplication.model.User;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    BigDecimal getBalance();

    List<Transaction> getLedger();

    User getProfile();

    User updateProfile(User updatedInfo);

    void deposit(BigDecimal amount);

    void depositToUser(BigDecimal amount, String fpxTxnId,Long userId);

    boolean withdraw(BigDecimal amount);

}
