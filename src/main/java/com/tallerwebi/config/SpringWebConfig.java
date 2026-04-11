package com.tallerwebi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@EnableWebMvc
@Configuration
@ComponentScan(
  { "com.tallerwebi.presentacion", "com.tallerwebi.dominio", "com.tallerwebi.infraestructura" }
)
public class SpringWebConfig implements WebMvcConfigurer {

  @Autowired
  private ApplicationContext applicationContext;

  /**
   * Crea y registra un bean de tipo RestTemplate en el contexto de Spring.
   *
   * <p>Este bean se utiliza para realizar peticiones HTTP síncronas a servicios REST externos.
   * Se define aquí para que esté disponible para ser inyectado mediante {@code @Autowired}
   * en cualquier componente que lo necesite, como por ejemplo en {@link com.tallerwebi.dominio.ServicioDeudorImpl}.
   * </p>
   *
   * @return una instancia configurada de RestTemplate
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/css/**").addResourceLocations("/resources/core/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("/resources/core/js/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
  }

  @Bean
  public SpringResourceTemplateResolver templateResolver() {
    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
    templateResolver.setApplicationContext(this.applicationContext);
    templateResolver.setPrefix("/WEB-INF/views/thymeleaf/");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCacheable(true);
    return templateResolver;
  }

  @Bean
  public SpringTemplateEngine templateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver());
    templateEngine.setEnableSpringELCompiler(true);
    return templateEngine;
  }

  @Bean
  public ThymeleafViewResolver viewResolver() {
    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
    viewResolver.setTemplateEngine(templateEngine());
    return viewResolver;
  }
}
