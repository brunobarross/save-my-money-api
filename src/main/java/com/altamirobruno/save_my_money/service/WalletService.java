package com.altamirobruno.save_my_money.service;


import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.dto.mappers.WalletMapper;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

  public WalletDTO getById(@NotNull @Positive Long id){
    return walletRepository.findById(id).map(walletMapper::toDTO).orElseThrow(()-> new ItemNotFoundException(id));
  }

  public WalletDTO create(@Valid @NotNull WalletDTO wallet) {
    return walletMapper.toDTO(walletRepository.save(walletMapper.toEntity(wallet)));
  }

  public WalletDTO update(@NotNull @Positive Long id, @Valid @NotNull WalletDTO walletDTO){
    return walletRepository.findById(id)
            .map(item ->{
              item.setName(walletDTO.name());
              item.setColor(walletDTO.color());
              return walletRepository.save(item);
            }).map(walletMapper::toDTO).orElseThrow(()-> new ItemNotFoundException(id));
  }
  public void delete(@NotNull @Positive Long id) {
    walletRepository.delete(walletRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
  }


}
