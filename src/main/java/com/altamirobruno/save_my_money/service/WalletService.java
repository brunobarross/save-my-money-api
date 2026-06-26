package com.altamirobruno.save_my_money.service;


import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.dto.mappers.WalletMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.Transaction;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.TransactionRepository;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
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

  public WalletService(WalletRepository walletRepository, WalletMapper walletMapper, TransactionRepository transactionRepository) {
    this.walletRepository = walletRepository;
    this.walletMapper = walletMapper;
      this.transactionRepository = transactionRepository;
  }


  public BigDecimal calculateBalance(UUID walletId){
    return transactionRepository.getBalanceByWalletId(walletId);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
  public List<WalletDTO> getAll() {

    List<WalletDTO> wallets =  walletRepository.findAll()
      .stream()
      .map((wallet)->{
        BigDecimal amount = this.calculateBalance(wallet.getId());
        return walletMapper.toDTO(wallet, amount);
      }).collect(Collectors.toList());
    return wallets;
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
  public WalletDTO getById(@NotNull UUID id){
    return walletRepository.findById(id).map(wallet -> {
      BigDecimal amount = this.calculateBalance(wallet.getId());
      return walletMapper.toDTO(wallet, amount);
    }).orElseThrow(()-> new ItemNotFoundException(id));
  }

  public WalletDTO create(@Valid @NotNull WalletDTO walletDTO) {
    Wallet walletEntity = walletMapper.toEntity(walletDTO);
    Wallet savedWallet = walletRepository.save(walletEntity);
    return walletMapper.toDTO(savedWallet, BigDecimal.ZERO);
  }

  public WalletDTO update(@NotNull UUID id, @Valid @NotNull WalletDTO walletDTO){
    return walletRepository.findById(id)
            .map(item ->{
              item.setName(walletDTO.name());
              item.setColor(walletDTO.color());
              return walletRepository.save(item);
            }).map(savedWallet -> {
              BigDecimal amount = this.calculateBalance(savedWallet.getId());
              return walletMapper.toDTO(savedWallet, amount);
            }).orElseThrow(()-> new ItemNotFoundException(id));
  }
  public void delete(@NotNull UUID id) {
    if(!walletRepository.existsById(id)) {
      throw new ItemNotFoundException(id);
    }
    walletRepository.deleteById(id);
  }


}
