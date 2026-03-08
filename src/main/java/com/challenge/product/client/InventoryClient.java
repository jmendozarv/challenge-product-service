package com.challenge.product.client;

import com.challenge.product.client.dto.InventoryResponseDto;
import com.challenge.product.exception.InventoryServiceException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Cliente para integraciones con el servicio de inventario.
 */
@Component
@RequiredArgsConstructor
public class InventoryClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(InventoryClient.class);

  private final WebClient inventoryWebClient;

  /**
   * Obtiene la informacion de inventario para un producto especifico.
   *
   * @param productId identificador del producto
   * @return informacion reactiva del inventario
   * @throws InventoryServiceException si el servicio de inventario no esta disponible o no encuentra el producto
   */
  public Mono<InventoryResponseDto> getInventoryByProductId(Long productId) {
    LOGGER.debug("Requesting inventory for product id: {}", productId);

    return inventoryWebClient.get()
        .uri("/inventory/{productId}", productId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError,
            response -> {
              LOGGER.warn("Inventory not found for product id: {}, status: {}",
                  productId, response.statusCode());
              return Mono.error(new InventoryServiceException(
                  "Inventory not found for product id: " + productId));
            })
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              LOGGER.error("Inventory service unavailable, status: {}", response.statusCode());
              return Mono.error(new InventoryServiceException(
                  "Inventory service unavailable"));
            })
        .bodyToMono(InventoryResponseDto.class)
        .doOnSuccess(inventory -> LOGGER.debug("Inventory retrieved successfully for product id: {}", productId))
        .doOnError(error -> LOGGER.error("Error retrieving inventory for product id: {}", productId, error));
  }

}
