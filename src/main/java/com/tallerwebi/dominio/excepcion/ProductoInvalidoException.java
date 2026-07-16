package com.tallerwebi.dominio.excepcion;

public class ProductoInvalidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ProductoInvalidoException(String message) {
    super(message);
  }

  // Cambiamos "Exception e" por "Throwable causa" para evitar el nombre corto y tipar correctamente
  public ProductoInvalidoException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
