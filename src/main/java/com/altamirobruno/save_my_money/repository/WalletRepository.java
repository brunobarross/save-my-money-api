package com.altamirobruno.save_my_money.repository;

import com.altamirobruno.save_my_money.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
