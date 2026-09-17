package com.altamirobruno.save_my_money.dto;

import com.altamirobruno.save_my_money.enums.RoleName;
import java.util.UUID;

public record UserDTO(
  UUID id,
  String name,
  String password,
  RoleName role
) {
}
