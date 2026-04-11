package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementación de {@link ServicioDeudor} que interactúa con un servicio
 * externo
 * a través de un {@link RestTemplate}.
 */
@Service
public class ServicioDeudorImpl implements ServicioDeudor {

  private final String URL = "http://localhost:3000/deudores";
  private final String API_KEY = "tallerwebi";

  private final RestTemplate restTemplate;

  /**
   * Construye el servicio con el RestTemplate necesario para las peticiones HTTP.
   *
   * @param restTemplate el cliente HTTP a utilizar
   */
  @Autowired
  public ServicioDeudorImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Deudor> obtenerTodos() {
    try {
      HttpEntity<String> httpEntity = new HttpEntity<>(this.getHttpHeaders());
      ResponseEntity<Deudor[]> response =
        this.restTemplate.exchange(this.URL, HttpMethod.GET, httpEntity, Deudor[].class);
      return Arrays.asList(response.getBody());
    } catch (RestClientException exception) {
      this.handleException(exception);
      return Collections.emptyList();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Deudor obtenerPorDni(String dni) {
    try {
      // Crea una entidad HTTP que contiene los encabezados necesarios para la
      // petición.
      HttpEntity<String> httpEntity = new HttpEntity<>(this.getHttpHeaders());

      // Realiza una petición GET al endpoint específico del deudor (url/dni) y espera
      // una respuesta del tipo Deudor.
      ResponseEntity<Deudor> response =
        this.restTemplate.exchange(this.URL + "/" + dni, HttpMethod.GET, httpEntity, Deudor.class);

      // Retorna el cuerpo de la respuesta, que contiene el objeto Deudor
      // deserializado.
      return response.getBody();
    } catch (HttpClientErrorException exception) {
      // Captura excepciones de cliente HTTP (4xx). Si el código es 404, lanza una
      // excepción de negocio.
      if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw (DeudorNoEncontradoException) new DeudorNoEncontradoException("Deudor no encontrado")
          .initCause(exception);
      }
      // Si el error 4xx es otro (ej. 400), lo maneja genéricamente.
      this.handleException(exception);
      return null;
    } catch (RestClientException exception) {
      // Captura otros errores de comunicación REST (ej. timeout o errores de servidor
      // 5xx).
      this.handleException(exception);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Deudor crear(Deudor deudor) {
    if (deudor.getNombre() == null || deudor.getNombre().isEmpty()) {
      throw new IllegalArgumentException("Falta nombre");
    }
    try {
      HttpEntity<Deudor> httpEntity = new HttpEntity<>(deudor, this.getHttpHeaders());
      return this.restTemplate.postForObject(this.URL, httpEntity, Deudor.class);
    } catch (RestClientException exception) {
      this.handleException(exception);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Deudor actualizar(Long id, Deudor deudor) {
    if (deudor.getNombre() == null || deudor.getNombre().isEmpty()) {
      throw new IllegalArgumentException("Falta nombre");
    }
    try {
      HttpEntity<Deudor> httpEntity = new HttpEntity<>(deudor, this.getHttpHeaders());
      this.restTemplate.put(this.URL + "/" + id, httpEntity);
      return deudor;
    } catch (RestClientException exception) {
      this.handleException(exception);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Deudor modificar(Long id, Deudor deudor) {
    try {
      HttpEntity<Deudor> httpEntity = new HttpEntity<>(deudor, this.getHttpHeaders());
      Deudor modificado =
        this.restTemplate.patchForObject(this.URL + "/" + id, httpEntity, Deudor.class);
      if (modificado == null) {
        throw new DeudorNoEncontradoException("Deudor a modificar no encontrado");
      }
      return modificado;
    } catch (RestClientException exception) {
      this.handleException(exception);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void borrar(Long id) {
    try {
      HttpEntity<String> httpEntity = new HttpEntity<>(this.getHttpHeaders());
      this.restTemplate.exchange(this.URL + "/" + id, HttpMethod.DELETE, httpEntity, Deudor.class);
    } catch (RestClientException exception) {
      this.handleException(exception);
    }
  }

  /**
   * Maneja las excepciones ocurridas durante la comunicación con el servicio
   * externo.
   *
   * @param exception la excepción original lanzada por el cliente HTTP
   * @throws RuntimeException envuelta para indicar un error de comunicación
   */
  private void handleException(RestClientException exception) {
    throw new RuntimeException("Error de comunicacion con el servicio externo", exception);
  }

  /**
   * Genera los encabezados HTTP requeridos por la API externa.
   *
   * @return un objeto {@link HttpHeaders} con la configuración necesaria
   */
  private HttpHeaders getHttpHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set("apikey", API_KEY);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    return httpHeaders;
  }
}
