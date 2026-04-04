package com.tallerwebi.dominio;

public interface RepositorioMotor {
  void guardar(Motor motorGuardado);

  Motor buscarPorId(Long id);

  Boolean modificar(Long id, String string);
}
