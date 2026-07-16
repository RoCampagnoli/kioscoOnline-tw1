package com.tallerwebi.presentacion.kiosquero;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Productos.CategoriaProductos;
import com.tallerwebi.dominio.Productos.Producto;
import com.tallerwebi.dominio.Productos.ServicioProducto;
import com.tallerwebi.dominio.Usuario.Usuario;
import com.tallerwebi.dominio.excepcion.ProductoNoEncontradoException;
import com.tallerwebi.presentacion.Kiosquero.KiosqProductoControlador;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class KiosqProductoControladorTest {

  private KiosqProductoControlador kiosqProductoControlador;

  private ServicioProducto servicioProductoMock;
  private HttpSession sessionMock;
  private Usuario usuarioMock;
  private Producto productoMock;
  private CategoriaProductos categoriaMock;
  private BindingResult bindingResultMock;

  @BeforeEach
  public void init() {
    servicioProductoMock = Mockito.mock(ServicioProducto.class);
    kiosqProductoControlador = new KiosqProductoControlador(servicioProductoMock);
    sessionMock = Mockito.mock(HttpSession.class);
    usuarioMock = Mockito.mock(Usuario.class);
    productoMock = Mockito.mock(Producto.class);
    categoriaMock = Mockito.mock(CategoriaProductos.class);
    bindingResultMock = Mockito.mock(BindingResult.class);
  }

  @Test
  public void siNoHayUsuarioLogueadoDebeRedirigirAlLogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(sessionMock, null, null);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siElUsuarioLogueadoNoEsKiosqueroDebeRedirigirAlLogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("CLIENTE");

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(sessionMock, null, null);

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siElKiosqueroEstaLogueadoDebeMostrarLaVistaDeProductos() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(servicioProductoMock.obtenerListadoProductos()).thenReturn(List.of(productoMock));
    when(servicioProductoMock.obtenerListadoCategorias()).thenReturn(List.of(categoriaMock));

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(sessionMock, null, null);

    assertThat(mav.getViewName(), equalTo("productosKiosquero"));
  }

  @Test
  public void laVistaDeProductosDebeMostrarElUsuarioLogueado() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(usuarioMock.getNombre()).thenReturn("Rocio");
    when(servicioProductoMock.obtenerListadoProductos()).thenReturn(List.of(productoMock));
    when(servicioProductoMock.obtenerListadoCategorias()).thenReturn(List.of(categoriaMock));

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(sessionMock, null, null);

    assertThat(((Usuario) mav.getModel().get("usuario")).getNombre(), equalTo("Rocio"));
  }

  @Test
  public void laVistaDeProductosDebeMostrarElListadoDeProductosCargados() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(productoMock.getNombre()).thenReturn("Alfajor");
    when(servicioProductoMock.obtenerListadoProductos()).thenReturn(List.of(productoMock));
    when(servicioProductoMock.obtenerListadoCategorias()).thenReturn(List.of(categoriaMock));

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(sessionMock, null, null);
    List<Producto> productosObtenidos = (List<Producto>) mav.getModel().get("productos");

    assertThat(productosObtenidos, hasSize(1));
    assertThat(productosObtenidos.get(0).getNombre(), equalTo("Alfajor"));
  }

  @Test
  public void laVistaDeProductosDebeMostrarElListadoDeCategorias() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(categoriaMock.getNombreCategoria()).thenReturn("Golosinas");
    when(servicioProductoMock.obtenerListadoProductos()).thenReturn(List.of(productoMock));
    when(servicioProductoMock.obtenerListadoCategorias()).thenReturn(List.of(categoriaMock));

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(sessionMock, null, null);
    List<CategoriaProductos> categoriasObtenidas = (List<CategoriaProductos>) mav
      .getModel()
      .get("categorias");

    assertThat(categoriasObtenidas, hasSize(1));
    assertThat(categoriasObtenidas.get(0).getNombreCategoria(), equalTo("Golosinas"));
  }

  @Test
  public void siNoHayProductosCargadosDebeMostrarLaVistaConListadoVacio() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(servicioProductoMock.obtenerListadoProductos())
      .thenThrow(
        new ProductoNoEncontradoException("No se encontraron productos en la base de datos")
      );
    when(servicioProductoMock.obtenerListadoCategorias()).thenReturn(List.of(categoriaMock));

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(sessionMock, null, null);
    List<Producto> productosObtenidos = (List<Producto>) mav.getModel().get("productos");

    assertThat(mav.getViewName(), equalTo("productosKiosquero"));
    assertThat(productosObtenidos, is(empty()));
  }

  @Test
  public void siSeFiltraPorBusquedaDebeRetornarSoloLosProductosQueCoincidan() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(servicioProductoMock.buscarProductosPorNombre("Alfajor"))
      .thenReturn(List.of(productoMock));
    when(productoMock.getNombre()).thenReturn("Alfajor Jorgito");

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(
      sessionMock,
      null,
      "Alfajor"
    );
    List<Producto> productosObtenidos = (List<Producto>) mav.getModel().get("productos");

    assertThat(productosObtenidos, hasSize(1));
    assertThat(productosObtenidos.get(0).getNombre(), equalTo("Alfajor Jorgito"));
  }

  @Test
  public void siSeFiltraPorCategoriaDebeRetornarSoloLosProductosDeEsaCategoria() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(servicioProductoMock.obtenerListadoProductosFiltrado("Bebidas"))
      .thenReturn(List.of(productoMock));
    when(productoMock.getNombre()).thenReturn("Coca Cola");

    ModelAndView mav = kiosqProductoControlador.irACargaProductosKiosquero(
      sessionMock,
      "Bebidas",
      null
    );
    List<Producto> productosObtenidos = (List<Producto>) mav.getModel().get("productos");

    assertThat(productosObtenidos, hasSize(1));
    assertThat(productosObtenidos.get(0).getNombre(), equalTo("Coca Cola"));
  }

  @Test
  public void siElUsuarioNoEsKiosqueroAlCrearProductoDebeRedirigirAlLogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("CLIENTE");
    RedirectAttributes flashMock = Mockito.mock(RedirectAttributes.class);

    ModelAndView mav = kiosqProductoControlador.crearProducto(
      productoMock,
      bindingResultMock,
      1L,
      null,
      false,
      sessionMock,
      flashMock
    );

    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siSeCreaUnProductoConExitoDebeRedirigirAProductosKiosqueroConMensajeDeExito()
    throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(bindingResultMock.hasErrors()).thenReturn(false);
    RedirectAttributes flashMock = Mockito.mock(RedirectAttributes.class);

    ModelAndView mav = kiosqProductoControlador.crearProducto(
      productoMock,
      bindingResultMock,
      1L,
      null,
      false,
      sessionMock,
      flashMock
    );

    Mockito.verify(servicioProductoMock).crearProducto(productoMock, 1L, null, false);
    Mockito.verify(flashMock).addFlashAttribute("mensajeExito", "¡Producto creado con éxito!");
    assertThat(mav.getViewName(), equalTo("redirect:/productosKiosquero"));
  }

  @Test
  public void siFallaLaCreacionDelProductoDebeRedirigirConMensajeDeError() throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    when(bindingResultMock.hasErrors()).thenReturn(false);
    RedirectAttributes flashMock = Mockito.mock(RedirectAttributes.class);

    Mockito
      .doThrow(new RuntimeException("Error de base de datos"))
      .when(servicioProductoMock)
      .crearProducto(productoMock, 1L, null, false);

    ModelAndView mav = kiosqProductoControlador.crearProducto(
      productoMock,
      bindingResultMock,
      1L,
      null,
      false,
      sessionMock,
      flashMock
    );

    Mockito
      .verify(flashMock)
      .addFlashAttribute(
        Mockito.eq("mensajeError"),
        Mockito.contains("No se pudo crear el producto. Error: Error de base de datos")
      );
    assertThat(mav.getViewName(), equalTo("redirect:/productosKiosquero"));
  }

  @Test
  public void siSeCambiaLaImagenSinEstarLogueadoDebeRetornarUnauthorized() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);
    MultipartFile imagenMock = Mockito.mock(MultipartFile.class);

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.cambiarImagenProducto(
      1L,
      imagenMock,
      false,
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    assertThat(response.getBody().get("exito"), is(false));
  }

  @Test
  public void siSeCambiaLaImagenConExitoDebeRetornarOkConLaNuevaUrl() throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");

    MultipartFile imagenMock = Mockito.mock(MultipartFile.class);
    Producto productoGuardado = Mockito.mock(Producto.class);

    when(servicioProductoMock.obtenerProductoPorId(1L)).thenReturn(productoMock);
    when(productoMock.getCategoria()).thenReturn(categoriaMock);
    when(categoriaMock.getId()).thenReturn(10L);

    when(servicioProductoMock.crearProducto(productoMock, 10L, imagenMock, false))
      .thenReturn(productoGuardado);
    when(productoGuardado.getImagen()).thenReturn("http://img.url/nueva.jpg");

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.cambiarImagenProducto(
      1L,
      imagenMock,
      false,
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody().get("exito"), is(true));
    assertThat(response.getBody().get("nuevaUrl"), equalTo("http://img.url/nueva.jpg"));
  }

  @Test
  public void siFallaLaSubidaDeImagenDebeRetornarBadRequest() throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");
    MultipartFile imagenMock = Mockito.mock(MultipartFile.class);

    when(servicioProductoMock.obtenerProductoPorId(1L))
      .thenThrow(new RuntimeException("Error de disco"));

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.cambiarImagenProducto(
      1L,
      imagenMock,
      false,
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    Assertions.assertNotNull(response.getBody());
    assertThat(response.getBody().get("exito"), is(false));
    assertThat(response.getBody().get("mensaje").toString(), containsString("Error de disco"));
  }

  @Test
  public void siSeEditaRapidoSinPermisosDebeRetornarUnauthorized() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.editarRapido(
      1L,
      "precio",
      "150",
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void siSeEditaUnCampoExitosamenteDebeRetornarOkConElNuevoValor() throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");

    Producto productoEditado = Mockito.mock(Producto.class);
    when(servicioProductoMock.actualizarCampoProducto(1L, "nombre", "Alfajor Triple"))
      .thenReturn(productoEditado);

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.editarRapido(
      1L,
      "nombre",
      "Alfajor Triple",
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody().get("exito"), is(true));
    assertThat(response.getBody().get("nuevoValor"), equalTo("Alfajor Triple"));
  }

  @Test
  public void siSeEditaLaCategoriaExitosamenteDebeRetornarElNombreDeLaCategoria() throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");

    Producto productoEditado = Mockito.mock(Producto.class);
    CategoriaProductos categoriaNueva = Mockito.mock(CategoriaProductos.class);

    when(servicioProductoMock.actualizarCampoProducto(1L, "categoria", "2"))
      .thenReturn(productoEditado);
    when(productoEditado.getCategoria()).thenReturn(categoriaNueva);
    when(categoriaNueva.getNombreCategoria()).thenReturn("Kiosco");

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.editarRapido(
      1L,
      "categoria",
      "2",
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody().get("nuevoValor"), equalTo("Kiosco"));
  }

  @Test
  public void siSeProduceUnaExcepcionDeNegocioEnEditarRapidoDeBeRetornarBadRequest()
    throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");

    when(servicioProductoMock.actualizarCampoProducto(1L, "precio", "-10"))
      .thenThrow(
        new com.tallerwebi.dominio.excepcion.ProductoInvalidoException(
          "El precio no puede ser negativo"
        )
      );

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.editarRapido(
      1L,
      "precio",
      "-10",
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().get("exito"), is(false));
    assertThat(response.getBody().get("mensaje"), equalTo("El precio no puede ser negativo"));
  }

  @Test
  public void siOcurreUnErrorInesperadoEnEditarRapidoDebeRetornarInternalServerError()
    throws Exception {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");

    when(servicioProductoMock.actualizarCampoProducto(1L, "precio", "100"))
      .thenThrow(new RuntimeException("Fallo catastrófico de la base de datos"));

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.editarRapido(
      1L,
      "precio",
      "100",
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    assertThat(
      response.getBody().get("mensaje"),
      equalTo("Error inesperado al actualizar el producto.")
    );
  }

  // --- NUEVO TEST: ACCIÓN ELIMINAR ---

  @Test
  public void siSeEliminaUnProductoSinPermisosDebeRetornarUnauthorized() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.eliminarProducto(
      1L,
      sessionMock
    );

    assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
  }

  @Test
  public void siSeEliminaUnProductoExitosamenteDebeRetornarOk() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(sessionMock.getAttribute("ROL")).thenReturn("KIOSQUERO");

    ResponseEntity<Map<String, Object>> response = kiosqProductoControlador.eliminarProducto(
      1L,
      sessionMock
    );

    Mockito.verify(servicioProductoMock).eliminarProducto(1L);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(response.getBody().get("exito"), is(true));
  }
}
