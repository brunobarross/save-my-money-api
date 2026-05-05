package com.altamirobruno.save_my_money.dto;

import java.util.UUID;

public record WalletDTO(
  UUID id,
  String name,
  String color
) {
}
