package com.tallerwebi.presentacion.Calendario;

import com.tallerwebi.dominio.Pedidos.ItemPedido;
import com.tallerwebi.dominio.Pedidos.Pedido;
import java.util.List;
import java.util.stream.Collectors;

public class PedidoCalendarioDTO {

  private Long id;
  private String estado;
  private Double subtotal;
  private String fechaRetiro;
  private String nombreHijo;
  private String fotoHijo;
  private List<ItemDTO> items;

  public PedidoCalendarioDTO(Pedido pedido) {
    this.id = pedido.getId();
    this.estado = pedido.getEstado().name();
    this.subtotal = pedido.getSubtotal();
    this.fechaRetiro = pedido.getFechaRetiroFormateada();
    this.nombreHijo = pedido.getHijo().getNombre();
    this.fotoHijo = pedido.getHijo().getFotoPerfil();
    this.items = pedido.getItems().stream().map(ItemDTO::new).collect(Collectors.toList());
  }

  // Getters y Setters
  public Long getId() {
    return id;
  }

  public String getEstado() {
    return estado;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public String getFechaRetiro() {
    return fechaRetiro;
  }

  public String getNombreHijo() {
    return nombreHijo;
  }

  public String getFotoHijo() {
    return fotoHijo;
  }

  public List<ItemDTO> getItems() {
    return items;
  }

  public static class ItemDTO {

    private String nombreProducto;
    private String imagenProducto;
    private Integer cantidad;
    private Double subtotal;

    public ItemDTO(ItemPedido item) {
      this.nombreProducto = item.getProducto().getNombre();
      this.imagenProducto = item.getProducto().getImagen();
      this.cantidad = item.getCantidad();
      this.subtotal = item.getSubtotal();
    }

    // Getters
    public String getNombreProducto() {
      return nombreProducto;
    }

    public String getImagenProducto() {
      return imagenProducto;
    }

    public Integer getCantidad() {
      return cantidad;
    }

    public Double getSubtotal() {
      return subtotal;
    }
  }
}
