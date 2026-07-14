package com.tallerwebi.presentacion.Pagos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Pagos.ServicioMercadoPago;
import com.tallerwebi.dominio.Pedidos.Pedido;
import com.tallerwebi.dominio.Pedidos.ServicioPedido;
import com.tallerwebi.dominio.Usuario.Usuario;
import com.tallerwebi.dominio.excepcion.PedidoNoEncontradoException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorMisPedidosTest {

  private ServicioPedido servicioPedidoMock;
  private ServicioMercadoPago servicioMercadoPagoMock;
  private ControladorMisPedidos controladorMisPedidos;
  private HttpSession sessionMock;
  private RedirectAttributes flashMock;
  private Usuario usuarioMock;

  @BeforeEach
  public void init() {
    this.servicioPedidoMock = mock(ServicioPedido.class);
    this.servicioMercadoPagoMock = mock(ServicioMercadoPago.class);
    this.controladorMisPedidos =
      new ControladorMisPedidos(servicioPedidoMock, servicioMercadoPagoMock);
    this.sessionMock = mock(HttpSession.class);
    this.flashMock = mock(RedirectAttributes.class);

    this.usuarioMock = mock(Usuario.class);
    when(this.usuarioMock.getId()).thenReturn(1L);
    when(this.sessionMock.getAttribute("USUARIO")).thenReturn(this.usuarioMock);
  }

  @Test
  public void siElUsuarioNoEstaLogueadoAlVerMisPedidosDebeRedirigirAlLogin() {
    when(this.sessionMock.getAttribute("USUARIO")).thenReturn(null);
    ModelAndView mav = controladorMisPedidos.verMisPedidos(sessionMock);
    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siElUsuarioEstaLogueadoDebeMostrarSusPedidosAgrupadosPorFechaEnElModelo() {
    Pedido pedido = mock(Pedido.class);
    when(pedido.getFecha()).thenReturn(new Date()); // 👈 necesario, el controller ahora la usa

    List<Pedido> pedidos = List.of(pedido);
    when(servicioPedidoMock.obtenerTodosLosPedidos(1L)).thenReturn(pedidos);

    ModelAndView mav = controladorMisPedidos.verMisPedidos(sessionMock);

    assertThat(mav.getViewName(), equalTo("mis-pedidos"));
    assertThat(mav.getModel().get("usuario"), equalTo(usuarioMock));

    @SuppressWarnings("unchecked")
    Map<String, List<Pedido>> pedidosPorFecha = (Map<String, List<Pedido>>) mav
      .getModel()
      .get("pedidosPorFecha");

    assertThat(pedidosPorFecha, notNullValue());
    assertThat(
      pedidosPorFecha.values().stream().flatMap(List::stream).collect(Collectors.toList()),
      equalTo(pedidos)
    );
  }

  @Test
  public void siElUsuarioNoEstaLogueadoAlCancelarDebeRedirigirAlLogin() {
    when(this.sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = controladorMisPedidos.cancelarPedido(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void alCancelarUnPedidoValidoDebeLlamarAlServicioYRedirigirConMensajeDeExito() {
    ModelAndView mav = controladorMisPedidos.cancelarPedido(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-pedidos"));
    verify(servicioPedidoMock).cancelarPedido(5L, 1L);
    verify(flashMock).addFlashAttribute("mensajeExito", "El pedido #5 fue cancelado.");
  }

  @Test
  public void siElPedidoNoExisteAlCancelarDebeRedirigirConMensajeDeError() {
    doThrow(new PedidoNoEncontradoException("El pedido con ID 5 no existe."))
      .when(servicioPedidoMock)
      .cancelarPedido(5L, 1L);

    ModelAndView mav = controladorMisPedidos.cancelarPedido(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-pedidos"));
    verify(flashMock)
      .addFlashAttribute(eq("mensajeError"), contains("No se pudo cancelar el pedido"));
  }

  @Test
  public void siElUsuarioNoEstaLogueadoAlRetomarPagoDebeRedirigirAlLogin() {
    when(this.sessionMock.getAttribute("USUARIO")).thenReturn(null);

    ModelAndView mav = controladorMisPedidos.retomarPago(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:/login"));
  }

  @Test
  public void siElPedidoNoExisteAlRetomarPagoDebeRedirigirConMensajeDeError() {
    when(servicioPedidoMock.obtenerResultadosBusquedaPedidoPorId(5L)).thenReturn(null);

    ModelAndView mav = controladorMisPedidos.retomarPago(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-pedidos"));
    verify(flashMock)
      .addFlashAttribute("mensajeError", "No se pudo encontrar el pedido para pagar.");
    verify(servicioMercadoPagoMock, never()).crearPreferenciaDePago(anyList());
  }

  @Test
  public void siElPedidoPerteneceAOtroUsuarioAlRetomarPagoDebeRedirigirConMensajeDeError() {
    Pedido pedido = mock(Pedido.class);
    Usuario otroUsuario = mock(Usuario.class);
    when(otroUsuario.getId()).thenReturn(999L);
    when(pedido.getUsuario()).thenReturn(otroUsuario);
    when(servicioPedidoMock.obtenerResultadosBusquedaPedidoPorId(5L)).thenReturn(pedido);

    ModelAndView mav = controladorMisPedidos.retomarPago(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-pedidos"));
    verify(flashMock)
      .addFlashAttribute("mensajeError", "No se pudo encontrar el pedido para pagar.");
    verify(servicioMercadoPagoMock, never()).crearPreferenciaDePago(anyList());
  }

  @Test
  public void alRetomarPagoConExitoDebeRedirigirAlCheckoutDeMercadoPago() {
    Pedido pedido = mock(Pedido.class);
    when(pedido.getUsuario()).thenReturn(usuarioMock);
    when(servicioPedidoMock.obtenerResultadosBusquedaPedidoPorId(5L)).thenReturn(pedido);
    when(servicioMercadoPagoMock.crearPreferenciaDePago(List.of(pedido)))
      .thenReturn("https://mp.com/redirect");

    ModelAndView mav = controladorMisPedidos.retomarPago(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:https://mp.com/redirect"));
  }

  @Test
  public void siMercadoPagoFallaAlRetomarPagoDebeRedirigirConMensajeDeError() {
    Pedido pedido = mock(Pedido.class);
    when(pedido.getUsuario()).thenReturn(usuarioMock);
    when(servicioPedidoMock.obtenerResultadosBusquedaPedidoPorId(5L)).thenReturn(pedido);
    when(servicioMercadoPagoMock.crearPreferenciaDePago(List.of(pedido))).thenReturn(null);

    ModelAndView mav = controladorMisPedidos.retomarPago(5L, sessionMock, flashMock);

    assertThat(mav.getViewName(), equalTo("redirect:/mis-pedidos"));
    verify(flashMock)
      .addFlashAttribute(eq("mensajeError"), contains("No se pudo conectar con Mercado Pago"));
  }
}
