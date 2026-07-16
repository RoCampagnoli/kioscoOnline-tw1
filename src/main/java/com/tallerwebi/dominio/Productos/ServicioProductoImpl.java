package com.tallerwebi.dominio.Productos;

import com.tallerwebi.dominio.SubidaDeImgs.ServicioImagenes;
import com.tallerwebi.dominio.excepcion.ProductoInvalidoException;
import com.tallerwebi.dominio.excepcion.ProductoNoEncontradoException;
import java.util.List;
import java.util.Locale;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("servicioProducto")
@Transactional
public class ServicioProductoImpl implements ServicioProducto {

  private static final String CARPETA_IMAGENES_PRODUCTOS = "productos";

  private final RepositorioProducto repositorioProducto;
  private final ServicioImagenes servicioImagenes;

  @Autowired
  public ServicioProductoImpl(
    RepositorioProducto repositorioProducto,
    ServicioImagenes servicioImagenes
  ) {
    this.repositorioProducto = repositorioProducto;
    this.servicioImagenes = servicioImagenes;
  }

  @Override
  public List<Producto> obtenerListadoProductos() {
    List<Producto> productosTodos = this.repositorioProducto.listarProductos();
    if (productosTodos.isEmpty()) {
      throw new ProductoNoEncontradoException("No se encontraron productos en la base de datos");
    }
    return productosTodos;
  }

  @Override
  public List<Producto> obtenerListadoProductosFiltrado(String categoria) {
    List<Producto> productosFiltrados =
      this.repositorioProducto.listarProductosFiltrados(categoria);
    if (productosFiltrados.isEmpty()) {
      throw new ProductoNoEncontradoException("No se encontraron productos en esta categoria");
    }
    return productosFiltrados;
  }

  @Override
  public List<CategoriaProductos> obtenerListadoCategorias() {
    return this.repositorioProducto.listarCategorias();
  }

  @Override
  public List<Producto> buscarProductosPorNombre(String texto) {
    List<Producto> productosBuscados = this.repositorioProducto.buscarProductos(texto);
    if (productosBuscados.isEmpty()) {
      throw new ProductoNoEncontradoException(
        "No se encontró ninguna coincidencia para: " + texto + ". Intente otra búsqueda"
      );
    }
    return productosBuscados;
  }

  @Override
  public Producto obtenerProductoPorId(Long id) {
    Producto producto = this.repositorioProducto.buscarProductoPorId(id);
    if (producto == null) {
      throw new ProductoNoEncontradoException("No se encontró el producto con id: " + id);
    }
    return producto;
  }

  @Override
  public Producto crearProducto(
    Producto producto,
    Long categoriaId,
    MultipartFile imagenFile, // <-- Consistencia de nombres: imagenFile
    boolean quitarFondoImagen
  ) {
    // Validaciones de negocio obligatorias
    this.validarNombre(producto.getNombre());
    this.validarPrecio(producto.getPrecio());
    this.validarCantidad(producto.getCantidad());

    // Forzamos que la categoría sea obligatoria en la capa de negocio
    if (categoriaId == null) {
      throw new ProductoInvalidoException("La categoría es obligatoria para crear un producto");
    }

    // Resolvemos la categoría directamente usando el Long (evitando conversiones String innecesarias)
    producto.setCategoria(this.resolverCategoriaPorId(categoriaId));

    // Subida de la imagen a Cloudinary usando el servicio modularizado
    if (imagenFile != null && !imagenFile.isEmpty()) {
      String urlImagen =
        this.servicioImagenes.subirImagenProducto(
            imagenFile,
            CARPETA_IMAGENES_PRODUCTOS,
            quitarFondoImagen
          );
      producto.setImagen(urlImagen);
    } else {
      // Imagen genérica por defecto si no se sube ninguna
      producto.setImagen(
        "https://res.cloudinary.com/dqrka5zry/image/upload/v1783997395/producto-default.png"
      );
    }

    this.repositorioProducto.guardar(producto);
    return producto;
  }

  @Override
  public Producto actualizarCampoProducto(Long id, String campo, String valor) {
    if (campo == null) {
      throw new ProductoInvalidoException("Debe indicar qué campo desea modificar");
    }

    Producto producto = this.obtenerProductoPorId(id);
    this.procesarActualizacionDeCampo(producto, campo.toLowerCase(Locale.ROOT), valor);
    this.repositorioProducto.guardar(producto);
    return producto;
  }

  @Override
  public void eliminarProducto(Long id) {
    Producto producto = this.obtenerProductoPorId(id);
    this.repositorioProducto.eliminar(producto);
  }

  // --- NUEVO MÉTODO AUXILIAR PARA EVITAR PARSEO REDUNDANTE DE LONG A STRING Y VICEVERSA ---
  private CategoriaProductos resolverCategoriaPorId(Long id) {
    CategoriaProductos categoria = this.repositorioProducto.buscarCategoriaPorId(id);
    if (categoria == null) {
      throw new ProductoInvalidoException("La categoría seleccionada no existe");
    }
    return categoria;
  }

  // Reducimos la complejidad ciclomática delegando el switch a un método privado separado
  private void procesarActualizacionDeCampo(Producto producto, String campo, String valor) {
    switch (campo) {
      case "nombre":
        this.validarNombre(valor);
        producto.setNombre(valor.trim());
        break;
      case "descripcion":
        producto.setDescripcion(valor == null ? null : valor.trim());
        break;
      case "precio":
        double precio = this.parsearPrecio(valor);
        this.validarPrecio(precio);
        producto.setPrecio(precio);
        break;
      case "cantidad":
        Integer cantidad = this.parsearCantidad(valor);
        this.validarCantidad(cantidad);
        producto.setCantidad(cantidad);
        break;
      case "categoria":
        producto.setCategoria(this.resolverCategoria(valor));
        break;
      default:
        throw new ProductoInvalidoException("Campo no editable: " + campo);
    }
  }

  private void validarNombre(String nombre) {
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new ProductoInvalidoException("El nombre del producto no puede estar vacío");
    }
  }

  private void validarPrecio(double precio) {
    if (precio < 0) {
      throw new ProductoInvalidoException("El precio no puede ser negativo");
    }
  }

  private void validarCantidad(Integer cantidad) {
    if (cantidad == null || cantidad < 0) {
      throw new ProductoInvalidoException("La cantidad no puede ser negativa");
    }
  }

  // Solución AvoidCatchingNPE y PreserveStackTrace: validamos nulos explícitamente e incluimos la causa
  private double parsearPrecio(String valor) {
    if (valor == null) {
      throw new ProductoInvalidoException("El precio ingresado no es válido");
    }
    try {
      return Double.parseDouble(valor.trim().replace(",", "."));
    } catch (NumberFormatException e) {
      throw new ProductoInvalidoException("El precio ingresado no es válido", e);
    }
  }

  private Integer parsearCantidad(String valor) {
    if (valor == null) {
      throw new ProductoInvalidoException("La cantidad ingresada no es válida");
    }
    try {
      return Integer.parseInt(valor.trim());
    } catch (NumberFormatException e) {
      throw new ProductoInvalidoException("La cantidad ingresada no es válida", e);
    }
  }

  private CategoriaProductos resolverCategoria(String idCategoria) {
    if (idCategoria == null || idCategoria.trim().isEmpty()) {
      throw new ProductoInvalidoException("La categoría seleccionada no es válida");
    }
    try {
      Long id = Long.parseLong(idCategoria.trim());
      return this.resolverCategoriaPorId(id); // <-- Reutiliza la lógica de búsqueda y validación de existencia
    } catch (NumberFormatException e) {
      throw new ProductoInvalidoException("La categoría seleccionada no es válida", e);
    }
  }
}
