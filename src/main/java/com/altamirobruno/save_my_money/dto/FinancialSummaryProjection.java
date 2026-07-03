package com.altamirobruno.save_my_money.dto;

import java.math.BigDecimal;

public interface FinancialSummaryProjection {
    BigDecimal getTotalReceipts();
    BigDecimal getTotalExpenses();
    BigDecimal getTotalBalance();
}