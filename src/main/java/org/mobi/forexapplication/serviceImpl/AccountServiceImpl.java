package org.mobi.forexapplication.serviceImpl;
import org.mobi.forexapplication.Exception.GlobalCustomException;
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
import java.util.Objects;

@Service
public class AccountServiceImpl  implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            throw new RuntimeException("Unauthenticated: Principal is not an instance of UserDetails");
        }

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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String Name;

        assert principal instanceof UserDetails;
        Name = ((UserDetails) principal).getUsername();

        if((Objects.isNull(Name))){
            throw GlobalCustomException.UserNotFound("Name");
        }
        else{
            return getCurrentUser();
        }
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

//    @Override
//    public void deposit(BigDecimal amount, String fpxTxnId) {
//        if (transactionRepository.existsByFpxTxnId(fpxTxnId)) {
//            System.out.println("Duplicate FPX transaction ignored: " + fpxTxnId);
//            return;
//        }
//        User user = getCurrentUser();
//        System.out.println("The deposited user is "+user);
//
//        user.setDematBalance(user.getDematBalance().add(amount));
//        userRepository.save(user);
//
//        Transaction transaction = new Transaction(user, "DEPOSIT", amount, LocalDateTime.now());
//        transaction.setFpxTxnId(fpxTxnId);
//        transactionRepository.save(transaction);
//    }

    @Override
    public void depositToUser(BigDecimal amount, String fpxTxnId, Long userId) {

        if (transactionRepository.existsByFpxTxnId(fpxTxnId)) {
            System.out.println("Duplicate FPX transaction ignored: " + fpxTxnId);
            return;
        }

        // load the user directly by id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        //credit wallet
        user.setDematBalance(user.getDematBalance().add(amount));
        userRepository.save(user);

        Transaction txn = new Transaction(user, "DEPOSIT", amount, LocalDateTime.now());
        txn.setFpxTxnId(fpxTxnId);
        transactionRepository.save(txn);

        System.out.println("Deposited " + amount + " to user " + user.getUsername());
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
