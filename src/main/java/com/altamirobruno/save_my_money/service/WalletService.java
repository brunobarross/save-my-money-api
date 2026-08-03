package com.altamirobruno.save_my_money.service;

import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.dto.mappers.WalletMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public WalletService(WalletRepository walletRepository, WalletMapper walletMapper, TransactionRepository transactionRepository, UserService userService) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public BigDecimal calculateBalance(UUID walletId, Integer month, Integer year) {
        return transactionRepository.getBalanceByWalletId(walletId, month, year);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public List<WalletDTO> getAll(String username, Integer month, Integer year) {
        User user = this.userService.findUserByName(username);
        return walletRepository.findByUser(user)
                .stream()
                .map((wallet) -> {
                    BigDecimal amount = this.calculateBalance(wallet.getId(), month, year);
                    return walletMapper.toDTO(wallet, amount);
                }).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public WalletDTO getById(@NotNull UUID id, String username) {
        Wallet walletEntity = getWalletEntityAndCheckOwnership(id, username);

        LocalDate now = LocalDate.now();
        BigDecimal amount = this.calculateBalance(walletEntity.getId(), now.getMonthValue(), now.getYear());

        return walletMapper.toDTO(walletEntity, amount);
    }

    @Transactional
    public WalletDTO create(@Valid @NotNull WalletDTO walletDTO, String username) {
        User user = this.userService.findUserByName(username);
        Wallet walletEntity = walletMapper.toEntity(walletDTO);

        if (walletEntity.getAmount() == null) {
            walletEntity.setAmount(BigDecimal.ZERO);
        }
        walletEntity.setUser(user);

        Wallet savedWallet = walletRepository.save(walletEntity);
        return walletMapper.toDTO(savedWallet, BigDecimal.ZERO);
    }

    @Transactional
    public WalletDTO update(@NotNull UUID id, @Valid @NotNull WalletDTO walletDTO, String username) {
        Wallet walletEntity = getWalletEntityAndCheckOwnership(id, username);

        walletEntity.setName(walletDTO.name());
        walletEntity.setColor(walletDTO.color());

        LocalDate now = LocalDate.now();
        BigDecimal amount = this.calculateBalance(walletEntity.getId(), now.getMonthValue(), now.getYear());
        walletEntity.setAmount(amount);

        walletRepository.save(walletEntity);
        return walletMapper.toDTO(walletEntity, amount);
    }

    @Transactional
    public void delete(@NotNull UUID id, String username) {
        Wallet walletEntity = getWalletEntityAndCheckOwnership(id, username);
        walletRepository.delete(walletEntity);
    }

    private Wallet getWalletEntityAndCheckOwnership(UUID id, String username) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        User user = this.userService.findUserByName(username);

        if (!wallet.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: You do not own this wallet");
        }

        return wallet;
    }
}