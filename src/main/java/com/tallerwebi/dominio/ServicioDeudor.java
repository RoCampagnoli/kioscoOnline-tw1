package com.tallerwebi.dominio;

import java.util.List;

/**
 * Define las operaciones de negocio permitidas para la gestión de deudores.
 */
public interface ServicioDeudor {
  /**
   * Obtiene todos los deudores registrados en el sistema.
   *
   * @return una lista de objetos {@link Deudor}
   */
  List<Deudor> obtenerTodos();

  /**
   * Busca un deudor específico mediante su DNI.
   *
   * @param dni el DNI del deudor a buscar
   * @return el {@link Deudor} encontrado
   * @throws com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException si no existe el deudor
   */
  Deudor obtenerPorDni(String dni);

  /**
   * Registra un nuevo deudor en el sistema.
   *
   * @param deudor el objeto {@link Deudor} con la información a persistir
   * @return el {@link Deudor} registrado
   */
  Deudor crear(Deudor deudor);

  /**
   * Actualiza completamente la información de un deudor existente.
   *
   * @param id     el identificador del deudor a actualizar
   * @param deudor el objeto {@link Deudor} con la información actualizada
   * @return el {@link Deudor} actualizado
   * @throws com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException si no existe el deudor
   */
  Deudor actualizar(Long id, Deudor deudor);

  /**
   * Modifica parcialmente la información de un deudor existente.
   *
   * @param id     el identificador del deudor a modificar
   * @param deudor el objeto {@link Deudor} con los campos a modificar
   * @return el {@link Deudor} modificado
   * @throws com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException si no existe el deudor
   */
  Deudor modificar(Long id, Deudor deudor);

  /**
   * Elimina un deudor del sistema dado su identificador.
   *
   * @param id el identificador del deudor a borrar
   * @throws com.tallerwebi.dominio.excepcion.DeudorNoEncontradoException si no existe el deudor
   */
  void borrar(Long id);
}
