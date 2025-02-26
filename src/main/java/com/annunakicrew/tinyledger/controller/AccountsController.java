package com.annunakicrew.tinyledger.controller;

import com.annunakicrew.tinyledger.model.Account;
import com.annunakicrew.tinyledger.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {
    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.createAccount(account.getAccountNumber(), account.getBalance()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    @GetMapping
    public ResponseEntity<Iterable<Account>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts().values());
    }


}
