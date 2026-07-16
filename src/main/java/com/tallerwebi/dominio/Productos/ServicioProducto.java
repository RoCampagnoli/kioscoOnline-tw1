package com.tallerwebi.dominio.Productos;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ServicioProducto {
  List<Producto> obtenerListadoProductos();

  List<Producto> obtenerListadoProductosFiltrado(String categoria);

  List<CategoriaProductos> obtenerListadoCategorias();

  List<Producto> buscarProductosPorNombre(String texto);

  Producto obtenerProductoPorId(Long id);

  /**
   * Da de alta un producto nuevo, asignándole la categoría indicada. La
   * imagen es opcional: si se envía, se sube a Cloudinary y se guarda la URL
   * resultante. Si quitarFondoImagen es true (y hay imagen), se le pide a
   * Cloudinary que le remueva el fondo.
   */
  Producto crearProducto(
    Producto producto,
    Long categoriaId,
    MultipartFile imagen,
    boolean quitarFondoImagen
  );

  /**
   * Actualiza un único campo de un producto ya existente (edición estilo
   * planilla de cálculo: se hace click en la celda y se guarda al vuelo).
   * Campos soportados: nombre, descripcion, precio, cantidad, categoria.
   */
  Producto actualizarCampoProducto(Long id, String campo, String valor);

  void eliminarProducto(Long id);
}
