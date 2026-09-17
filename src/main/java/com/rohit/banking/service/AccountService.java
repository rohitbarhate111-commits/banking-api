package com.rohit.banking.service;

import com.rohit.banking.exception.InsufficientFundsException;
import com.rohit.banking.exception.ResourceNotFoundException;
import com.rohit.banking.model.Account;
import com.rohit.banking.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void transfer(Long fromId, Long toId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        if (fromId != null && fromId.equals(toId)) {
            throw new IllegalArgumentException("Sender and receiver accounts cannot be the same");
        }

        Account sender = repository.findById(fromId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found: " + fromId));

        Account receiver = repository.findById(toId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found: " + toId));

        if (sender.getBalance() < amount) {
            throw new InsufficientFundsException(
                    "Insufficient balance: available " + sender.getBalance() + ", requested " + amount
            );
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        repository.save(sender);
        repository.save(receiver);
    }
}