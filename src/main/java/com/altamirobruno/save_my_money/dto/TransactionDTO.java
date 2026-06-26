package com.altamirobruno.save_my_money.dto;

import com.altamirobruno.save_my_money.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionDTO(
        UUID id,
        String name,
        BigDecimal value,
        String description,
        LocalDate date,
        String installment,
        TransactionType type,
        UUID walletId,
        UUID userId
) {

}
