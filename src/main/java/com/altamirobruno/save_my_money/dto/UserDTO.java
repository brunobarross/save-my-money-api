package com.altamirobruno.save_my_money.dto;

import com.altamirobruno.save_my_money.enums.RoleName;
import com.altamirobruno.save_my_money.model.Role;

import java.util.List;
import java.util.UUID;

public record UserDTO(
  UUID id,
  String name,
  String password,
  RoleName role
) {
}
