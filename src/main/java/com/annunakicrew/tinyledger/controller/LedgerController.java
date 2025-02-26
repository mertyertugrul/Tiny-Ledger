package com.annunakicrew.tinyledger.controller;

import com.annunakicrew.tinyledger.model.Transaction;
import com.annunakicrew.tinyledger.service.LedgerService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class LedgerController {
    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/transactions")
    public Transaction recordTransaction(@RequestBody Transaction transaction) {
        return ledgerService.recordTransaction(transaction.getType(), transaction.getAmount());
    }

    @GetMapping("/balance")
    public BigDecimal getCurrentBalance() {
        return ledgerService.getCurrentBalance();
    }

    @GetMapping("/transactions")
    public Iterable<Transaction> getTransactions() {
        return ledgerService.getTransactions();
    }
}
