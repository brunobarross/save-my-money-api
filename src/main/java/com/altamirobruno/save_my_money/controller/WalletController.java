package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
  private final WalletService walletService;

  public WalletController(WalletService walletService){
    this.walletService = walletService;
  }

  @GetMapping
  public List<Wallet> getAll(){
    return walletService.getAll();
  }

  @PostMapping
  public Wallet create(@RequestBody Wallet wallet){
    return walletService.create(wallet);
  }

//  @ResponseStatus(code = HttpStatus.NO_CONTENT)
//  @DeleteMapping("/${id}")
//  public void delete(@PathVariable Long id){
//    walletService.remove(id);
//  }
}
