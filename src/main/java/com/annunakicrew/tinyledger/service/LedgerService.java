package com.annunakicrew.tinyledger.service;

import com.annunakicrew.tinyledger.model.Transaction;
import com.annunakicrew.tinyledger.model.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LedgerService {
    private final List<Transaction> transactions = new ArrayList<>();

    public Transaction recordTransaction(TransactionType type, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .type(type)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();
        transactions.add(transaction);
        return transaction;
    }

    public BigDecimal getCurrentBalance() {
        return transactions.stream()
                .map(transaction -> transaction.getType() == TransactionType.DEPOSIT
                        ? transaction.getAmount() : transaction.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
