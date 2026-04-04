package com.tallerwebi.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioMotorImpl implements ServicioMotor {

  @Autowired
  private RepositorioMotor repositorioMotor;

  public ServicioMotorImpl(RepositorioMotor repositorioMotor) {
    this.repositorioMotor = repositorioMotor;
  }

  @Override
  public Boolean crear(Motor motor) {
    this.repositorioMotor.guardar(motor);
    return true;
  }

  @Override
  public Boolean modificar(Long id, String nombre) {
    Motor motorBuscado = this.repositorioMotor.buscarPorId(id);

    if (motorBuscado != null) {
      motorBuscado.setNombre(nombre);
      this.repositorioMotor.guardar(motorBuscado);
      return true;
    }

    return false;
  }
}
