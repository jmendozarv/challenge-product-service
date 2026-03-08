package com.challenge.product.service;

import com.challenge.product.model.ProductRequest;
import com.challenge.product.model.ProductResponse;
import com.challenge.product.model.ProductWithStockResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Define operaciones de negocio para gestionar productos de forma reactiva.
 */
public interface ProductService {

  /**
   * Crea un producto a partir de una solicitud de entrada.
   *
   * @param request datos del producto a crear
   * @return producto creado
   */
  Mono<ProductResponse> createProduct(ProductRequest request);

  /**
   * Recupera todos los productos registrados.
   *
   * @return flujo de productos
   */
  Flux<ProductResponse> getAllProducts();

  /**
   * Recupera productos cuyo precio supera el umbral indicado.
   *
   * @param minPrice precio minimo para el filtro
   * @return flujo de productos filtrados
   */
  Flux<ProductResponse> getExpensiveProducts(Double minPrice);

  /**
   * Obtiene un producto por su identificador.
   *
   * @param id identificador del producto
   * @return producto con informacion de stock
   */
  Mono<ProductWithStockResponse> getProductById(Long id);
}
