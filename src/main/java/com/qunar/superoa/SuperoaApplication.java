package com.qunar.superoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/25_下午3:41
 * @Despriction: 入口
 */

@EnableWebMvc
@EnableFeignClients
@ComponentScan(basePackages = {"com.qunar.superoa","org.activiti"})
@SpringBootApplication()
public class SuperoaApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SuperoaApplication.class);
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    super.onStartup(servletContext);
    servletContext.addListener(new HttpSessionEventPublisher());
  }

  public static void main(String[] args) {
    SpringApplication.run(SuperoaApplication.class, args);
  }
}
