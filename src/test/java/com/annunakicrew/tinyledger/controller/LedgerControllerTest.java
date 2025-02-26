package com.annunakicrew.tinyledger.controller;

import com.annunakicrew.tinyledger.model.Account;
import com.annunakicrew.tinyledger.model.Transaction;
import com.annunakicrew.tinyledger.model.TransactionType;
import com.annunakicrew.tinyledger.service.LedgerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LedgerController.class)
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LedgerService ledgerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRecordTransaction_Deposit() throws Exception {
        // Request
        Transaction request = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .targetAccount(Account.builder()
                        .accountNumber("GB82WEST12345698765432")
                        .build())
                .build();

        // Service mock returns a "saved" transaction with a timestamp
        Transaction saved = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .timestamp(LocalDateTime.of(2023, 1, 1, 10, 0))
                .targetAccount(Account.builder()
                        .accountNumber("GB82WEST12345698765432")
                        .build())
                .build();

        given(ledgerService.recordTransaction(any(Transaction.class)))
                .willReturn(saved);

        mockMvc.perform(post("/api/ledger/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.targetAccount.accountNumber").value("GB82WEST12345698765432"));

        verify(ledgerService, times(1)).recordTransaction(any(Transaction.class));
    }

    @Test
    void testGetTransactions() throws Exception {
        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(50))
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0))
                .sourceAccount(Account.builder().accountNumber("GB82WEST12345698765432").build())
                .build();

        given(ledgerService.getTransactions()).willReturn(Collections.singletonList(transaction));

        mockMvc.perform(get("/api/ledger/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].timestamp").exists())
                .andExpect(jsonPath("$[0].sourceAccount.accountNumber").value("GB82WEST12345698765432"));

        verify(ledgerService, times(1)).getTransactions();
    }
}
