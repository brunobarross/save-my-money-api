package com.altamirobruno.save_my_money.dto.mappers;

import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.model.Wallet;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class WalletMapper {
  public WalletDTO toDTO(Wallet wallet) {
    return new WalletDTO(wallet.getId(), wallet.getName(), wallet.getColor());

  }

  public Wallet toEntity(WalletDTO walletDTO) {
    if (walletDTO == null) {
      return null;
    }

    Wallet wallet = new Wallet();

    if (walletDTO.id() != null) {
      wallet.setId(walletDTO.id());
    }

    wallet.setName(walletDTO.name());
    wallet.setColor(walletDTO.color());


    return wallet;

  }
}
