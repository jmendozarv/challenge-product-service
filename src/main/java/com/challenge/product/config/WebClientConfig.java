package com.challenge.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuracion de clientes HTTP reactivos con WebClient.
 */
@Configuration
public class WebClientConfig {

  /**
   * Crea un WebClient configurado para comunicarse con el servicio de inventario.
   *
   * @param inventoryBaseUrl URL base del servicio de inventario desde application.properties
   * @return instancia de WebClient configurada
   */
  @Bean
  public WebClient inventoryWebClient(
      @Value("${inventory.service.base-url}") String inventoryBaseUrl) {
    return WebClient.builder()
        .baseUrl(inventoryBaseUrl)
        .build();
  }
}
