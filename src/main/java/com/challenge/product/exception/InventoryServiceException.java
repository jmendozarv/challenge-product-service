package com.challenge.product.exception;

/**
 * Excepcion lanzada cuando hay problemas con el servicio de inventario.
 */
public class InventoryServiceException extends RuntimeException {

  /**
   * Crea la excepcion con un mensaje y la causa raiz.
   *
   * @param message detalle del error
   * @param cause causa original del error
   */
  public InventoryServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Crea la excepcion con un mensaje personalizado.
   *
   * @param message detalle del error
   */
  public InventoryServiceException(String message) {
    super(message);
  }
}

