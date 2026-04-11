package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Deudor;
import java.util.List;
import java.util.stream.Collectors;

public class DeudorDto {

  private Long id;
  private String nombre;
  private String dni;

  public DeudorDto() {}

  public DeudorDto(Long id, String nombre, String dni) {
    this.id = id;
    this.nombre = nombre;
    this.dni = dni;
  }

  public static List<DeudorDto> desdeColeccion(List<Deudor> deudores) {
    return deudores
      .stream()
      .map(deudor -> new DeudorDto(deudor.getId(), deudor.getNombre(), deudor.getDni()))
      .collect(Collectors.toList());
  }

  public Deudor entidad() {
    return new Deudor(this.id, this.nombre, this.dni);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }
}
