package com.annunakicrew.tinyledger.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void testRecordTransaction() throws Exception {
        Transaction requestTransaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .build();

        Transaction savedTransaction = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .timestamp(LocalDateTime.of(2023, 1, 1, 10, 0))
                .build();

        given(ledgerService.recordTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100)))
                .willReturn(savedTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(ledgerService, times(1))
                .recordTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100));
    }

    @Test
    void testGetCurrentBalance() throws Exception {
        given(ledgerService.getCurrentBalance())
                .willReturn(BigDecimal.valueOf(150.50));

        mockMvc.perform(get("/api/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string(BigDecimal.valueOf(150.50).toString()));

        verify(ledgerService, times(1)).getCurrentBalance();
    }

    @Test
    void testGetTransactions() throws Exception {
        Transaction transaction = Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(50))
                .timestamp(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();

        given(ledgerService.getTransactions())
                .willReturn(Collections.singletonList(transaction));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].timestamp").exists());

        verify(ledgerService, times(1)).getTransactions();
    }
}
