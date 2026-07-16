package com.tallerwebi.presentacion.Kiosquero;

import com.tallerwebi.dominio.Productos.Producto;
import com.tallerwebi.dominio.Productos.ServicioProducto;
import com.tallerwebi.dominio.Usuario.Usuario;
import com.tallerwebi.dominio.excepcion.ProductoInvalidoException;
import com.tallerwebi.dominio.excepcion.ProductoNoEncontradoException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class KiosqProductoControlador {

  private static final String USUARIO = "USUARIO";
  private static final String ROL = "ROL";
  private static final String KIOSQUERO = "KIOSQUERO";
  private static final String VISTA_PRODUCTOS = "productosKiosquero";
  private static final String REDIRECT_PRODUCTOS = "redirect:/productosKiosquero";

  private static final String RESP_EXITO = "exito";
  private static final String RESP_MENSAJE = "mensaje";

  private static final String CAMPO_CATEGORIA = "categoria";
  private static final String TEXTO_VACIO = "";

  private final ServicioProducto servicioProducto;

  @Autowired
  public KiosqProductoControlador(ServicioProducto servicioProducto) {
    this.servicioProducto = servicioProducto;
  }

  @RequestMapping(path = "/productosKiosquero", method = RequestMethod.GET)
  public ModelAndView irACargaProductosKiosquero(
    HttpSession session,
    @RequestParam(value = "categoria", required = false) String categoria,
    @RequestParam(value = "busqueda", required = false) String busqueda
  ) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    String rol = (String) session.getAttribute(ROL);

    if (usuario == null || !KIOSQUERO.equals(rol)) {
      return new ModelAndView("redirect:/login");
    }

    ModelMap modelo = new ModelMap();
    modelo.put("usuario", usuario);
    modelo.put("categorias", this.servicioProducto.obtenerListadoCategorias());
    modelo.put("categoriaActual", categoria);
    modelo.put("busquedaActual", busqueda);

    // Se soluciona el DD-anomaly delegando la lógica a un método limpio
    modelo.put("productos", this.cargarProductosFiltrados(categoria, busqueda, modelo));

    return new ModelAndView(VISTA_PRODUCTOS, modelo);
  }

  // Método auxiliar limpio para encapsular la búsqueda y evitar DD-anomaly
  private List<Producto> cargarProductosFiltrados(
    String categoria,
    String busqueda,
    ModelMap modelo
  ) {
    try {
      if (busqueda != null && !busqueda.trim().isEmpty()) {
        return this.servicioProducto.buscarProductosPorNombre(busqueda.trim());
      }
      if (categoria != null && !categoria.trim().isEmpty() && !"TODOS".equals(categoria)) {
        return this.servicioProducto.obtenerListadoProductosFiltrado(categoria);
      }
      return this.servicioProducto.obtenerListadoProductos();
    } catch (ProductoNoEncontradoException e) {
      modelo.put("errorCargaProductos", e.getMessage());
      return Collections.emptyList();
    }
  }

  @RequestMapping(path = "/productosKiosquero/crear", method = RequestMethod.POST)
  public ModelAndView crearProducto(
    @ModelAttribute Producto producto,
    org.springframework.validation.BindingResult result, // Evita que Spring aborte con un 400 si falla el binding
    @RequestParam(value = "categoriaId", required = true) Long categoriaId,
    @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile, // Nombre alineado con el input HTML
    @RequestParam(
      value = "quitarFondoImagen",
      required = false,
      defaultValue = "false"
    ) boolean quitarFondo,
    HttpSession session,
    RedirectAttributes flash
  ) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    String rol = (String) session.getAttribute(ROL);

    if (usuario == null || !KIOSQUERO.equals(rol)) {
      return new ModelAndView("redirect:/login");
    }

    // Validación por si falta la categoría obligatoria o fallan los tipos de datos
    if (result.hasErrors() || categoriaId == null) {
      flash.addFlashAttribute(
        "mensajeError",
        "No se pudo crear el producto. Verifique los datos ingresados."
      );
      return new ModelAndView(REDIRECT_PRODUCTOS);
    }

    try {
      // El servicio recibe el MultipartFile (imagenFile) y se encarga de subirlo a Cloudinary
      this.servicioProducto.crearProducto(producto, categoriaId, imagenFile, quitarFondo);
      flash.addFlashAttribute("mensajeExito", "¡Producto creado con éxito!");
    } catch (Exception e) {
      flash.addFlashAttribute(
        "mensajeError",
        "No se pudo crear el producto. Error: " + e.getMessage()
      );
    }

    return new ModelAndView(REDIRECT_PRODUCTOS);
  }

  // 3. ACCIÓN: CAMBIAR IMAGEN (Ruta unificada)
  @RequestMapping(path = "/productosKiosquero/cambiar-imagen", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> cambiarImagenProducto(
    @RequestParam("id") Long id,
    @RequestParam("imagen") MultipartFile imagen,
    @RequestParam(
      value = "quitarFondo",
      required = false,
      defaultValue = "false"
    ) boolean quitarFondo,
    HttpSession session
  ) {
    Map<String, Object> respuesta = new HashMap<>();
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    String rol = (String) session.getAttribute(ROL);

    if (usuario == null || !KIOSQUERO.equals(rol)) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, "No autorizado");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    try {
      Producto producto = this.servicioProducto.obtenerProductoPorId(id);
      String urlNuevaImg =
        this.servicioProducto.crearProducto(
            producto,
            producto.getCategoria() != null ? producto.getCategoria().getId() : null,
            imagen,
            quitarFondo
          )
          .getImagen();

      respuesta.put(RESP_EXITO, true);
      respuesta.put("nuevaUrl", urlNuevaImg);
      respuesta.put(RESP_MENSAJE, "Imagen actualizada de manera exitosa.");
      return ResponseEntity.ok(respuesta);
    } catch (Exception e) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, "Error al subir la imagen: " + e.getMessage());
      return ResponseEntity.badRequest().body(respuesta);
    }
  }

  @RequestMapping(path = "/productosKiosquero/editar-rapido", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> editarRapido(
    @RequestParam("id") Long id,
    @RequestParam("campo") String campo,
    @RequestParam("valor") String valor,
    HttpSession session
  ) {
    Map<String, Object> respuesta = new HashMap<>();

    if (!esSesionKiosqueroValida(session)) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, "No autorizado");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    String errorValidacion = validarEntradaEditarRapido(campo, valor);
    if (errorValidacion != null) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, errorValidacion);
      return ResponseEntity.badRequest().body(respuesta);
    }

    try {
      Producto productoEditado = this.servicioProducto.actualizarCampoProducto(id, campo, valor);

      respuesta.put(RESP_EXITO, true);
      respuesta.put(RESP_MENSAJE, "Producto actualizado correctamente");
      respuesta.put("nuevoValor", obtenerValorRetorno(productoEditado, campo, valor));

      return ResponseEntity.ok(respuesta);
    } catch (ProductoInvalidoException | ProductoNoEncontradoException e) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, e.getMessage());
      return ResponseEntity.badRequest().body(respuesta);
    } catch (Exception e) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, "Error inesperado al actualizar el producto.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
  }

  @RequestMapping(path = "/productosKiosquero/eliminar", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> eliminarProducto(
    @RequestParam("id") Long id,
    HttpSession session
  ) {
    Map<String, Object> respuesta = new HashMap<>();
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    String rol = (String) session.getAttribute(ROL);

    if (usuario == null || !KIOSQUERO.equals(rol)) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, "No autorizado");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    try {
      this.servicioProducto.eliminarProducto(id);
      respuesta.put(RESP_EXITO, true);
      respuesta.put(RESP_MENSAJE, "Producto eliminado correctamente");
      return ResponseEntity.ok(respuesta);
    } catch (ProductoNoEncontradoException e) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, e.getMessage());
      return ResponseEntity.badRequest().body(respuesta);
    } catch (Exception e) {
      respuesta.put(RESP_EXITO, false);
      respuesta.put(RESP_MENSAJE, "Error al intentar eliminar el producto.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
  }

  // --- MÉTODOS AUXILIARES PRIVADOS PARA REDUCIR COMPLEJIDAD (PMD) ---

  private boolean esSesionKiosqueroValida(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);
    String rol = (String) session.getAttribute(ROL);
    return usuario != null && KIOSQUERO.equals(rol);
  }

  private String validarEntradaEditarRapido(String campo, String valor) {
    // Usamos la constante TEXTO_VACIO y eliminamos el literal del if
    if (valor == null || valor.trim().isEmpty()) {
      return "El valor ingresado no puede estar vacío.";
    }
    // Usamos CAMPO_CATEGORIA en lugar del literal directo "categoria"
    if (CAMPO_CATEGORIA.equalsIgnoreCase(campo) && !esNumeroValido(valor)) {
      return "La categoría seleccionada no es válida.";
    }
    return null;
  }

  private boolean esNumeroValido(String valor) {
    try {
      Long.parseLong(valor);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private String obtenerValorRetorno(Producto producto, String campo, String valorDefecto) {
    // Usamos CAMPO_CATEGORIA en lugar del literal directo "categoria"
    if (CAMPO_CATEGORIA.equalsIgnoreCase(campo) && producto.getCategoria() != null) {
      return producto.getCategoria().getNombreCategoria();
    }
    return valorDefecto;
  }
}
