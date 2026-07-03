package com.altamirobruno.save_my_money.dto;

import java.math.BigDecimal;

public record FinancialSummaryDTO(
        BigDecimal totalReceipts,
        BigDecimal totalExpenses,
        BigDecimal currentBalance
) {
}
