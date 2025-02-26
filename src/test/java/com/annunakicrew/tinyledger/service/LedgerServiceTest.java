package com.annunakicrew.tinyledger.service;

import com.annunakicrew.tinyledger.model.Account;
import com.annunakicrew.tinyledger.model.Transaction;
import com.annunakicrew.tinyledger.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LedgerServiceTest {

    private LedgerService ledgerService;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
        ledgerService = new LedgerService(accountService);

        accountService.createAccount("SRC123", BigDecimal.valueOf(500));
        accountService.createAccount("TGT456", BigDecimal.valueOf(200));
    }

    @Test
    void testRecordDeposit() {
        Account target = accountService.getAccount("TGT456");
        Transaction deposit = Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .targetAccount(target)
                .build();

        Transaction recorded = ledgerService.recordTransaction(deposit);
        assertEquals(TransactionType.DEPOSIT, recorded.getType());
        assertEquals(BigDecimal.valueOf(100), recorded.getAmount());
        assertNotNull(recorded.getTimestamp());
        assertEquals("TGT456", recorded.getTargetAccount().getAccountNumber());

        // Check that the target account balance updated
        Account updatedTarget = accountService.getAccount("TGT456");
        assertEquals(BigDecimal.valueOf(300), updatedTarget.getBalance());

        // Check transactions list
        List<Transaction> allTx = ledgerService.getTransactions();
        assertEquals(1, allTx.size());
        assertTrue(allTx.contains(recorded));
    }

    @Test
    void testRecordWithdrawal() {
        Account source = accountService.getAccount("SRC123");
        Transaction withdrawal = Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(50))
                .sourceAccount(source)
                .build();

        Transaction recorded = ledgerService.recordTransaction(withdrawal);
        assertEquals(TransactionType.WITHDRAWAL, recorded.getType());
        assertEquals(BigDecimal.valueOf(50), recorded.getAmount());
        assertNotNull(recorded.getTimestamp());

        Account updatedSource = accountService.getAccount("SRC123");
        assertEquals(BigDecimal.valueOf(450), updatedSource.getBalance());
    }

    @Test
    void testRecordTransfer() {
        Account source = accountService.getAccount("SRC123");
        Account target = accountService.getAccount("TGT456");
        Transaction transfer = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(100))
                .sourceAccount(source)
                .targetAccount(target)
                .build();

        Transaction recorded = ledgerService.recordTransaction(transfer);
        assertEquals(TransactionType.TRANSFER, recorded.getType());
        assertEquals(BigDecimal.valueOf(100), recorded.getAmount());
        assertNotNull(recorded.getTimestamp());
        assertEquals("SRC123", recorded.getSourceAccount().getAccountNumber());
        assertEquals("TGT456", recorded.getTargetAccount().getAccountNumber());

        // Check final balances
        Account updatedSource = accountService.getAccount("SRC123");
        Account updatedTarget = accountService.getAccount("TGT456");

        assertEquals(BigDecimal.valueOf(400), updatedSource.getBalance());
        assertEquals(BigDecimal.valueOf(300), updatedTarget.getBalance());
    }

    @Test
    void testRecordTransaction_insufficientFundsForTransfer() {
        Account source = accountService.getAccount("SRC123");
        // Source has 500
        Account target = accountService.getAccount("TGT456");
        // Target has 200

        Transaction transfer = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(600))
                .sourceAccount(source)
                .targetAccount(target)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> ledgerService.recordTransaction(transfer),
                "Should throw if source account has insufficient funds.");

        // Ensure no transactions were added
        assertTrue(ledgerService.getTransactions().isEmpty());
    }

    @Test
    void testGetTransactionsUnmodifiable() {
        Account source = accountService.getAccount("SRC123");
        Transaction withdrawal = Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(50))
                .sourceAccount(source)
                .build();

        ledgerService.recordTransaction(withdrawal);
        List<Transaction> allTx = ledgerService.getTransactions();

        Transaction newTx = Transaction.builder().build();
        assertThrows(UnsupportedOperationException.class,
                () -> allTx.add(newTx),
                "Should not allow modifications from outside the service."
        );
    }
}
