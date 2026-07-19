package com.altamirobruno.save_my_money.repository;

import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    List<Wallet> findByUser(User user);
}
