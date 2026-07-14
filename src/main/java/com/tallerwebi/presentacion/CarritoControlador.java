package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrito.ServicioCarrito;
import com.tallerwebi.dominio.Pedidos.ItemPedido;
import com.tallerwebi.dominio.Pedidos.Pedido;
import com.tallerwebi.dominio.Pedidos.ServicioPedido;
import com.tallerwebi.dominio.Usuario.Usuario;
import com.tallerwebi.dominio.excepcion.ProductoNoEncontradoException;
import com.tallerwebi.dominio.excepcion.ProductoSinStockException;
import java.util.List;
import java.util.stream.Collectors; // 👈 agregar este import
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CarritoControlador {

  private final ServicioCarrito servicioCarrito;
  private final ServicioPedido servicioPedido;
  private static final int UN_SOLO_PEDIDO = 1;

  private static final String USUARIO = "USUARIO";
  private static final String CARRITO = "carrito";
  private static final String PRODUCTO_ID = "productoId";

  @Autowired
  public CarritoControlador(ServicioCarrito servicioCarrito, ServicioPedido servicioPedido) {
    this.servicioCarrito = servicioCarrito;
    this.servicioPedido = servicioPedido;
  }

  @RequestMapping(path = "/carrito/agregar", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> agregarProducto(
    @RequestParam(PRODUCTO_ID) Long productoId,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    try {
      servicioCarrito.agregarProducto(productoId, usuario.getId());
      return ResponseEntity.ok("ok");
    } catch (ProductoNoEncontradoException | ProductoSinStockException e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  @RequestMapping(path = "/carrito", method = RequestMethod.GET)
  public ModelAndView verCarrito(HttpSession session, RedirectAttributes flash) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }
    List<Pedido> pedidos = servicioPedido.obtenerPedidosEnCarrito(usuario.getId());

    // Si no hay pedidos, no dejamos entrar al carrito
    if (pedidos == null || pedidos.isEmpty()) {
      flash.addFlashAttribute(
        "errorDistribucion",
        "Hay productos sin cantidades asignadas, debes asignarlos o eliminarlos"
      );
      return new ModelAndView("redirect:/distribucion");
    }
    Double total = pedidos
      .stream()
      .flatMap(p -> p.getItems().stream())
      .mapToDouble(ItemPedido::getSubtotal)
      .sum();

    ModelMap model = new ModelMap();
    model.put("total", total);
    model.put("pedidos", pedidos);
    model.put("usuario", usuario);

    return new ModelAndView(CARRITO, model);
  }

  @RequestMapping(path = "/carrito/eliminar", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> eliminarProducto(
    @RequestParam(PRODUCTO_ID) Long productoId,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    if (usuario == null) {
      return ResponseEntity.status(401).body("Usuario no autenticado");
    }

    try {
      servicioCarrito.eliminarProducto(productoId, usuario.getId());
      return ResponseEntity.ok("ok");
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  @RequestMapping(path = "/carrito/aumentar", method = RequestMethod.POST)
  public String aumentarCantidadDeProducto(
    @RequestParam(PRODUCTO_ID) Long productoId,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    servicioCarrito.aumentarCantidad(productoId, usuario.getId());

    return "redirect:/carrito";
  }

  @RequestMapping(path = "/carrito/disminuir", method = RequestMethod.POST)
  public String restarCantidadDeProducto(
    @RequestParam(PRODUCTO_ID) Long productoId,
    HttpSession session
  ) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    servicioCarrito.disminuirCantidad(productoId, usuario.getId());

    return "redirect:/carrito";
  }

  @RequestMapping(path = "/carrito/pagar-despues", method = RequestMethod.POST)
  public ModelAndView pagarDespues(HttpSession session, RedirectAttributes flash) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    List<Pedido> pedidosEnCarrito = servicioPedido.obtenerPedidosEnCarrito(usuario.getId());

    if (pedidosEnCarrito.isEmpty()) {
      flash.addFlashAttribute("errorDistribucion", "No hay pedidos para dejar pendientes de pago");
      return new ModelAndView("redirect:/distribucion");
    }

    servicioPedido.marcarPedidosEnCarritoComoPendientes(usuario.getId());
    // Mismo criterio que en /pagar: ya están comprometidos a un pedido real, dejan de ser "candidatos"
    servicioCarrito.vaciarCarrito(usuario.getId());

    String mensaje = construirMensajeConNumerosDePedido(
      pedidosEnCarrito,
      "quedó como pago pendiente",
      "quedaron como pago pendientes"
    );
    flash.addFlashAttribute("mensajeInfo", mensaje);

    return new ModelAndView("redirect:/mis-pedidos");
  }

  @RequestMapping(path = "/carrito/terminar-mas-tarde", method = RequestMethod.GET)
  public ModelAndView terminarMasTarde(HttpSession session, RedirectAttributes flash) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    if (usuario == null) {
      return new ModelAndView("redirect:/login");
    }

    // No hace falta cambiar ningún estado: ya están guardados como EN_CARRITO
    List<Pedido> pedidosEnCarrito = servicioPedido.obtenerPedidosEnCarrito(usuario.getId());

    if (!pedidosEnCarrito.isEmpty()) {
      String mensaje = construirMensajeConNumerosDePedido(
        pedidosEnCarrito,
        "quedó guardado en tu carrito",
        "quedaron guardados en tu carrito"
      );
      flash.addFlashAttribute("mensajeInfo", mensaje);
    }

    return new ModelAndView("redirect:/mis-pedidos");
  }

  private String construirMensajeConNumerosDePedido(
    List<Pedido> pedidos,
    String sufijoSingular,
    String sufijoPlural
  ) {
    List<String> numeros = pedidos.stream().map(p -> "#" + p.getId()).collect(Collectors.toList());

    if (numeros.size() == UN_SOLO_PEDIDO) {
      return "Tu pedido " + numeros.get(0) + " " + sufijoSingular + ".";
    }

    String todosMenosUltimo = String.join(", ", numeros.subList(0, numeros.size() - 1));
    String ultimo = numeros.get(numeros.size() - 1);
    return "Tus pedidos " + todosMenosUltimo + " y " + ultimo + " " + sufijoPlural + ".";
  }
}
