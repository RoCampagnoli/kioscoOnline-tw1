package com.tallerwebi.dominio.Productos;

import java.util.List;

public interface RepositorioProducto {
  List<Producto> listarProductos();

  List<Producto> listarProductosFiltrados(String categoria);

  List<CategoriaProductos> listarCategorias();

  List<Producto> buscarProductos(String texto);

  Producto buscarProductoPorId(long id);

  CategoriaProductos buscarCategoriaPorId(Long id);

  void guardar(Producto producto);

  void eliminar(Producto producto);
}
