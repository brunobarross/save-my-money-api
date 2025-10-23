package com.altamirobruno.save_my_money.exceptions;

public class ItemNotFoundException extends RuntimeException {
  public ItemNotFoundException(Long id) {
    super("Registro não encontrado com o id: " + id);
  }

}
