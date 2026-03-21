package com.rohit.banking.service;

import com.rohit.banking.exception.ResourceNotFoundException;
import com.rohit.banking.model.Account;
import com.rohit.banking.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account createAccount(Account account) {
        return repository.save(account);
    }

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public void transfer(Long fromId, Long toId, double amount) {

        Account from = repository.findById(fromId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Account to = repository.findById(toId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        repository.save(from);
        repository.save(to);
    }
}