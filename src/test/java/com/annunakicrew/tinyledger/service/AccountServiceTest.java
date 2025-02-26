package com.annunakicrew.tinyledger.service;

import com.annunakicrew.tinyledger.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void testCreateAccount() {
        Account account = accountService.createAccount("GB82WEST12345698765432", BigDecimal.valueOf(500));
        assertNotNull(account);
        assertEquals("GB82WEST12345698765432", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(500), account.getBalance());

        // Attempting to create the same account again should throw
        assertThrows(IllegalArgumentException.class, () ->
                accountService.createAccount("GB82WEST12345698765432", BigDecimal.TEN)
        );
    }

    @Test
    void testGetAccountNonExistent() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.getAccount("NONEXISTENT1234")
        );
    }

    @Test
    void testGetAccounts() {
        accountService.createAccount("GB01XXXX12345678901234", BigDecimal.valueOf(100));
        accountService.createAccount("GB02YYYY23456789012345", BigDecimal.valueOf(200));

        Map<String, Account> all = accountService.getAccounts();
        assertEquals(2, all.size());
        assertTrue(all.containsKey("GB01XXXX12345678901234"));
        assertTrue(all.containsKey("GB02YYYY23456789012345"));

        Account account = Account.builder().build();
        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () ->
                all.put("GB03ZZZZ34567890123456", account)
        );
    }

    @Test
    void testDeposit() {
        accountService.createAccount("GB82WEST12345698765432", BigDecimal.valueOf(500));
        accountService.deposit("GB82WEST12345698765432", BigDecimal.valueOf(100));

        Account updated = accountService.getAccount("GB82WEST12345698765432");
        assertEquals(BigDecimal.valueOf(600), updated.getBalance());
    }

    @Test
    void testWithdraw() {
        accountService.createAccount("GB82WEST12345698765432", BigDecimal.valueOf(500));
        accountService.withdraw("GB82WEST12345698765432", BigDecimal.valueOf(200));

        Account updated = accountService.getAccount("GB82WEST12345698765432");
        assertEquals(BigDecimal.valueOf(300), updated.getBalance());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        accountService.createAccount("GB82WEST12345698765432", BigDecimal.valueOf(100));
        BigDecimal amount = BigDecimal.valueOf(200);
        assertThrows(IllegalArgumentException.class, () ->
                accountService.withdraw("GB82WEST12345698765432", amount)
        );
    }
}
