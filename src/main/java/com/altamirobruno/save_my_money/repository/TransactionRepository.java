package com.altamirobruno.save_my_money.repository;

import com.altamirobruno.save_my_money.dto.FinancialSummaryDTO;
import com.altamirobruno.save_my_money.dto.FinancialSummaryProjection;
import com.altamirobruno.save_my_money.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId " +
            "AND EXTRACT(MONTH FROM t.date) = :month " +
            "AND EXTRACT(YEAR FROM t.date) = :year")
    List<Transaction> findTransactionByWalletIdAndMonthAndYear(
            @Param("walletId") UUID walletId,
            @Param("month") int month,
            @Param("year") int year);

    List<Transaction> findTransactionByWalletId(UUID walletId);

    @Query("SELECT COALESCE(SUM(t.value), 0) FROM Transaction t WHERE t.wallet.id = :walletId")
    BigDecimal getBalanceByWalletId(@Param("walletId") UUID walletId);


    @Query(value = "SELECT " +
            "COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_value ELSE 0 END), 0) AS totalReceipts, " +
            "COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_value ELSE 0 END), 0) AS totalExpenses, " +
            "(COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_value ELSE 0 END), 0) - " +
            " COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_value ELSE 0 END), 0)) AS totalBalance " +
            "FROM tb_transactions " +
            "WHERE user_id = CAST(:userId AS uuid) " +
            "AND EXTRACT(MONTH FROM date) = :month " +
            "AND EXTRACT(YEAR FROM date) = :year",
            nativeQuery = true)
    FinancialSummaryProjection getFinancialSummary(@Param("month") int month, @Param("year") int year,  @Param("userId") UUID userId);
}