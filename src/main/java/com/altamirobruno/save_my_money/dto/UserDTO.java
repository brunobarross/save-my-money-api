package com.altamirobruno.save_my_money.dto;

import java.util.UUID;

public record UserDTO(
  UUID id,
  String name
) {
}
