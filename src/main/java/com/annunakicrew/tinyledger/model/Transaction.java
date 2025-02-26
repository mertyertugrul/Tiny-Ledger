package com.annunakicrew.tinyledger.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private Account sourceAccount;
    private Account targetAccount;
}
