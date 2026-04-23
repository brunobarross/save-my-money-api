package com.altamirobruno.save_my_money.dto;

import com.altamirobruno.save_my_money.enums.TransactionType;
import com.altamirobruno.save_my_money.model.Wallet;

import java.time.LocalDate;

public record TransactionDTO(
        Long id,
        String name,
        Float value,
        String description,
        LocalDate date,
        String installment,
        TransactionType type,
        Wallet wallet
) {

}
