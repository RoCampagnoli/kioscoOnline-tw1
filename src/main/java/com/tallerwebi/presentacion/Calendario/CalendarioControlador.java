package com.tallerwebi.presentacion.Calendario;

import com.tallerwebi.dominio.Calendario.ServicioCalendario;
import com.tallerwebi.dominio.Pedidos.Pedido;
import com.tallerwebi.dominio.Pedidos.ServicioPedido;
import com.tallerwebi.dominio.Usuario.Usuario;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalendarioControlador {

  private static final String USUARIO_SESSION = "USUARIO";
  private final ServicioCalendario servicioCalendario;
  private final ServicioPedido servicioPedido; // 👈 Inyectamos el servicio de pedidos

  public CalendarioControlador(
    ServicioCalendario servicioCalendario,
    ServicioPedido servicioPedido
  ) {
    this.servicioCalendario = servicioCalendario;
    this.servicioPedido = servicioPedido;
  }

  @GetMapping("/mi-calendario")
  public ModelAndView irAMiCalendario(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO_SESSION);
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }
    ModelAndView model = new ModelAndView("mi-calendario");
    model.addObject("usuario", usuario);
    return model;
  }

  @GetMapping("/api/calendario")
  @ResponseBody
  public List<EventoCalendarioDTO> obtenerEventos(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO_SESSION);

    if (usuario == null) {
      return List.of();
    }
    return servicioCalendario.obtenerPedidosParaCalendarioDelUsuario(usuario.getId());
  }

  // 🔴 NUEVO: Endpoint asíncrono para el modal de detalle del pedido clickeado
  @GetMapping("/api/pedidos/detalle")
  @ResponseBody
  public ResponseEntity<PedidoCalendarioDTO> obtenerDetallePedidoJson(
    @RequestParam("id") Long idPedido,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO_SESSION);
    if (usuario == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Buscamos el pedido en la base de datos
    Pedido pedido = servicioPedido.buscarPorId(idPedido);
    if (pedido == null || !pedido.getUsuario().getId().equals(usuario.getId())) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Retornamos el DTO estructurado y seguro para JSON
    PedidoCalendarioDTO PCalendarioDto = new PedidoCalendarioDTO(pedido);
    return ResponseEntity.ok(PCalendarioDto);
  }
}
