package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Productos.CategoriaProductos;
import com.tallerwebi.dominio.Productos.Producto;
import com.tallerwebi.dominio.Productos.RepositorioProducto;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioProducto")
public class RepositorioProductoImpl implements RepositorioProducto {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioProductoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<Producto> listarProductos() {
    return this.sessionFactory.getCurrentSession()
      .createQuery("FROM Producto", Producto.class)
      .getResultList();
  }

  @Override
  public List<Producto> listarProductosFiltrados(String categoria) {
    return this.sessionFactory.getCurrentSession()
      .createQuery("FROM Producto p WHERE p.categoria.nombreCategoria = :nombreCat", Producto.class)
      .setParameter("nombreCat", categoria)
      .getResultList();
  }

  @Override
  public List<CategoriaProductos> listarCategorias() {
    return this.sessionFactory.getCurrentSession()
      .createQuery("FROM CategoriaProductos ", CategoriaProductos.class)
      .getResultList();
  }

  @Override
  public List<Producto> buscarProductos(String texto) {
    return this.sessionFactory.getCurrentSession()
      .createQuery(
        "FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(:nombreBuscado)",
        Producto.class
      )
      .setParameter("nombreBuscado", "%" + texto + "%") // Los % permiten buscar en cualquier parte del texto
      .getResultList();
  }

  @Override
  public Producto buscarProductoPorId(long id) {
    return this.sessionFactory.getCurrentSession().get(Producto.class, id);
  }

  @Override
  public CategoriaProductos buscarCategoriaPorId(Long id) {
    return this.sessionFactory.getCurrentSession().get(CategoriaProductos.class, id);
  }

  @Override
  public void guardar(Producto producto) {
    this.sessionFactory.getCurrentSession().saveOrUpdate(producto);
  }

  @Override
  public void eliminar(Producto producto) {
    this.sessionFactory.getCurrentSession().delete(producto);
  }
}
