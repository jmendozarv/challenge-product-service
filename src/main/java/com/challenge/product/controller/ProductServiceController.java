package com.challenge.product.controller;

import com.challenge.product.api.ProductsApi;
import com.challenge.product.model.ProductRequest;
import com.challenge.product.model.ProductResponse;
import com.challenge.product.model.ProductWithStockResponse;
import com.challenge.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador reactivo que implementa el contrato OpenAPI de productos.
 */
@AllArgsConstructor
@RestController
public class ProductServiceController implements ProductsApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceController.class);

  private final ProductService productService;

  /**
   * Crea un producto a partir del cuerpo de la solicitud.
   *
   * @param productRequest solicitud reactiva de creacion
   * @param exchange contexto web reactivo
   * @return respuesta con el producto creado
   */
  @Override
  public Mono<ResponseEntity<ProductResponse>> createProduct(Mono<ProductRequest> productRequest,
                                                             ServerWebExchange exchange) {
    LOGGER.info("POST /products - Creating new product");
    return Mono.from(productRequest)
        .flatMap(productService::createProduct)
        .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product))
        .doOnSuccess(response -> LOGGER.info("Product created successfully"))
        .doOnError(error -> LOGGER.error("Error creating product", error));
  }

  /**
   * Lista todos los productos registrados.
   *
   * @param exchange contexto web reactivo
   * @return respuesta con flujo de productos
   */
  @Override
  public Mono<ResponseEntity<Flux<ProductResponse>>> getAllProducts(
      ServerWebExchange exchange) {
    LOGGER.info("GET /products - Fetching all products");
    return Mono.just(ResponseEntity.ok(productService.getAllProducts()));
  }

  /**
   * Lista productos con precio mayor al umbral recibido.
   *
   * @param minPrice precio minimo para filtrar
   * @param exchange contexto web reactivo
   * @return respuesta con flujo filtrado
   */
  @Override
  public Mono<ResponseEntity<Flux<ProductResponse>>> getExpensiveProducts(Double minPrice,
                                                                          ServerWebExchange exchange) {
    LOGGER.info("GET /products/filter/expensive - Filtering products with minPrice: {}", minPrice);
    return Mono.just(ResponseEntity.ok(productService.getExpensiveProducts(minPrice)));
  }

  /**
   * Obtiene un producto por su identificador.
   *
   * @param id identificador del producto
   * @param exchange contexto web reactivo
   * @return respuesta con el producto encontrado
   */
  @Override
  public Mono<ResponseEntity<ProductWithStockResponse>> getProductById(Long id,
                                                                       ServerWebExchange exchange) {
    LOGGER.info("GET /products/{} - Fetching product by id", id);
    return productService.getProductById(id)
        .map(ResponseEntity::ok)
        .doOnSuccess(response -> LOGGER.info("Product retrieved successfully: id={}", id))
        .doOnError(error -> LOGGER.error("Error retrieving product with id: {}", id, error));
  }
}
