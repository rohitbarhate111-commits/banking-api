package com.rohit.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohit.banking.exception.GlobalExceptionHandler;
import com.rohit.banking.exception.InsufficientFundsException;
import com.rohit.banking.exception.ResourceNotFoundException;
import com.rohit.banking.model.Account;
import com.rohit.banking.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    @DisplayName("POST /api/accounts: returns 201 Created with valid account")
    void createAccount_valid_returns201() throws Exception {
        Account saved = new Account(1L, "Rohit Barhate", 1000.0);
        when(accountService.createAccount(any(Account.class))).thenReturn(saved);

        Account request = new Account(null, "Rohit Barhate", 1000.0);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountHolderName").value("Rohit Barhate"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @DisplayName("POST /api/accounts: returns 400 Bad Request when name is blank")
    void createAccount_blankName_returns400() throws Exception {
        Account request = new Account(null, "", 500.0);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountHolderName").value("Account holder name is required"));

        verify(accountService, never()).createAccount(any(Account.class));
    }

    @Test
    @DisplayName("POST /api/accounts: returns 400 Bad Request when balance is negative")
    void createAccount_negativeBalance_returns400() throws Exception {
        Account request = new Account(null, "Rohit Barhate", -10.0);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.balance").value("Balance cannot be negative"));

        verify(accountService, never()).createAccount(any(Account.class));
    }

    @Test
    @DisplayName("GET /api/accounts: returns 200 OK with list of accounts")
    void getAllAccounts_returns200() throws Exception {
        List<Account> accounts = List.of(
                new Account(1L, "Rohit Barhate", 1000.0),
                new Account(2L, "Jane Doe", 500.0)
        );
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountHolderName").value("Rohit Barhate"))
                .andExpect(jsonPath("$[1].accountHolderName").value("Jane Doe"));
    }

    @Test
    @DisplayName("POST /api/accounts/transfer: returns 200 OK on valid transfer")
    void transfer_valid_returns200() throws Exception {
        doNothing().when(accountService).transfer(1L, 2L, 100.0);

        mockMvc.perform(post("/api/accounts/transfer")
                        .param("fromId", "1")
                        .param("toId", "2")
                        .param("amount", "100.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));

        verify(accountService).transfer(1L, 2L, 100.0);
    }

    @Test
    @DisplayName("POST /api/accounts/transfer: returns 400 Bad Request on illegal argument")
    void transfer_illegalArgument_returns400() throws Exception {
        doThrow(new IllegalArgumentException("Transfer amount must be greater than zero"))
                .when(accountService).transfer(1L, 2L, 0.0);

        mockMvc.perform(post("/api/accounts/transfer")
                        .param("fromId", "1")
                        .param("toId", "2")
                        .param("amount", "0.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Transfer amount must be greater than zero"));
    }

    @Test
    @DisplayName("POST /api/accounts/transfer: returns 404 Not Found when account missing")
    void transfer_accountNotFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Sender account not found: 99"))
                .when(accountService).transfer(eq(99L), any(), anyDouble());

        mockMvc.perform(post("/api/accounts/transfer")
                        .param("fromId", "99")
                        .param("toId", "2")
                        .param("amount", "50.0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Sender account not found: 99"));
    }

    @Test
    @DisplayName("POST /api/accounts/transfer: returns 422 Unprocessable Entity on insufficient funds")
    void transfer_insufficientFunds_returns422() throws Exception {
        doThrow(new InsufficientFundsException("Insufficient balance: available 10.0, requested 100.0"))
                .when(accountService).transfer(eq(1L), eq(2L), eq(100.0));

        mockMvc.perform(post("/api/accounts/transfer")
                        .param("fromId", "1")
                        .param("toId", "2")
                        .param("amount", "100.0"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Insufficient balance: available 10.0, requested 100.0"));
    }
}