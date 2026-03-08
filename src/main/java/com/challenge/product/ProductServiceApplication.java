package com.challenge.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Product Service.
 */
@SpringBootApplication
public class ProductServiceApplication {

  /**
   * Inicia el contexto de Spring Boot.
   *
   * @param args argumentos de linea de comandos
   */
  public static void main(String[] args) {
    SpringApplication.run(ProductServiceApplication.class, args);
  }

}
