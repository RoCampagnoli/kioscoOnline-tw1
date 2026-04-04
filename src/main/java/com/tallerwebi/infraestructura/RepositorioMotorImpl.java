package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Motor;
import com.tallerwebi.dominio.RepositorioMotor;
import javax.persistence.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioMotorImpl implements RepositorioMotor {

  @Autowired
  private SessionFactory sessionFactory;

  public RepositorioMotorImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Motor motor) {
    this.sessionFactory.getCurrentSession().save(motor);
  }

  @Override
  public Boolean modificar(Long id, String string) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'modificar'");
  }

  @Override
  public Motor buscarPorId(Long id) {
    String hql = "FROM Motor WHERE id = :id";
    Query query = this.sessionFactory.getCurrentSession().createQuery(hql);
    query.setParameter("id", id);
    return (Motor) query.getSingleResult();
  }
}
