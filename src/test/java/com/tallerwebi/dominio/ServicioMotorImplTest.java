package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioMotorImplTest {

  /**
   * Crear un motor
   * Modificar un motor
   * Listar todos los motores
   */
  private RepositorioMotor repositorioMotor;
  private ServicioMotor servicioMotor;

  @BeforeEach
  public void init() {
    this.repositorioMotor = mock(RepositorioMotor.class);
    this.servicioMotor = new ServicioMotorImpl(repositorioMotor);
  }

  @Test
  public void deberiaCrearUnMotor() {
    Motor motor = this.dadoQueTengoUnMotor(1L, "");
    // when(this.repositorioMotor.guardar(motor)).thenReturn(true);

    Boolean creado = this.cuandoCreoUnMotor(motor);

    this.entoncesElResultadoEsVerdadero(creado);
  }

  @Test
  public void deberiaModificarElNombreDeUnMotor() {
    // Given | When | Then
    // Motor otroMotor = mock(Motor.class); // Ejemplo con mock
    Motor motorGuardado = this.dadoQueTengoUnMotor(1L, "Motor modificado");
    Motor motor = this.dadoQueTengoUnMotor(1L, "Nombre original");
    when(this.repositorioMotor.buscarPorId(1L)).thenReturn(motor);

    Boolean modificado = this.servicioMotor.modificar(1L, "Motor modificado");

    entoncesElResultadoEsVerdadero(modificado);
    verify(this.repositorioMotor, times(1)).guardar(motorGuardado);
  }

  @Test
  public void noDeberiaModificarElNombreDeUnMotorSiElIdentificadorNoExiste() {
    when(this.repositorioMotor.buscarPorId(anyLong())).thenReturn(null);

    Boolean modificado = servicioMotor.modificar(0L, "Motor modificado");

    assertThat(modificado, is(false));
  }

  private Motor dadoQueTengoUnMotor(Long id, String nombre) {
    return new Motor(id, nombre);
  }

  private Boolean cuandoCreoUnMotor(Motor motor) {
    return this.servicioMotor.crear(motor);
  }

  private void entoncesElResultadoEsVerdadero(Boolean resultado) {
    assertThat(resultado, is(true));
  }
}
