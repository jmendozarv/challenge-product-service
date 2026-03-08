package com.challenge.product.exception;

/**
 * Excepcion lanzada cuando no existe un producto para el criterio solicitado.
 */
public class ProductNotFoundException extends RuntimeException {

  /**
   * Crea la excepcion con un mensaje basado en el id del producto.
   *
   * @param id identificador del producto no encontrado
   */
  public ProductNotFoundException(Long id) {
    super("Product not found with id: " + id);
  }

  /**
   * Crea la excepcion con un mensaje personalizado.
   *
   * @param message detalle del error
   */
  public ProductNotFoundException(String message) {
    super(message);
  }
}