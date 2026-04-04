package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
// @Table(name = "engine")
public class Motor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 200, nullable = false)
  private String nombre;

  // @OneToOne
  // private Creador creador;

  // motor 1 --- N vehiculos
  @OneToMany(mappedBy = "motor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Vehiculo> vehiculos = new ArrayList<>();

  @Transient
  private byte edad;

  public Motor(Long id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  public Long getId() {
    return this.id;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void agregarVehiculo(Vehiculo vehiculo) {
    this.vehiculos.add(vehiculo);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Motor other = (Motor) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    return true;
  }
}
