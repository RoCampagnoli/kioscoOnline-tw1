package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Deudor;
import com.tallerwebi.dominio.ServicioDeudor;
import com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControladorDeudorTest {

  private ControladorDeudor controlador;
  private ServicioDeudor servicioDeudor;

  @BeforeEach
  public void init() {
    this.servicioDeudor = mock(ServicioDeudor.class);
    this.controlador = new ControladorDeudor(this.servicioDeudor);
  }

  @Test
  public void deberiaRetornar200AlObtenerDeudorPorDni() {
    Deudor deudor = new Deudor(1L, "Fabio", "123");
    when(this.servicioDeudor.obtenerPorDni("123")).thenReturn(deudor);

    ResponseEntity<?> response = this.controlador.obtenerPorDni("123");

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(), instanceOf(DeudorDto.class));
    DeudorDto body = (DeudorDto) response.getBody();
    assertThat(body.getNombre(), is("Fabio"));
    assertThat(body.getDni(), is("123"));
  }

  @Test
  public void deberiaRetornar404CuandoDeudorNoExistePorDni() {
    when(this.servicioDeudor.obtenerPorDni("999")).thenThrow(DeudorNoEncontradoException.class);

    ResponseEntity<?> response = this.controlador.obtenerPorDni("999");

    assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), instanceOf(Map.class));
    assertThat((Map<?, ?>) response.getBody(), hasEntry("error", "No encontrado"));
  }

  @Test
  public void deberiaRetornar201AlCrearDeudor() {
    DeudorDto dto = new DeudorDto(null, "Juan", "111");
    when(this.servicioDeudor.crear(any())).thenReturn(new Deudor(1L, "Juan", "111"));

    ResponseEntity<?> response = this.controlador.crear(dto);

    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(response.getBody(), instanceOf(DeudorDto.class));
    DeudorDto body = (DeudorDto) response.getBody();
    assertThat(body.getNombre(), is("Juan"));
    assertThat(body.getId(), is(1L));
  }

  @Test
  public void deberiaRetornar404AlActualizarDeudorInexistente() {
    DeudorDto dto = new DeudorDto(999L, "Fabio", "123");
    when(this.servicioDeudor.actualizar(eq(999L), any()))
      .thenThrow(DeudorNoEncontradoException.class);

    ResponseEntity<?> response = this.controlador.actualizar(999L, dto);

    assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), instanceOf(Map.class));
    assertThat((Map<?, ?>) response.getBody(), hasEntry("error", "No encontrado"));
  }

  @Test
  public void deberiaRetornar200AlActualizarDeudor() {
    DeudorDto dto = new DeudorDto(1L, "Fabio", "123");
    when(this.servicioDeudor.actualizar(eq(1L), any())).thenReturn(new Deudor(1L, "Fabio", "123"));

    ResponseEntity<?> response = this.controlador.actualizar(1L, dto);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(), instanceOf(DeudorDto.class));
    DeudorDto body = (DeudorDto) response.getBody();
    assertThat(body.getId(), is(1L));
    assertThat(body.getNombre(), is("Fabio"));
    assertThat(body.getDni(), is("123"));
  }

  @Test
  public void deberiaRetornar404AlModificarDeudorInexistente() {
    DeudorDto dto = new DeudorDto(999L, "Fabio", "123");
    when(this.servicioDeudor.modificar(eq(999L), any()))
      .thenThrow(DeudorNoEncontradoException.class);

    ResponseEntity<?> response = this.controlador.modificar(999L, dto);

    assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), instanceOf(Map.class));
    assertThat((Map<?, ?>) response.getBody(), hasEntry("error", "No encontrado"));
  }

  @Test
  public void deberiaRetornar204AlBorrarDeudor() {
    doNothing().when(this.servicioDeudor).borrar(1L);

    ResponseEntity<?> response = this.controlador.borrar(1L);

    assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
  }

  @Test
  public void deberiaRetornar404AlBorrarDeudorInexistente() {
    doThrow(DeudorNoEncontradoException.class).when(this.servicioDeudor).borrar(999L);

    ResponseEntity<?> response = this.controlador.borrar(999L);

    assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    assertThat(response.getBody(), instanceOf(Map.class));
    assertThat((Map<?, ?>) response.getBody(), hasEntry("error", "No encontrado"));
  }

  @Test
  public void deberiaRetornar200AlObtenerTodos() {
    when(this.servicioDeudor.obtenerTodos()).thenReturn(Collections.emptyList());

    ResponseEntity<?> response = this.controlador.obtenerTodos();

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(), instanceOf(List.class));
    assertThat((List<?>) response.getBody(), is(empty()));
  }

  @Test
  public void deberiaRetornar200AlModificarDeudor() {
    DeudorDto dto = new DeudorDto(1L, "Fabio", "123");
    when(this.servicioDeudor.modificar(eq(1L), any())).thenReturn(new Deudor(1L, "Fabio", "123"));

    ResponseEntity<?> response = this.controlador.modificar(1L, dto);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody(), instanceOf(DeudorDto.class));
    DeudorDto body = (DeudorDto) response.getBody();
    assertThat(body.getId(), is(1L));
    assertThat(body.getNombre(), is("Fabio"));
    assertThat(body.getDni(), is("123"));
  }

  @Test
  public void deberiaRetornar200AlObtenerEncabezados() {
    ResponseEntity<Void> response = this.controlador.obtenerEncabezados();
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  /**
   * Pruebas de handleException
   */

  @Test
  public void handleException_deberiaRetornarBadRequestParaIllegalArgumentException() {
    IllegalArgumentException exception = new IllegalArgumentException("Error");
    ResponseEntity<String> response = this.controlador.handleException(exception);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody(), is("Error"));
  }

  @Test
  public void handleException_deberiaRetornarInternalServerErrorParaComunicacion() {
    RuntimeException exception = new RuntimeException("Error de comunicacion");
    ResponseEntity<String> response = this.controlador.handleException(exception);
    assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    assertThat(response.getBody(), is("Error de comunicacion"));
  }

  @Test
  public void handleException_deberiaRetornarInternalServerErrorParaOtrasExcepciones() {
    RuntimeException exception = new RuntimeException("Otra cosa");
    ResponseEntity<String> response = this.controlador.handleException(exception);
    assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
  }
}
