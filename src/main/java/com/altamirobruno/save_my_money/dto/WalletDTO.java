package com.altamirobruno.save_my_money.dto;

import com.altamirobruno.save_my_money.enums.IconType;

import java.util.UUID;

public record WalletDTO(
  UUID id,
  String name,
  String color,
  UUID userId,
  IconType icon
) {
}
