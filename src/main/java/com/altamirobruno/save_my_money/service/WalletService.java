package com.altamirobruno.save_my_money.service;


import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.dto.mappers.WalletMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {
  private final WalletRepository walletRepository;
  private final WalletMapper walletMapper;

  public WalletService(WalletRepository walletRepository, WalletMapper walletMapper) {
    this.walletRepository = walletRepository;
    this.walletMapper = walletMapper;
  }

  public List<WalletDTO> getAll() {
    return walletRepository.findAll()
      .stream()
      .map(walletMapper::toDTO)
      .collect(Collectors.toList());
  }

  public WalletDTO create(WalletDTO wallet) {
    return walletMapper.toDTO(walletRepository.save(walletMapper.toEntity(wallet)));
  }

  public void delete(Long id) {
    walletRepository.delete(walletRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
  }


}
