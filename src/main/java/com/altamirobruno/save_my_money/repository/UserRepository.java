package com.altamirobruno.save_my_money.repository;

import com.altamirobruno.save_my_money.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByName(String name);
}
