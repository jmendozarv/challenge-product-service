package com.challenge.product.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.challenge.product.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Pruebas unitarias para validar el mapeo de excepciones HTTP del handler global.
 */
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  /**
   * Verifica que ProductNotFoundException se mapee a 404.
   */
  @Test
  void shouldMapProductNotFoundTo404() {
    Mono<ResponseEntity<ErrorResponse>> result =
        handler.handleProductNotFound(new ProductNotFoundException(10L));

    StepVerifier.create(result)
        .assertNext(response -> {
          assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals(404, response.getBody().getStatus());
        })
        .verifyComplete();
  }

  /**
   * Verifica que IllegalArgumentException se mapee a 400.
   */
  @Test
  void shouldMapIllegalArgumentTo400() {
    Mono<ResponseEntity<ErrorResponse>> result =
        handler.handleBadRequest(new IllegalArgumentException("invalid"));

    StepVerifier.create(result)
        .assertNext(response -> {
          assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals(400, response.getBody().getStatus());
        })
        .verifyComplete();
  }

  /**
   * Verifica que errores no controlados se mapeen a 500.
   */
  @Test
  void shouldMapUnexpectedTo500() {
    Mono<ResponseEntity<ErrorResponse>> result =
        handler.handleUnexpected(new RuntimeException("boom"));

    StepVerifier.create(result)
        .assertNext(response -> {
          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
          assertNotNull(response.getBody());
          assertEquals(500, response.getBody().getStatus());
        })
        .verifyComplete();
  }
}
