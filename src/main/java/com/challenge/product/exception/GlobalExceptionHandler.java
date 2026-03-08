package com.challenge.product.exception;

import com.challenge.product.model.ErrorResponse;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * Maneja excepciones de la API y las transforma en respuestas estandarizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String MSG_UNEXPECTED_INTERNAL_ERROR = "Unexpected internal error";

  /**
   * Mapea errores de producto no encontrado a HTTP 404.
   *
   * @param ex excepcion de producto no encontrado
   * @return respuesta de error con estado 404
   */
  @ExceptionHandler(ProductNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleProductNotFound(ProductNotFoundException ex) {
    LOGGER.warn("Product not found: {}", ex.getMessage());
    return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /**
   * Mapea errores del servicio de inventario a HTTP 503.
   *
   * @param ex excepcion del servicio de inventario
   * @return respuesta de error con estado 503
   */
  @ExceptionHandler(InventoryServiceException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleInventoryServiceError(InventoryServiceException ex) {
    LOGGER.error("Inventory service error: {}", ex.getMessage());
    return buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
  }

  /**
   * Mapea errores de validacion de negocio a HTTP 400.
   *
   * @param ex excepcion de argumento invalido
   * @return respuesta de error con estado 400
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(IllegalArgumentException ex) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Mapea errores internos controlados a HTTP 500.
   *
   * @param ex excepcion de estado invalido
   * @return respuesta de error con estado 500
   */
  @ExceptionHandler(IllegalStateException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleUnexpectedState(IllegalStateException ex) {
    LOGGER.error("Controlled internal error while processing request", ex);
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, MSG_UNEXPECTED_INTERNAL_ERROR);
  }

  /**
   * Mapea errores no controlados a HTTP 500.
   *
   * @param ex excepcion inesperada
   * @return respuesta de error con estado 500
   */
  @ExceptionHandler(Throwable.class)
  public Mono<ResponseEntity<ErrorResponse>> handleUnexpected(Throwable ex) {
    LOGGER.error("Unhandled error while processing request", ex);
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, MSG_UNEXPECTED_INTERNAL_ERROR);
  }

  /**
   * Construye la estructura estandar de error para la API.
   *
   * @param status estado HTTP de la respuesta
   * @param message mensaje de error
   * @return respuesta reactiva con el cuerpo de error
   */
  private Mono<ResponseEntity<ErrorResponse>> buildError(HttpStatus status, String message) {
    ErrorResponse response = new ErrorResponse()
        .message(message)
        .status(status.value())
        .timestamp(OffsetDateTime.now());
    return Mono.just(ResponseEntity.status(status).body(response));
  }
}
