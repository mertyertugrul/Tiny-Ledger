package com.annunakicrew.tinyledger.service;

import com.annunakicrew.tinyledger.model.Transaction;
import com.annunakicrew.tinyledger.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LedgerServiceTest {

    private LedgerService ledgerService;

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService();
    }

    @Test
    void testRecordDepositTransaction() {
        Transaction transaction = ledgerService.recordTransaction(
                TransactionType.DEPOSIT,
                BigDecimal.valueOf(100.00)
        );

        assertNotNull(transaction);
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(BigDecimal.valueOf(100.00), transaction.getAmount());
        assertEquals(BigDecimal.valueOf(100.00), ledgerService.getCurrentBalance(),
                "Balance should reflect the deposit.");
    }

    @Test
    void testRecordWithdrawalTransaction() {
        ledgerService.recordTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(200.00));

        Transaction transaction = ledgerService.recordTransaction(
                TransactionType.WITHDRAWAL,
                BigDecimal.valueOf(50.00)
        );

        assertNotNull(transaction);
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(BigDecimal.valueOf(50.00), transaction.getAmount());

        assertEquals(BigDecimal.valueOf(150.00), ledgerService.getCurrentBalance(),
                "Balance should be deposit minus withdrawal.");
    }

    @Test
    void testGetCurrentBalanceWithMultipleTransactions() {
        ledgerService.recordTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(100.00));
        ledgerService.recordTransaction(TransactionType.DEPOSIT, BigDecimal.valueOf(200.00));
        ledgerService.recordTransaction(TransactionType.WITHDRAWAL, BigDecimal.valueOf(50.00));

        assertEquals(BigDecimal.valueOf(250.00), ledgerService.getCurrentBalance(),
                "Balance should match the sum of all deposits minus withdrawals.");
    }

    @Test
    void testGetTransactionsUnmodifiable() {
        ledgerService.recordTransaction(TransactionType.DEPOSIT, BigDecimal.TEN);
        List<Transaction> transactions = ledgerService.getTransactions();

        Transaction transaction = Transaction.builder().build();
        assertThrows(UnsupportedOperationException.class,
                () -> transactions.add(transaction),
                "The returned transaction list should be unmodifiable."
        );
    }
}
