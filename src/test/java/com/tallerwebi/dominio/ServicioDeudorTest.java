package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ServicioDeudorTest {

  private final String URL = "http://localhost:3000/deudores";

  private RestTemplate restTemplate;
  private ServicioDeudor servicioDeudor;

  @BeforeEach
  public void init() {
    this.restTemplate = mock(RestTemplate.class);
    this.servicioDeudor = new ServicioDeudorImpl(this.restTemplate);
  }

  @Test
  public void deberiaObtenerTodosLosDeudoresExitosamente() {
    when(this.restTemplate.exchange(eq(this.URL), eq(HttpMethod.GET), any(), eq(Deudor[].class)))
      .thenReturn(ResponseEntity.ok(new Deudor[] { new Deudor(1L, "Fabio", "123") }));

    List<Deudor> resultado = this.servicioDeudor.obtenerTodos();

    assertThat(resultado, hasSize(1));
    assertThat(resultado.get(0).getNombre(), is("Fabio"));
    assertThat(resultado.get(0).getDni(), is("123"));
    assertThat(resultado.get(0).getId(), is(1L));
  }

  @Test
  public void deberiaObtenerDeudorPorDniExitosamente() {
    Deudor deudor = new Deudor(1L, "Fabio", "123");
    when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Deudor.class)))
      .thenReturn(ResponseEntity.ok(deudor));

    Deudor resultado = this.servicioDeudor.obtenerPorDni("123");

    assertThat(resultado, is(notNullValue()));
    assertThat(resultado.getNombre(), is("Fabio"));
    assertThat(resultado.getDni(), is("123"));
    assertThat(resultado.getId(), is(1L));
  }

  @Test
  public void deberiaLanzarExcepcionCuandoNoEncuentraDeudorPorDni() {
    when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Deudor.class)))
      .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

    assertThrows(DeudorNoEncontradoException.class, () -> this.servicioDeudor.obtenerPorDni("999"));
  }

  @Test
  public void deberiaCrearDeudor() {
    Deudor nuevo = new Deudor(null, "Juan", "111");
    when(this.restTemplate.postForObject(eq(this.URL), any(), eq(Deudor.class)))
      .thenReturn(new Deudor(2L, "Juan", "111"));

    Deudor creado = this.servicioDeudor.crear(nuevo);

    assertThat(creado.getId(), is(2L));
  }

  @Test
  public void deberiaActualizarDeudorExistente() {
    Deudor deudor = new Deudor(1L, "Fabio", "123");

    Deudor resultado = this.servicioDeudor.actualizar(1L, deudor);

    assertThat(resultado.getNombre(), is("Fabio"));
    assertThat(resultado.getDni(), is("123"));
    verify(this.restTemplate, times(1)).put(eq(this.URL + "/1"), any());
  }

  @Test
  public void deberiaModificarDeudorExistente() {
    Deudor deudor = new Deudor(1L, "Fabio", "123");
    when(this.restTemplate.patchForObject(eq(this.URL + "/1"), any(), eq(Deudor.class)))
      .thenReturn(deudor);

    Deudor modificado = this.servicioDeudor.modificar(1L, deudor);

    assertThat(modificado.getNombre(), is("Fabio"));
  }

  @Test
  public void deberiaBorrarDeudorExitosamente() {
    this.servicioDeudor.borrar(1L);

    verify(this.restTemplate, times(1))
      .exchange(eq(this.URL + "/1"), eq(HttpMethod.DELETE), any(), eq(Deudor.class));
  }

  @Test
  public void deberiaLanzarRuntimeExceptionCuandoFallaObtenerTodos() {
    when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Deudor[].class)))
      .thenThrow(new RestClientException("Error"));

    assertThrows(RuntimeException.class, () -> this.servicioDeudor.obtenerTodos());
  }

  @Test
  public void deberiaLanzarRuntimeExceptionCuandoFallaObtenerPorDni() {
    when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Deudor.class)))
      .thenThrow(new RestClientException("Error"));

    assertThrows(RuntimeException.class, () -> this.servicioDeudor.obtenerPorDni("123"));
  }

  @Test
  public void deberiaLanzarRuntimeExceptionCuandoFallaObtenerPorDniGeneral() {
    when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Deudor.class)))
      .thenThrow(new RestClientException("Error General"));

    assertThrows(RuntimeException.class, () -> this.servicioDeudor.obtenerPorDni("123"));
  }

  @Test
  public void deberiaLanzarRuntimeExceptionCuandoFallaCrearDeudor() {
    when(this.restTemplate.postForObject(anyString(), any(), eq(Deudor.class)))
      .thenThrow(new RestClientException("Error"));

    assertThrows(
      RuntimeException.class,
      () -> this.servicioDeudor.crear(new Deudor(1L, "Fabio", "123"))
    );
  }

  @Test
  public void deberiaLanzarRuntimeExceptionCuandoFallaActualizarDeudor() {
    doThrow(new RestClientException("Error")).when(this.restTemplate).put(anyString(), any());

    assertThrows(
      RuntimeException.class,
      () -> this.servicioDeudor.actualizar(1L, new Deudor(1L, "Fabio", "123"))
    );
  }

  @Test
  public void deberiaLanzarRuntimeExceptionCuandoElEndpointObtenerDeudorPorDniLanzaRestClientException() {
    when(this.restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Deudor.class)))
      .thenThrow(new RestClientException("Error"));

    assertThrows(RuntimeException.class, () -> this.servicioDeudor.obtenerPorDni("123"));
  }

  @Test
  public void deberiaLanzarRuntimeExceptionCuandoBorrarDeudorLanzaRestClientException() {
    doThrow(new RestClientException("Error"))
      .when(this.restTemplate)
      .exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Deudor.class));

    assertThrows(RuntimeException.class, () -> this.servicioDeudor.borrar(1L));
  }

  @Test
  public void deberiaLanzarIllegalArgumentExceptionSiNombreEsNuloEnCrear() {
    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioDeudor.crear(new Deudor(1L, null, "123"))
    );
  }

  @Test
  public void deberiaLanzarIllegalArgumentExceptionSiNombreEsVacioEnCrear() {
    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioDeudor.crear(new Deudor(1L, "", "123"))
    );
  }

  @Test
  public void deberiaLanzarIllegalArgumentExceptionSiNombreEsNuloEnActualizar() {
    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioDeudor.actualizar(1L, new Deudor(1L, null, "123"))
    );
  }

  @Test
  public void deberiaLanzarIllegalArgumentExceptionSiNombreEsVacioEnActualizar() {
    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioDeudor.actualizar(1L, new Deudor(1L, "", "123"))
    );
  }
}
