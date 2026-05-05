package com.altamirobruno.save_my_money.exceptions;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
  public ItemNotFoundException(UUID id) {
    super("Registro não encontrado com o id: " + id);
  }

}
