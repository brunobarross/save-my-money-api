package com.altamirobruno.save_my_money.service;


import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.dto.mappers.WalletMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import com.altamirobruno.save_my_money.repository.UserRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, WalletMapper walletMapper, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    private User runtimeUserLookup(String username) {
        return userRepository.findUserByName(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }


    public BigDecimal calculateBalance(UUID walletId) {
        return transactionRepository.getBalanceByWalletId(walletId);
    }


    public List<WalletDTO> getAll() {
        return walletRepository.findAll()
                .stream()
                .map((wallet) -> {
                    BigDecimal amount = this.calculateBalance(wallet.getId());
                    return walletMapper.toDTO(wallet, amount);
                }).collect(Collectors.toList());
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public List<WalletDTO> getMyAllWallets(String username) {
        User user = runtimeUserLookup(username);
        return walletRepository.findByUser(user)
                .stream()
                .map((wallet) -> {
                    BigDecimal amount = this.calculateBalance(wallet.getId());
                    return walletMapper.toDTO(wallet, amount);
                }).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public WalletDTO getById(@NotNull UUID id, String username) {
        WalletDTO wallet = walletRepository.findById(id).map(walletItem -> {
            BigDecimal amount = this.calculateBalance(walletItem.getId());
            return walletMapper.toDTO(walletItem, amount);
        }).orElseThrow(() -> new ItemNotFoundException(id));

        User user = runtimeUserLookup(username);

        if(!wallet.userId().equals(user.getUserId())){
            throw new RuntimeException("Access denied: You do not own this wallet");
        }

        return wallet;
    }

    public WalletDTO create(@Valid @NotNull WalletDTO walletDTO, String username) {
        User user = runtimeUserLookup(username);
        Wallet walletEntity = walletMapper.toEntity(walletDTO);
        if (walletEntity.getAmount() == null) {
            walletEntity.setAmount(BigDecimal.ZERO);
        }
        walletEntity.setUser(user);
        Wallet savedWallet = walletRepository.save(walletEntity);
        return walletMapper.toDTO(savedWallet, BigDecimal.ZERO);
    }

    public WalletDTO update(@NotNull UUID id, @Valid @NotNull WalletDTO walletDTO, String username) {
        Wallet wallet = walletMapper.toEntity(getById(id,username));
        wallet.setName(walletDTO.name());
        wallet.setColor(walletDTO.color());
        BigDecimal amount = this.calculateBalance(walletDTO.id());
        wallet.setAmount(amount);
        walletRepository.save(wallet);
        return walletMapper.toDTO(wallet, amount);
    }

    public void delete(@NotNull UUID id, String username) {
        Wallet wallet = walletMapper.toEntity(getById(id,username));
        walletRepository.delete(wallet);
    }


}
