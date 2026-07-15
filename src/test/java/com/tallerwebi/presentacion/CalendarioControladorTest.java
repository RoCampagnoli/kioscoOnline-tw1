package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Calendario.ServicioCalendario;
import com.tallerwebi.dominio.Hijos.Hijo;
import com.tallerwebi.dominio.Pedidos.Pedido;
import com.tallerwebi.dominio.Pedidos.ServicioPedido;
import com.tallerwebi.dominio.Usuario.Usuario;
import com.tallerwebi.presentacion.Calendario.CalendarioControlador;
import com.tallerwebi.presentacion.Calendario.PedidoCalendarioDTO;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

public class CalendarioControladorTest {

  private CalendarioControlador calendarioControlador;
  private ServicioCalendario servicioCalendarioMock;
  private HttpSession sessionMock;
  private Usuario usuarioMock;
  private ServicioPedido servicioPedidoMock;

  @BeforeEach
  public void init() {
    sessionMock = mock(HttpSession.class);
    usuarioMock = mock(Usuario.class);
    servicioCalendarioMock = mock(ServicioCalendario.class);
    servicioPedidoMock = mock(ServicioPedido.class);
    calendarioControlador = new CalendarioControlador(servicioCalendarioMock, servicioPedidoMock);
  }

  @Test
  public void queSiNoHaySesionRedirijaAlLogin() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);
    ModelAndView mav = calendarioControlador.irAMiCalendario(sessionMock);
    assertThat(mav.getViewName(), equalToIgnoringCase("redirect:/login"));
  }

  @Test
  public void siHaySesionQueVayaAlCalendario() {
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);
    ModelAndView mav = calendarioControlador.irAMiCalendario(sessionMock);
    assertThat(mav.getViewName(), equalToIgnoringCase("mi-calendario"));
  } // 🔴 NUEVO TEST: Valida el detalle del pedido cuando no hay sesión activa

  @Test
  public void obtenerDetallePedidoDebeRetornarUnauthorizedSiNoHaySesion() {
    // GIVEN
    when(sessionMock.getAttribute("USUARIO")).thenReturn(null);

    // WHEN
    ResponseEntity<PedidoCalendarioDTO> respuesta = calendarioControlador.obtenerDetallePedidoJson(
      1L,
      sessionMock
    );

    // THEN
    assertThat(respuesta.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
  }

  // 🔴 NUEVO TEST: Valida que devuelva el PedidoDTO de forma correcta si todo coincide
  @Test
  public void obtenerDetallePedidoDebeRetornarPedidoSiExisteYPerteneceAlUsuario() {
    // GIVEN
    Long pedidoId = 1L;
    Long usuarioId = 10L;

    when(usuarioMock.getId()).thenReturn(usuarioId);
    when(sessionMock.getAttribute("USUARIO")).thenReturn(usuarioMock);

    // Mocks encadenados del Pedido para armar el DTO de respuesta
    Pedido pedidoMock = mock(Pedido.class);
    Hijo hijoMock = mock(Hijo.class);

    when(pedidoMock.getId()).thenReturn(pedidoId);
    when(pedidoMock.getEstado())
      .thenReturn(com.tallerwebi.dominio.Pedidos.EstadoPedido.PAGO_PENDIENTE);
    when(pedidoMock.getSubtotal()).thenReturn(1500.0);
    when(pedidoMock.getFechaRetiroFormateada()).thenReturn("16/07/2026");
    when(pedidoMock.getItems()).thenReturn(new ArrayList<>()); // Lista vacía de ítems para simplificar

    // Mock del Hijo asociado
    when(hijoMock.getNombre()).thenReturn("Santiago");
    when(hijoMock.getFotoPerfil()).thenReturn("foto.jpg");
    when(pedidoMock.getHijo()).thenReturn(hijoMock);

    // El pedido pertenece al usuario logueado
    when(pedidoMock.getUsuario()).thenReturn(usuarioMock);

    when(servicioPedidoMock.buscarPorId(pedidoId)).thenReturn(pedidoMock);

    // WHEN
    ResponseEntity<PedidoCalendarioDTO> respuesta = calendarioControlador.obtenerDetallePedidoJson(
      pedidoId,
      sessionMock
    );

    // THEN
    assertThat(respuesta.getStatusCode(), equalTo(HttpStatus.OK));
    assertThat(respuesta.getBody().getId(), equalTo(pedidoId));
    assertThat(respuesta.getBody().getNombreHijo(), equalTo("Santiago"));
  }
}
