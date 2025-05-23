package org.mobi.forexapplication.serviceImpl;

import jakarta.servlet.http.HttpSession;
import org.mobi.forexapplication.model.Transaction;
import org.mobi.forexapplication.model.User;
import org.mobi.forexapplication.repository.TransactionRepository;
import org.mobi.forexapplication.repository.UserRepository;
import org.mobi.forexapplication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl  implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        //Using singleton here to get the existing instance without creating new one
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        System.out.println(username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }


    @Override
    public BigDecimal getBalance() {
        User user = getCurrentUser();
        return user.getDematBalance();
    }

    @Override
    public List<Transaction> getLedger() {
        User user = getCurrentUser();
        return transactionRepository.findByUserOrderByTimestampDesc(user);
    }

    @Override
    public User getProfile() {
        return getCurrentUser();
    }

    @Override
    public User updateProfile(User updatedInfo) {
        User user = getCurrentUser();
        user.setFullName(updatedInfo.getFullName());
        user.setMobileNumber(updatedInfo.getMobileNumber());
        user.setDob(updatedInfo.getDob());
        return userRepository.save(user);
    }

    @Override
    public void deposit(BigDecimal amount) {
        User user=getCurrentUser();
        user.setDematBalance(user.getDematBalance().add(amount));
        userRepository.save(user);

        Transaction transaction=new Transaction(user,"DEPOSIT",amount,LocalDateTime.now());
        transactionRepository.save(transaction);
    }


    @Override
    public boolean withdraw(BigDecimal amount) {
        User user=getCurrentUser();

        if(user.getDematBalance().compareTo(amount)<0) return false;

        user.setDematBalance(user.getDematBalance().subtract(amount));
        userRepository.save(user);

        Transaction transaction=new Transaction(user,"WITHDRAW",amount, LocalDateTime.now());
        transactionRepository.save(transaction);

        return true;
    }


}
