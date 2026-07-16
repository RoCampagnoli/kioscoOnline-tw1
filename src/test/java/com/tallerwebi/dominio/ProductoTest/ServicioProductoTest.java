package com.tallerwebi.dominio.ProductoTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Productos.*;
import com.tallerwebi.dominio.SubidaDeImgs.ServicioImagenes;
import com.tallerwebi.dominio.excepcion.ProductoInvalidoException;
import com.tallerwebi.dominio.excepcion.ProductoNoEncontradoException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

public class ServicioProductoTest {

  private ServicioProducto servicioProducto;
  private RepositorioProducto repositorioProductoMock;
  private ServicioImagenes servicioImagenesMock;
  private Producto productoMock;
  private CategoriaProductos categoriaMock;
  private MultipartFile imagenMock;

  @BeforeEach
  public void init() {
    repositorioProductoMock = Mockito.mock(RepositorioProducto.class);
    servicioImagenesMock = Mockito.mock(ServicioImagenes.class);
    servicioProducto = new ServicioProductoImpl(repositorioProductoMock, servicioImagenesMock);
    productoMock = Mockito.mock(Producto.class);
    categoriaMock = Mockito.mock(CategoriaProductos.class);
    imagenMock = Mockito.mock(MultipartFile.class);
  }

  @Test
  public void cuandoSeSolicitanTodosLosProductosDebeBuscarEnElRepoYDevolverlo() {
    //preparacion
    List<Producto> listaSimulada = List.of(productoMock);
    when(repositorioProductoMock.listarProductos()).thenReturn(listaSimulada);

    //ejecucion
    List<Producto> productosObtenidos = servicioProducto.obtenerListadoProductos();

    //validacion
    assertThat(productosObtenidos, hasSize(1));
    verify(repositorioProductoMock, times(1)).listarProductos();
  }

  @Test
  public void cuandoSeSolicitanTodosLosProductosYNoHay_debeLanzarExcepcion() {
    List<Producto> listaSimulada = List.of();
    when(repositorioProductoMock.listarProductos()).thenReturn(listaSimulada);

    assertThrows(
      ProductoNoEncontradoException.class,
      () -> servicioProducto.obtenerListadoProductos()
    );
  }

  @Test
  public void cuandoSeFiltraPorCategoriaDebeRetornarLosProductosDeEsaCategoria() {
    String categoria = "categoria";
    List<Producto> listaSimulada = List.of(productoMock);
    when(repositorioProductoMock.listarProductosFiltrados(categoria)).thenReturn(listaSimulada);

    List<Producto> productosObtenidos = servicioProducto.obtenerListadoProductosFiltrado(categoria);
    assertThat(productosObtenidos, hasSize(1));
  }

  @Test
  public void cuandoSeFiltraPorCategoriaYNoHayProductosDebeLanzarExcepcion() {
    when(repositorioProductoMock.listarProductosFiltrados("categoria")).thenReturn(List.of()); //devuelve lista vacia

    assertThrows(
      ProductoNoEncontradoException.class,
      () -> servicioProducto.obtenerListadoProductosFiltrado("categoria")
    );
  }

  @Test
  public void cuandoSeBuscaPorNombreDebeRetornarLosProductosConEseNombre() {
    String busqueda = "busqueda";
    Producto productoMock2 = Mockito.mock(Producto.class);
    List<Producto> listaSimulada = List.of(productoMock, productoMock2);
    when(repositorioProductoMock.buscarProductos(busqueda)).thenReturn(listaSimulada);

    List<Producto> productosObtenidos = servicioProducto.buscarProductosPorNombre(busqueda);
    assertThat(productosObtenidos, hasSize(2));
  }

  @Test
  public void siSeBuscaUnProductoYNoExisteDebeLanzarExcepcion() {
    when(repositorioProductoMock.buscarProductos("nombre")).thenReturn(List.of()); //que devuelva una lista vacia

    assertThrows(
      ProductoNoEncontradoException.class,
      () -> servicioProducto.buscarProductosPorNombre("nombre")
    );
  }

  @Test
  public void alObtenerUnProductoPorIdDebeRetornarloSiExiste() {
    when(productoMock.getId()).thenReturn(1L);
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(productoMock);

    Producto productoObtenido = servicioProducto.obtenerProductoPorId(1L);

    assertThat(productoObtenido.getId(), equalTo(1L));
  }

  @Test
  public void alObtenerUnProductoPorIdInexistenteDebeLanzarExcepcion() {
    when(repositorioProductoMock.buscarProductoPorId(99L)).thenReturn(null);

    assertThrows(
      ProductoNoEncontradoException.class,
      () -> servicioProducto.obtenerProductoPorId(99L)
    );
  }

  @Test
  public void alCrearUnProductoValidoDeberiaGuardarloConSuCategoriaSinImagen() {
    // Usamos una instancia real para que retenga los valores que setea el servicio
    Producto productoReal = new Producto();
    productoReal.setNombre("Alfajor");
    productoReal.setPrecio(150.0);
    productoReal.setCantidad(10);

    // Mockeamos para que el repositorio devuelva la categoría mockeada
    when(repositorioProductoMock.buscarCategoriaPorId(1L)).thenReturn(categoriaMock);

    // Ejecutamos la creación del producto
    Producto productoCreado = servicioProducto.crearProducto(productoReal, 1L, null, false);

    // Verificamos que se le asigne la imagen por defecto en lugar de null
    assertThat(
      productoCreado.getImagen(),
      is("https://res.cloudinary.com/dqrka5zry/image/upload/v1783997395/producto-default.png")
    );
  }

  @Test
  public void alCrearUnProductoConImagenDebeSubirlaYAsignarLaUrlAlProducto() {
    // Usamos una instancia real para que retenga los valores que setea el servicio
    Producto productoReal = new Producto();
    productoReal.setNombre("Alfajor");
    productoReal.setPrecio(150.0);
    productoReal.setCantidad(10);

    // Mockear la existencia de la categoría
    when(repositorioProductoMock.buscarCategoriaPorId(1L)).thenReturn(categoriaMock);

    // Mockear la subida de la imagen
    when(imagenMock.isEmpty()).thenReturn(false);
    when(servicioImagenesMock.subirImagenProducto(imagenMock, "productos", false))
      .thenReturn("http://res.cloudinary.com/url-de-la-imagen.png");

    // Ejecución pasándole el ID 1L de categoría
    Producto productoCreado = servicioProducto.crearProducto(productoReal, 1L, imagenMock, false);

    assertThat(productoCreado.getImagen(), is("http://res.cloudinary.com/url-de-la-imagen.png"));
  }

  @Test
  public void alCrearUnProductoConImagenYQuitarFondoDebeInvocarAlServicioConFlagTrue() {
    // Usamos una instancia real para que retenga los valores que setea el servicio
    Producto productoReal = new Producto();
    productoReal.setNombre("Alfajor");
    productoReal.setPrecio(150.0);
    productoReal.setCantidad(10);

    // Mockear la existencia de la categoría
    when(repositorioProductoMock.buscarCategoriaPorId(1L)).thenReturn(categoriaMock);

    // Mockear comportamiento de subida de imagen con remoción de fondo activo (true)
    when(imagenMock.isEmpty()).thenReturn(false);
    when(servicioImagenesMock.subirImagenProducto(imagenMock, "productos", true))
      .thenReturn("http://res.cloudinary.com/url-de-la-imagen-sin-fondo.png");

    Producto productoCreado = servicioProducto.crearProducto(productoReal, 1L, imagenMock, true);

    // Verificar que la URL resultante coincida y se haya invocado al servicio con flag true
    assertThat(
      productoCreado.getImagen(),
      is("http://res.cloudinary.com/url-de-la-imagen-sin-fondo.png")
    );
  }

  @Test
  public void alCrearUnProductoConImagenVaciaNoDebeIntentarSubirla() {
    // Usamos una instancia real para que retenga los valores que setea el servicio
    Producto productoReal = new Producto();
    productoReal.setNombre("Alfajor");
    productoReal.setPrecio(150.0);
    productoReal.setCantidad(10);

    // Mockear la existencia de la categoría
    when(repositorioProductoMock.buscarCategoriaPorId(1L)).thenReturn(categoriaMock);

    // Mockear imagen vacía
    when(imagenMock.isEmpty()).thenReturn(true);

    Producto productoCreado = servicioProducto.crearProducto(productoReal, 1L, imagenMock, false);

    // Debe asignarse la imagen por defecto porque el archivo subido estaba vacío
    assertThat(
      productoCreado.getImagen(),
      is("https://res.cloudinary.com/dqrka5zry/image/upload/v1783997395/producto-default.png")
    );
  }

  @Test
  public void alCrearUnProductoSinNombreDebeLanzarExcepcionYNoGuardarlo() {
    Producto producto = new Producto();
    producto.setNombre("   ");
    producto.setPrecio(500.0);
    producto.setCantidad(10);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.crearProducto(producto, null, null, false)
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alCrearUnProductoConPrecioNegativoDebeLanzarExcepcion() {
    Producto producto = new Producto();
    producto.setNombre("Alfajor");
    producto.setPrecio(-10.0);
    producto.setCantidad(10);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.crearProducto(producto, null, null, false)
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alCrearUnProductoConCantidadNegativaDebeLanzarExcepcion() {
    Producto producto = new Producto();
    producto.setNombre("Alfajor");
    producto.setPrecio(500.0);
    producto.setCantidad(-3);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.crearProducto(producto, null, null, false)
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alCrearUnProductoConCategoriaInexistenteDebeLanzarExcepcion() {
    Producto producto = new Producto();
    producto.setNombre("Alfajor");
    producto.setPrecio(500.0);
    producto.setCantidad(10);
    when(repositorioProductoMock.buscarCategoriaPorId(99L)).thenReturn(null);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.crearProducto(producto, 99L, null, false)
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alEditarElNombreDeUnProductoDebeActualizarloYGuardarlo() {
    Producto producto = new Producto();
    producto.setNombre("Alfajor");
    producto.setPrecio(500.0);
    producto.setCantidad(10);
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    Producto productoActualizado = servicioProducto.actualizarCampoProducto(
      1L,
      "nombre",
      "Chocolatín"
    );

    assertThat(productoActualizado.getNombre(), equalTo("Chocolatín"));
    verify(repositorioProductoMock, times(1)).guardar(producto);
  }

  @Test
  public void alEditarElNombreDeUnProductoConValorVacioDebeLanzarExcepcion() {
    Producto producto = new Producto();
    producto.setNombre("Alfajor");
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.actualizarCampoProducto(1L, "nombre", "")
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alEditarElPrecioDeUnProductoDebeActualizarloYGuardarlo() {
    Producto producto = new Producto();
    producto.setNombre("Alfajor");
    producto.setPrecio(500.0);
    producto.setCantidad(10);
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    Producto productoActualizado = servicioProducto.actualizarCampoProducto(1L, "precio", "650.50");

    assertThat(productoActualizado.getPrecio(), equalTo(650.50));
  }

  @Test
  public void alEditarElPrecioConUnValorNoNumericoDebeLanzarExcepcion() {
    Producto producto = new Producto();
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.actualizarCampoProducto(1L, "precio", "no-es-un-numero")
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alEditarLaCantidadDeUnProductoDebeActualizarlaYGuardarla() {
    Producto producto = new Producto();
    producto.setNombre("Alfajor");
    producto.setPrecio(500.0);
    producto.setCantidad(10);
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    Producto productoActualizado = servicioProducto.actualizarCampoProducto(1L, "cantidad", "20");

    assertThat(productoActualizado.getCantidad(), equalTo(20));
  }

  @Test
  public void alEditarLaCantidadConUnValorNegativoDebeLanzarExcepcion() {
    Producto producto = new Producto();
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.actualizarCampoProducto(1L, "cantidad", "-5")
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alEditarLaDescripcionDeUnProductoDebeActualizarlaYGuardarla() {
    Producto producto = new Producto();
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    Producto productoActualizado = servicioProducto.actualizarCampoProducto(
      1L,
      "descripcion",
      "Con dulce de leche"
    );

    assertThat(productoActualizado.getDescripcion(), equalTo("Con dulce de leche"));
  }

  @Test
  public void alEditarLaCategoriaDeUnProductoDebeActualizarlaYGuardarla() {
    Producto producto = new Producto();
    CategoriaProductos categoria = new CategoriaProductos();
    categoria.setId(2L);
    categoria.setNombreCategoria("Bebidas");
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);
    when(repositorioProductoMock.buscarCategoriaPorId(2L)).thenReturn(categoria);

    Producto productoActualizado = servicioProducto.actualizarCampoProducto(1L, "categoria", "2");

    assertThat(productoActualizado.getCategoria().getNombreCategoria(), equalTo("Bebidas"));
  }

  @Test
  public void alEditarUnCampoNoSoportadoDebeLanzarExcepcion() {
    Producto producto = new Producto();
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(producto);

    assertThrows(
      ProductoInvalidoException.class,
      () -> servicioProducto.actualizarCampoProducto(1L, "imagen", "http://algo.com/img.png")
    );
    verify(repositorioProductoMock, never()).guardar(any());
  }

  @Test
  public void alEditarUnProductoInexistenteDebeLanzarExcepcion() {
    when(repositorioProductoMock.buscarProductoPorId(99L)).thenReturn(null);

    assertThrows(
      ProductoNoEncontradoException.class,
      () -> servicioProducto.actualizarCampoProducto(99L, "nombre", "Alfajor")
    );
  }

  @Test
  public void alEliminarUnProductoExistenteDebeBorrarloDelRepositorio() {
    when(repositorioProductoMock.buscarProductoPorId(1L)).thenReturn(productoMock);

    servicioProducto.eliminarProducto(1L);

    verify(repositorioProductoMock, times(1)).eliminar(productoMock);
  }

  @Test
  public void alEliminarUnProductoInexistenteDebeLanzarExcepcionYNoIntentarBorrarlo() {
    when(repositorioProductoMock.buscarProductoPorId(99L)).thenReturn(null);

    assertThrows(ProductoNoEncontradoException.class, () -> servicioProducto.eliminarProducto(99L));
    verify(repositorioProductoMock, never()).eliminar(any());
  }
}
