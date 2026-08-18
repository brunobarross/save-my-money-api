package com.altamirobruno.save_my_money.repository;

import com.altamirobruno.save_my_money.dto.FinancialSummaryProjection;
import com.altamirobruno.save_my_money.model.Transaction;
import com.altamirobruno.save_my_money.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId " +
            "AND t.wallet.user.userId = :userId " +
            "AND EXTRACT(MONTH FROM t.date) = :month " +
            "AND EXTRACT(YEAR FROM t.date) = :year")
    Page<Transaction> findTransactionByWalletIdAndMonthAndYear(
            @Param("walletId") UUID walletId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("userId") UUID userId,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.wallet.user.userId = :userId " +
            "AND EXTRACT(MONTH FROM t.date) = :month " +
            "AND EXTRACT(YEAR FROM t.date) = :year")
    Page<Transaction> findTransactionByMonthAndYear(
            @Param("month") int month,
            @Param("year") int year,
            @Param("userId") UUID userId,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND t.wallet.user.userId = :userId")
    Page<Transaction> findTransactionByWalletId(@Param("walletId") UUID walletId, @Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.wallet.user.userId = :userId")
    Page<Transaction> findTransactionByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(
            CASE WHEN t.type = com.altamirobruno.save_my_money.enums.TransactionType.EXPENSE 
                 THEN t.value 
                 ELSE -t.value 
            END), 0) 
        FROM Transaction t 
        WHERE t.wallet.id = :walletId 
          AND MONTH(t.date) = :month 
          AND YEAR(t.date) = :year
    """)
    BigDecimal getBalanceByWalletId(@Param("walletId") UUID walletId, @Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT " +
            "COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_value ELSE 0 END), 0) AS totalReceipts, " +
            "COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_value ELSE 0 END), 0) AS totalExpenses, " +
            "(COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount_value ELSE 0 END), 0) - " +
            " COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount_value ELSE 0 END), 0)) AS totalBalance " +
            "FROM tb_transactions " +
            "WHERE user_id = CAST(:userId AS uuid) " +
            "AND EXTRACT(MONTH FROM date) = :month " +
            "AND EXTRACT(YEAR FROM date) = :year" +
            "AND ",
            nativeQuery = true)
    FinancialSummaryProjection getFinancialSummary(@Param("month") int month, @Param("year") int year,  @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.recurrenceGroupId = :groupId AND t.date >= :fromDate AND t.user.userId = :userId")
    void deleteFutureInstallments(@Param("groupId") UUID groupId, @Param("fromDate") LocalDate fromDate, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.recurrenceGroupId = :groupId AND t.user.userId = :userId")
    void deleteAllInGroup(@Param("groupId") UUID groupId, @Param("userId") UUID userId);

}