package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Carrito.Carrito;
import com.tallerwebi.dominio.Carrito.ItemCarrito;
import com.tallerwebi.dominio.Carrito.ServicioCarrito;
import com.tallerwebi.dominio.Hijos.Hijo;
import com.tallerwebi.dominio.Hijos.ServicioHijo;
import com.tallerwebi.dominio.Pedidos.Pedido;
import com.tallerwebi.dominio.Pedidos.ServicioPedido;
import com.tallerwebi.dominio.Productos.Producto;
import com.tallerwebi.dominio.Usuario.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class CarritoControladorTest {

  private HttpSession sessionMock;
  private CarritoControlador carritoControlador;
  private ServicioCarrito serviCarritomock;
  private ServicioHijo serviHijoMock;
  private Usuario usuarioMock;
  private Carrito carritoMock;
  private RedirectAttributes redirectAttributesMock;

  private Producto productoMock;
  private ItemCarrito itemMock;
  private Hijo hijoMock;
  private ServicioPedido serviPedidoMock;

  @BeforeEach
  public void init() {
    sessionMock = mock(HttpSession.class);
    serviCarritomock = mock(ServicioCarrito.class);
    serviPedidoMock = mock(ServicioPedido.class);
    usuarioMock = mock(Usuario.class);
    carritoMock = mock(Carrito.class);
    productoMock = mock(Producto.class);
    itemMock = mock(ItemCarrito.class);
    hijoMock = mock(Hijo.class);
    carritoControlador = new CarritoControlador(serviCarritomock, serviPedidoMock);
    redirectAttributesMock = mock(RedirectAttributes.class);

    when(usuarioMock.getId()).thenReturn(1L);
  }

  @Test
  public void siNoHayUsuarioLogueadoDebeRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mv = carritoControlador.verCarrito(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siHayUsuarioDebeMostrarLaVistaDeCarrito() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(serviPedidoMock.obtenerPedidosEnCarrito(any())).thenReturn(List.of(mock(Pedido.class)));

    ModelAndView mv = carritoControlador.verCarrito(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("carrito"));
  }

  @Test
  public void siHayUsuarioLosPedidosDebenEstarEnElModelo() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(serviCarritomock.obtenerOCrearCarrito(any())).thenReturn(carritoMock);
    when(carritoMock.getItems()).thenReturn(new ArrayList<>());

    when(serviPedidoMock.obtenerPedidosEnCarrito(any())).thenReturn(List.of(mock(Pedido.class)));

    ModelAndView mv = carritoControlador.verCarrito(sessionMock, redirectAttributesMock);

    List<Pedido> pedidos = (List<Pedido>) mv.getModel().get("pedidos");
    assertThat(pedidos.size(), equalTo(1));
  }

  // ---------- pagarDespues ----------

  @Test
  public void siNoHayUsuarioLogueadoAlPagarDespuesDebeRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mv = carritoControlador.pagarDespues(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siNoHayPedidosEnCarritoAlPagarDespuesDebeRedirigirADistribucionConError() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(serviPedidoMock.obtenerPedidosEnCarrito(1L)).thenReturn(new ArrayList<>());

    ModelAndView mv = carritoControlador.pagarDespues(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/distribucion"));
    verify(redirectAttributesMock)
      .addFlashAttribute(
        eq("errorDistribucion"),
        eq("No hay pedidos para dejar pendientes de pago")
      );
    verify(serviPedidoMock, never()).marcarPedidosEnCarritoComoPendientes(anyLong());
  }

  @Test
  public void conUnSoloPedidoEnCarritoAlPagarDespuesDebeMarcarComoPendienteVaciarCarritoYRedirigirAMisPedidos() {
    Pedido pedido = mock(Pedido.class);
    when(pedido.getId()).thenReturn(5L);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(serviPedidoMock.obtenerPedidosEnCarrito(1L)).thenReturn(List.of(pedido));

    ModelAndView mv = carritoControlador.pagarDespues(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/mis-pedidos"));
    verify(serviPedidoMock).marcarPedidosEnCarritoComoPendientes(1L);
    verify(serviCarritomock).vaciarCarrito(1L);
    verify(redirectAttributesMock)
      .addFlashAttribute(eq("mensajeInfo"), eq("Tu pedido #5 quedó como pago pendiente."));
  }

  @Test
  public void conVariosPedidosEnCarritoAlPagarDespuesElMensajeDebeSerPluralConTodosLosNumeros() {
    Pedido pedido1 = mock(Pedido.class);
    Pedido pedido2 = mock(Pedido.class);
    when(pedido1.getId()).thenReturn(4L);
    when(pedido2.getId()).thenReturn(5L);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(serviPedidoMock.obtenerPedidosEnCarrito(1L)).thenReturn(List.of(pedido1, pedido2));

    ModelAndView mv = carritoControlador.pagarDespues(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/mis-pedidos"));
    verify(redirectAttributesMock)
      .addFlashAttribute(
        eq("mensajeInfo"),
        eq("Tus pedidos #4 y #5 quedaron como pago pendientes.")
      );
  }

  // ---------- terminarMasTarde ----------

  @Test
  public void siNoHayUsuarioLogueadoAlTerminarMasTardeDebeRedirigirALogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mv = carritoControlador.terminarMasTarde(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void conPedidosEnCarritoAlTerminarMasTardeNoDebeCambiarEstadosYDebeMostrarMensaje() {
    Pedido pedido = mock(Pedido.class);
    when(pedido.getId()).thenReturn(7L);

    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(serviPedidoMock.obtenerPedidosEnCarrito(1L)).thenReturn(List.of(pedido));

    ModelAndView mv = carritoControlador.terminarMasTarde(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/mis-pedidos"));
    verify(redirectAttributesMock)
      .addFlashAttribute(eq("mensajeInfo"), eq("Tu pedido #7 quedó guardado en tu carrito."));
    verify(serviPedidoMock, never()).marcarPedidosEnCarritoComoPendientes(anyLong());
    verify(serviCarritomock, never()).vaciarCarrito(anyLong());
  }

  @Test
  public void sinPedidosEnCarritoAlTerminarMasTardeDebeRedirigirAMisPedidosSinMensaje() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    when(serviPedidoMock.obtenerPedidosEnCarrito(1L)).thenReturn(new ArrayList<>());

    ModelAndView mv = carritoControlador.terminarMasTarde(sessionMock, redirectAttributesMock);

    assertThat(mv.getViewName(), equalToIgnoringCase("redirect:/mis-pedidos"));
    verify(redirectAttributesMock, never()).addFlashAttribute(eq("mensajeInfo"), any());
  }
}
