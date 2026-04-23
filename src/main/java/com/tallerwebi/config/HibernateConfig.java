package com.tallerwebi.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

  @Bean
  public DataSource dataSource() {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    String dbHost = dotenv.get("DB_HOST", "localhost");
    String dbPort = dotenv.get("DB_PORT", "3306");
    String dbName = dotenv.get("DB_NAME", "tallerwebi");
    String dbUser = dotenv.get("DB_USER", "user");
    String dbPassword = dotenv.get("DB_PASSWORD", "user");

    String url = String.format(
      "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
      dbHost,
      dbPort,
      dbName
    );

    dataSource.setUrl(url);
    dataSource.setUsername(dbUser);
    dataSource.setPassword(dbPassword);
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    return dataSource;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan("com.tallerwebi.dominio");
    sessionFactory.setHibernateProperties(hibernateProperties());
    return sessionFactory;
  }

  @Bean
  public HibernateTransactionManager transactionManager() {
    return new HibernateTransactionManager(sessionFactory(dataSource()).getObject());
  }

  private Properties hibernateProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
    properties.setProperty("hibernate.show_sql", "true");
    properties.setProperty("hibernate.format_sql", "true");
    properties.setProperty("hibernate.hbm2ddl.auto", "create");
    properties.setProperty("hibernate.connection.characterEncoding", "utf8");
    properties.setProperty("hibernate.connection.CharSet", "utf8");
    properties.setProperty("hibernate.connection.useUnicode", "true");
    return properties;
  }
}
