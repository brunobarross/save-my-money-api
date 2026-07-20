package com.altamirobruno.save_my_money.repository;

import com.altamirobruno.save_my_money.dto.FinancialSummaryDTO;
import com.altamirobruno.save_my_money.dto.FinancialSummaryProjection;
import com.altamirobruno.save_my_money.model.Transaction;
import com.altamirobruno.save_my_money.model.User;
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
            "AND t.wallet.user.userId = :userId " +
            "AND EXTRACT(MONTH FROM t.date) = :month " +
            "AND EXTRACT(YEAR FROM t.date) = :year")
    List<Transaction> findTransactionByWalletIdAndMonthAndYear(
            @Param("walletId") UUID walletId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("userId") UUID userId);


    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND t.wallet.user.userId = :userId")
    List<Transaction> findTransactionByWalletId(@Param("walletId") UUID walletId, @Param("userId") UUID userId);

    List<Transaction> findTransactionByUser(User user);

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