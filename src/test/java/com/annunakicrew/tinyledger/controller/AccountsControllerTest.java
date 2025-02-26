package com.annunakicrew.tinyledger.controller;

import com.annunakicrew.tinyledger.model.Account;
import com.annunakicrew.tinyledger.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAccount() throws Exception {
        Account requestAccount = Account.builder()
                .accountNumber("GB82WEST12345698765432")
                .balance(BigDecimal.valueOf(500))
                .build();

        Account savedAccount = Account.builder()
                .accountNumber("GB82WEST12345698765432")
                .balance(BigDecimal.valueOf(500))
                .build();

        given(accountService.createAccount("GB82WEST12345698765432", BigDecimal.valueOf(500)))
                .willReturn(savedAccount);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("GB82WEST12345698765432"))
                .andExpect(jsonPath("$.balance").value(500.0));

        verify(accountService, times(1))
                .createAccount("GB82WEST12345698765432", BigDecimal.valueOf(500));
    }

    @Test
    void testGetAccount() throws Exception {
        Account existingAccount = Account.builder()
                .accountNumber("GB82WEST12345698765432")
                .balance(BigDecimal.valueOf(1000))
                .build();

        given(accountService.getAccount("GB82WEST12345698765432"))
                .willReturn(existingAccount);

        mockMvc.perform(get("/api/accounts/{accountNumber}", "GB82WEST12345698765432"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("GB82WEST12345698765432"))
                .andExpect(jsonPath("$.balance").value(1000.0));

        verify(accountService, times(1))
                .getAccount("GB82WEST12345698765432");
    }

    @Test
    void testGetAllAccounts() throws Exception {
        Account account = Account.builder()
                .accountNumber("GB82WEST12345698765432")
                .balance(BigDecimal.valueOf(1000))
                .build();

        given(accountService.getAccounts())
                .willReturn(Map.of("GB82WEST12345698765432", account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("GB82WEST12345698765432"))
                .andExpect(jsonPath("$[0].balance").value(1000.0));

        verify(accountService, times(1)).getAccounts();
    }
}
