package com.annunakicrew.tinyledger.model;

import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Account {
    @Pattern(regexp = "[A-Z]{2}\\d{2}[A-Z0-9]{11,30}", message = "Invalid account format")
    private String accountNumber;
    @Setter
    private BigDecimal balance;
}
