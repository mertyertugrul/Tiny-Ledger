package com.annunakicrew.tinyledger.service;

import com.annunakicrew.tinyledger.model.Account;
import com.annunakicrew.tinyledger.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LedgerService {
    private final List<Transaction> transactions = new ArrayList<>();
    private final AccountService accountService;

    public LedgerService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Transaction recordTransaction(Transaction transaction) {
        transaction = Transaction.builder()
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .sourceAccount(transaction.getSourceAccount())
                .targetAccount(transaction.getTargetAccount())
                .timestamp(LocalDateTime.now())
                .build();

        switch (transaction.getType()) {
            case DEPOSIT:
                Account target = transaction.getTargetAccount();
                if (target == null) {
                    throw new IllegalArgumentException("Target account is required for deposit.");
                }
                accountService.deposit(target.getAccountNumber(), transaction.getAmount());
                break;

            case WITHDRAWAL:
                Account source = transaction.getSourceAccount();
                if (source == null) {
                    throw new IllegalArgumentException("Source account is required for withdrawal.");
                }
                accountService.withdraw(source.getAccountNumber(), transaction.getAmount());
                break;

            case TRANSFER:
                if (transaction.getSourceAccount() == null || transaction.getTargetAccount() == null) {
                    throw new IllegalArgumentException("Both source and target accounts are required for transfer.");
                }
                accountService.withdraw(transaction.getSourceAccount().getAccountNumber(), transaction.getAmount());
                accountService.deposit(transaction.getTargetAccount().getAccountNumber(), transaction.getAmount());
                break;
        }

        transactions.add(transaction);
        return transaction;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}
