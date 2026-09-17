package com.rohit.banking.service;

import com.rohit.banking.exception.InsufficientFundsException;
import com.rohit.banking.exception.ResourceNotFoundException;
import com.rohit.banking.model.Account;
import com.rohit.banking.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService accountService;

    private Account sender;
    private Account receiver;

    @BeforeEach
    void setUp() {
        sender = new Account(1L, "Rohit Barhate", 500.0);
        receiver = new Account(2L, "Jane Doe", 200.0);
    }

    @Test
    @DisplayName("createAccount: saves and returns the new account")
    void createAccount_success() {
        when(repository.save(any(Account.class))).thenReturn(sender);

        Account created = accountService.createAccount(sender);

        assertThat(created).isNotNull();
        assertThat(created.getAccountHolderName()).isEqualTo("Rohit Barhate");
        verify(repository, times(1)).save(sender);
    }

    @Test
    @DisplayName("getAllAccounts: returns list of all accounts")
    void getAllAccounts_success() {
        when(repository.findAll()).thenReturn(List.of(sender, receiver));

        List<Account> accounts = accountService.getAllAccounts();

        assertThat(accounts).hasSize(2);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("transfer: moves balance atomically between accounts")
    void transfer_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(sender));
        when(repository.findById(2L)).thenReturn(Optional.of(receiver));

        accountService.transfer(1L, 2L, 150.0);

        assertThat(sender.getBalance()).isEqualTo(350.0);
        assertThat(receiver.getBalance()).isEqualTo(350.0);
        verify(repository).save(sender);
        verify(repository).save(receiver);
    }

    @Test
    @DisplayName("transfer: throws IllegalArgumentException when amount is zero or negative")
    void transfer_nonPositiveAmount_throwsException() {
        assertThatThrownBy(() -> accountService.transfer(1L, 2L, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transfer amount must be greater than zero");

        assertThatThrownBy(() -> accountService.transfer(1L, 2L, -50.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transfer amount must be greater than zero");

        verify(repository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("transfer: throws IllegalArgumentException when sender and receiver are the same account")
    void transfer_sameAccount_throwsException() {
        assertThatThrownBy(() -> accountService.transfer(1L, 1L, 50.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sender and receiver accounts cannot be the same");

        verify(repository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("transfer: throws InsufficientFundsException when sender has inadequate funds")
    void transfer_insufficientFunds_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(sender));
        when(repository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThatThrownBy(() -> accountService.transfer(1L, 2L, 1000.0))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient balance");

        verify(repository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("transfer: throws ResourceNotFoundException when sender account does not exist")
    void transfer_senderNotFound_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.transfer(99L, 2L, 50.0))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sender account not found: 99");

        verify(repository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("transfer: throws ResourceNotFoundException when receiver account does not exist")
    void transfer_receiverNotFound_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(sender));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.transfer(1L, 99L, 50.0))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Receiver account not found: 99");

        verify(repository, never()).save(any(Account.class));
    }
}