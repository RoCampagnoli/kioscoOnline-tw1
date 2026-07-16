package com.tallerwebi.presentacion.Kiosquero;

import com.tallerwebi.dominio.Pedidos.EstadoPedido;
import com.tallerwebi.dominio.Pedidos.Pedido;
import com.tallerwebi.dominio.Pedidos.ServicioPedido;
import com.tallerwebi.dominio.Usuario.Usuario;
import com.tallerwebi.dominio.excepcion.PedidoNoEncontradoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class KiosqHomeControlador {

  private static final String RETIRO_TODOS = "TODOS";
  private static final String RETIRO_HOY = "HOY";
  private static final String RETIRO_MANIANA = "MANIANA";
  private static final String RETIRO_SEMANA = "SEMANA";

  private final ServicioPedido servicioPedido;

  @Autowired
  public KiosqHomeControlador(ServicioPedido servicioPedido) {
    this.servicioPedido = servicioPedido;
  }

  @RequestMapping(path = "/homeKiosquero", method = RequestMethod.GET)
  public ModelAndView irAlHomeKiosquero(
    HttpSession session,
    @RequestParam(value = "estado", required = false) String estadoPedido,
    @RequestParam(value = "busqueda", required = false) String busqueda,
    @RequestParam(value = "retiro", required = false) String retiroFiltro
  ) {
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");
    String rol = (String) session.getAttribute("ROL");

    // Si no está logueado o no es KIOSQUERO, afuera
    if (usuario == null || !"KIOSQUERO".equals(rol)) {
      return new ModelAndView("redirect:/login");
    }
    ModelMap modelo = new ModelMap();
    modelo.put("usuario", usuario);

    List<EstadoPedido> estadosVisiblesParaKiosquero = Arrays
      .stream(EstadoPedido.values())
      .filter(e -> e != EstadoPedido.EN_CARRITO)
      .collect(Collectors.toList());

    modelo.put("estados", estadosVisiblesParaKiosquero);
    modelo.put("estadoActual", estadoPedido);

    String retiroParaUsar = (retiroFiltro == null) ? RETIRO_HOY : retiroFiltro;
    modelo.put("retiroActual", retiroParaUsar);
    this.cargarPedidosDeLosUsuariosCLientes(modelo, estadoPedido, retiroParaUsar);
    this.cargarResultadoBusquedaPedido(modelo, busqueda);

    return new ModelAndView("homeKiosquero", modelo);
  }

  @RequestMapping(path = "/homeKiosquero/cambiar-estado", method = RequestMethod.POST)
  public ModelAndView cambiarEstadoPedido(
    @RequestParam(value = "pedidoId") Long idPedido,
    @RequestParam(value = "nuevoEstado") String estadoNuevo,
    HttpSession session,
    RedirectAttributes flash
  ) {
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");
    String rol = (String) session.getAttribute("ROL");

    // Si no está logueado o no es KIOSQUERO, afuera
    if (usuario == null || !"KIOSQUERO".equals(rol)) {
      return new ModelAndView("redirect:/login");
    }
    try {
      // Intentamos actualizar el estado del pedido
      this.servicioPedido.actualizarEstadoPedido(idPedido, estadoNuevo);

      // Si no falló, enviamos el mensaje de éxito
      flash.addFlashAttribute(
        "mensajeExito",
        "¡El pedido #" + idPedido + " cambió al estado " + estadoNuevo + " con éxito!"
      );
    } catch (Exception e) {
      // Si ocurre cualquier error (PedidoNoEncontradoException, error de mail, base de datos, etc.)
      // Atrapamos el error y enviamos un mensaje explicativo
      flash.addFlashAttribute(
        "mensajeError",
        "No se pudo cambiar el estado del pedido #" + idPedido + ". Error: " + e.getMessage()
      );
    }
    return new ModelAndView("redirect:/homeKiosquero");
  }

  private void cargarPedidosDeLosUsuariosCLientes(
    ModelMap modelo,
    String estadoPedido,
    String retiroFiltro
  ) {
    try {
      List<Pedido> pedidosClientes;
      if (estadoPedido != null && !estadoPedido.isEmpty() && !RETIRO_TODOS.equals(estadoPedido)) {
        pedidosClientes = this.servicioPedido.obtenerPedidosDeLosUsuariosFiltrado(estadoPedido);
      } else {
        pedidosClientes = this.servicioPedido.obtenerPedidosDeLosUsuarios();
      }

      List<Pedido> pedidosFiltradosPorRetiro =
        this.filtrarPedidosPorFechaRetiro(pedidosClientes, retiroFiltro);

      modelo.put("pedidosClientes", pedidosFiltradosPorRetiro);
    } catch (PedidoNoEncontradoException e) {
      modelo.put("errorBusquedaPedido", e.getMessage());
    }
  }

  private List<Pedido> filtrarPedidosPorFechaRetiro(List<Pedido> pedidos, String retiroFiltro) {
    if (retiroFiltro == null || RETIRO_TODOS.equals(retiroFiltro)) {
      return pedidos;
    }

    return pedidos
      .stream()
      .filter(p -> p.getFechaRetiro() != null)
      .filter(p -> this.coincideConFiltroDeRetiro(p.getFechaRetiro(), retiroFiltro))
      .collect(java.util.stream.Collectors.toList());
  }

  private boolean coincideConFiltroDeRetiro(java.time.LocalDate fechaRetiro, String retiroFiltro) {
    java.time.LocalDate hoy = java.time.LocalDate.now();

    java.util.Map<String, Boolean> resultadosPorFiltro = new java.util.HashMap<>();
    resultadosPorFiltro.put(RETIRO_HOY, fechaRetiro.isEqual(hoy));
    resultadosPorFiltro.put(RETIRO_MANIANA, fechaRetiro.isEqual(hoy.plusDays(1)));
    resultadosPorFiltro.put(
      RETIRO_SEMANA,
      !fechaRetiro.isBefore(hoy) && !fechaRetiro.isAfter(hoy.plusDays(7))
    );

    return resultadosPorFiltro.getOrDefault(retiroFiltro, true);
  }

  private void cargarResultadoBusquedaPedido(ModelMap modelo, String busqueda) {
    try {
      if (busqueda != null && !busqueda.trim().isEmpty()) {
        String termino = busqueda.trim();
        List<Pedido> pedidosBuscados;

        // Expresión regular: ¿el término contiene SOLO números?
        if (termino.matches("\\d+")) {
          pedidosBuscados = new ArrayList<>(); // Si entra acá, recién ahí la creamos limpia
          // Es un ID. Convertimos a Long y buscamos
          Long idPedido = Long.parseLong(termino);
          Pedido pedido = this.servicioPedido.obtenerResultadosBusquedaPedidoPorId(idPedido);
          if (pedido != null) {
            pedidosBuscados.add(pedido);
          }
        } else {
          pedidosBuscados = this.servicioPedido.obtenerResultadosBusquedaPorNombre(busqueda);
        }
        modelo.put("pedidosBuscados", pedidosBuscados);
      }
    } catch (PedidoNoEncontradoException e) {
      modelo.put("errorBusquedaPedido", e.getMessage());
    }
  }
}
