package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tallerwebi.dominio.Motor;
import com.tallerwebi.dominio.RepositorioMotor;
import com.tallerwebi.dominio.Vehiculo;
import com.tallerwebi.infraestructura.config.HibernateInfaestructuraTestConfig;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfaestructuraTestConfig.class })
@Transactional
public class RepositorioMotorImplTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioMotor repositorioMotor;

  @BeforeEach
  public void init() {
    this.repositorioMotor = new RepositorioMotorImpl(this.sessionFactory);
  }

  @Test
  @Rollback
  public void deberiaGuardarUnMotor() {
    Motor motor = new Motor(1L, "MiMotor");

    this.repositorioMotor.guardar(motor);

    String hql = "FROM Motor Where nombre = :nombre";
    Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
    query.setParameter("nombre", "MiMotor");
    Motor motorBuscado = (Motor) query.getSingleResult();

    assertThat(motorBuscado, is(equalTo(motor)));
  }

  @Test
  @Rollback
  public void deberiaObtenerUnMotorExistentePorSuIdentificador() {
    Motor motor = new Motor(1L, "MiMotor");
    // El metodo save devuelve el id generado, usamos ese para buscarlo posteriormente
    // Esto nos ayuda con la generación de IDs desde la base datos y su correlacion
    // Si la prueba siguiente se ejecutó primero, el ID 1 fue generado y luego eliminado por el rollback
    // pero esto no regenera la secuencia interna de IDs
    Long idGenerado = (Long) this.sessionFactory.getCurrentSession().save(motor);

    Motor motorObtenido = this.repositorioMotor.buscarPorId(idGenerado);

    assertThat(motorObtenido, is(equalTo(motor)));
  }

  @Test
  @Rollback
  public void deberiaGuardarUnMotorConVehiculosAsociados() {
    // Motor 1 ---- N Vehiculos
    Vehiculo vehiculo = new Vehiculo("Uno");
    Vehiculo otroVehiculo = new Vehiculo("Dos");
    Motor motor = new Motor(1L, "");
    motor.agregarVehiculo(vehiculo);
    motor.agregarVehiculo(otroVehiculo);

    this.repositorioMotor.guardar(motor);

    Motor motorObtenido = this.repositorioMotor.buscarPorId(1L);

    assertThat(motorObtenido, is(equalTo(motor)));
  }
}
