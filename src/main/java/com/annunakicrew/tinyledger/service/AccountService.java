package com.annunakicrew.tinyledger.service;

import com.annunakicrew.tinyledger.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account createAccount(String accountNumber, BigDecimal initialBalance) {
        if (accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account already exists.");
        }
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(initialBalance != null ? initialBalance : BigDecimal.ZERO)
                .build();
        accounts.put(accountNumber, account);
        return account;
    }

    public Account getAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account does not exist.");
        }
        return account;
    }

    public Map<String, Account> getAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account does not exist.");
        }
        synchronized (account) {
            account.setBalance(account.getBalance().add(amount));
        }
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account does not exist.");
        }
        synchronized (account) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient funds.");
            }
            account.setBalance(account.getBalance().subtract(amount));
        }
    }

}
