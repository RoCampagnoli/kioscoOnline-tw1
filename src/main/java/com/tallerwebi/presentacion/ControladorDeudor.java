package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Deudor;
import com.tallerwebi.dominio.ServicioDeudor;
import com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST encargado de gestionar las operaciones relacionadas con los deudores.
 * Proporciona endpoints para crear, leer, actualizar, modificar y eliminar deudores.
 */
@RestController
@RequestMapping("/api/deudores")
public class ControladorDeudor {

  private static final String PATH_ID = "/{id}";
  private static final String ERROR_KEY = "error";
  private static final String NOT_FOUND_MESSAGE = "No encontrado";
  private final ServicioDeudor servicioDeudor;

  /**
   * Construye el controlador con el servicio de deudores.
   *
   * @param servicioDeudor el servicio de lógica de negocio para deudores
   */
  @Autowired
  public ControladorDeudor(ServicioDeudor servicioDeudor) {
    this.servicioDeudor = servicioDeudor;
  }

  /**
   * Manejador global de excepciones para las peticiones a este controlador.
   *
   * @param exception la excepción capturada
   * @return una respuesta con el código de estado HTTP y mensaje correspondiente
   */
  @ExceptionHandler({ RuntimeException.class, IllegalArgumentException.class })
  public ResponseEntity<String> handleException(RuntimeException exception) {
    if (exception instanceof IllegalArgumentException) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    if (
      exception.getMessage() != null && exception.getMessage().contains("Error de comunicacion")
    ) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  /**
   * Obtiene la lista completa de deudores registrados.
   *
   * @return una respuesta con la lista de DTOs de deudores y estado 200 OK
   */
  @GetMapping
  public ResponseEntity<List<DeudorDto>> obtenerTodos() {
    List<Deudor> deudores = this.servicioDeudor.obtenerTodos();
    return ResponseEntity.ok(DeudorDto.desdeColeccion(deudores));
  }

  /**
   * Obtiene un deudor específico a partir de su DNI.
   *
   * @param dni el número de DNI del deudor
   * @return una respuesta con el DTO del deudor encontrado (200 OK) o error 404
   */
  @GetMapping("/{dni}")
  public ResponseEntity<?> obtenerPorDni(@PathVariable String dni) {
    try {
      Deudor deudor = this.servicioDeudor.obtenerPorDni(dni);
      return ResponseEntity.ok(new DeudorDto(deudor.getId(), deudor.getNombre(), deudor.getDni()));
    } catch (DeudorNoEncontradoException exception) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.getNotFoundResponse());
    }
  }

  /**
   * Crea un nuevo deudor a partir de los datos proporcionados.
   *
   * @param deudorDto los datos del nuevo deudor
   * @return una respuesta con el DTO del deudor creado y estado 201 Created
   */
  @PostMapping
  public ResponseEntity<DeudorDto> crear(@RequestBody DeudorDto deudorDto) {
    Deudor creado = this.servicioDeudor.crear(deudorDto.entidad());
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(new DeudorDto(creado.getId(), creado.getNombre(), creado.getDni()));
  }

  /**
   * Actualiza completamente la información de un deudor existente.
   *
   * @param id        el identificador del deudor a actualizar
   * @param deudorDto los nuevos datos del deudor
   * @return una respuesta con el DTO actualizado (200 OK) o error 404
   */
  @PutMapping(PATH_ID)
  public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody DeudorDto deudorDto) {
    try {
      Deudor actualizado = this.servicioDeudor.actualizar(id, deudorDto.entidad());
      return ResponseEntity.ok(
        new DeudorDto(actualizado.getId(), actualizado.getNombre(), actualizado.getDni())
      );
    } catch (DeudorNoEncontradoException exception) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.getNotFoundResponse());
    }
  }

  /**
   * Modifica parcialmente la información de un deudor existente.
   *
   * @param id        el identificador del deudor a modificar
   * @param deudorDto los campos a modificar
   * @return una respuesta con el DTO modificado (200 OK) o error 404
   */
  @PatchMapping(PATH_ID)
  public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody DeudorDto deudorDto) {
    try {
      Deudor modificado = this.servicioDeudor.modificar(id, deudorDto.entidad());
      return ResponseEntity.ok(
        new DeudorDto(modificado.getId(), modificado.getNombre(), modificado.getDni())
      );
    } catch (DeudorNoEncontradoException exception) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.getNotFoundResponse());
    }
  }

  /**
   * Elimina un deudor del sistema por su identificador.
   *
   * @param id el identificador del deudor a eliminar
   * @return una respuesta vacía con estado 204 No Content, o error 404
   */
  @DeleteMapping(PATH_ID)
  public ResponseEntity<?> borrar(@PathVariable Long id) {
    try {
      this.servicioDeudor.borrar(id);
      return ResponseEntity.noContent().build();
    } catch (DeudorNoEncontradoException exception) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.getNotFoundResponse());
    }
  }

  /**
   * Endpoint de verificación de existencia del recurso.
   *
   * @return una respuesta vacía con estado 200 OK
   */
  @RequestMapping(method = RequestMethod.HEAD)
  public ResponseEntity<Void> obtenerEncabezados() {
    return ResponseEntity.ok().build();
  }

  /**
   * Genera un mapa con el mensaje de error estándar para recursos no encontrados.
   *
   * @return un {@code Map} con la clave "error" y el valor "No encontrado"
   */
  private Map<String, String> getNotFoundResponse() {
    return Collections.singletonMap(ERROR_KEY, NOT_FOUND_MESSAGE);
  }
}
