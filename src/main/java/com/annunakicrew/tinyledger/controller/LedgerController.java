package com.annunakicrew.tinyledger.controller;

import com.annunakicrew.tinyledger.model.Transaction;
import com.annunakicrew.tinyledger.service.LedgerService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Transaction> recordTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(ledgerService.recordTransaction(transaction.getType(), transaction.getAmount()));
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getCurrentBalance() {
        return ResponseEntity.ok(ledgerService.getCurrentBalance());
    }

    @GetMapping("/transactions")
    public ResponseEntity<Iterable<Transaction>> getTransactions() {
        return ResponseEntity.ok(ledgerService.getTransactions());
    }
}
