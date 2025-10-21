package com.altamirobruno.save_my_money.service;


import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {
  private final WalletRepository walletRepository;

  public WalletService(WalletRepository walletRepository){
    this.walletRepository = walletRepository;
  }

  public List<Wallet> getAll(){
    return walletRepository.findAll();
  }

  public Wallet create(Wallet wallet){
    return walletRepository.save(wallet);
  }

//  public void remove(Long id){
//     walletRepository.delete(walletRepository.findById(id).orElseThrow());
//  }
}
