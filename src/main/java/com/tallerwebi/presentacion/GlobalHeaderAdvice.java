package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Carrito.Carrito;
import com.tallerwebi.dominio.Carrito.ServicioCarrito;
import com.tallerwebi.dominio.Usuario.Usuario;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalHeaderAdvice {

  private final ServicioCarrito servicioCarrito;

  @Autowired
  public GlobalHeaderAdvice(ServicioCarrito servicioCarrito) {
    this.servicioCarrito = servicioCarrito;
  }

  // 💡 Este método se ejecuta automáticamente antes de renderizar CUALQUIER vista
  @ModelAttribute
  public void agregarCantidadCarritoAlModelo(Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("USUARIO");

    if (usuario != null) {
      try {
        // Obtenemos el carrito de la base de datos para este usuario
        Carrito carrito = servicioCarrito.obtenerOCrearCarrito(usuario.getId());

        // 💡 Inicializamos directamente con el valor real usando un ternario.
        // Así evitamos la doble definición ('DD'-anomaly) que hacía saltar al PMD.
        int cantidad = (carrito != null && carrito.getItems() != null)
          ? carrito.getItems().size()
          : 0;
        // Inyectamos la variable de forma global en todos los modelos
        model.addAttribute("cantProductosEnCarrito", cantidad);
      } catch (Exception e) {
        // Si ocurre un error, mandamos 0 para no romper la navegación
        model.addAttribute("cantProductosEnCarrito", 0);
      }
    }
  }
}
