package com.tallerwebi.dominio;

import javax.transaction.Transactional;

@Transactional
public interface ServicioMotor {
  Boolean crear(Motor motor);

  Boolean modificar(Long id, String string);
}
