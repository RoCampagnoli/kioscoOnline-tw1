package com.tallerwebi.dominio.excepcion;

public class DeudorNoEncontradoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DeudorNoEncontradoException(String message) {
    super(message);
  }
}
