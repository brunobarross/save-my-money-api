package com.altamirobruno.save_my_money.controller;

import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/wallets")
public class WalletController {
  private final WalletService walletService;

  public WalletController(WalletService walletService) {
    this.walletService = walletService;
  }

  @GetMapping
  public List<WalletDTO> getAll(@AuthenticationPrincipal Jwt jwt) {
    return walletService.getMyAllWallets(jwt.getSubject());
  }

  @GetMapping("/{id}")
  public WalletDTO getById(@PathVariable @NotNull UUID id, @AuthenticationPrincipal Jwt jwt) {
    return walletService.getById(id, jwt.getSubject());
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public WalletDTO create(@RequestBody @Valid WalletDTO wallet, @AuthenticationPrincipal Jwt jwt) {
    return walletService.create(wallet, jwt.getSubject());
  }

  @PutMapping("/{id}")
  public WalletDTO update(@PathVariable @NotNull UUID id, @RequestBody WalletDTO walletDTO, @AuthenticationPrincipal Jwt jwt) {
  return walletService.update(id, walletDTO, jwt.getSubject());
  }

  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    walletService.delete(id, jwt.getSubject());
  }
}
