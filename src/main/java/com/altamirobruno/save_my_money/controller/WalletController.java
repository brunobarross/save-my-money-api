package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
  private final WalletService walletService;

  public WalletController(WalletService walletService) {
    this.walletService = walletService;
  }

  @GetMapping
  public List<WalletDTO> getAll() {
    return walletService.getAll();
  }

  @GetMapping("/{id}")
  public WalletDTO getById(@PathVariable @NotNull UUID id){
    return walletService.getById(id);
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public WalletDTO create(@RequestBody @Valid WalletDTO wallet) {
    return walletService.create(wallet);
  }

  @PutMapping("/{id}")
  public WalletDTO update(@PathVariable @NotNull UUID id, @RequestBody WalletDTO walletDTO){
  return walletService.update(id, walletDTO);
  }

  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id) {
    walletService.delete(id);
  }
}
