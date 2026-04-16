package com.altamirobruno.save_my_money.repository;

import com.altamirobruno.save_my_money.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
