package com.challenge.product.service.impl;

import com.challenge.product.client.InventoryClient;
import com.challenge.product.entity.ProductEntity;
import com.challenge.product.exception.InventoryServiceException;
import com.challenge.product.exception.ProductNotFoundException;
import com.challenge.product.mapper.ProductMapper;
import com.challenge.product.model.ProductRequest;
import com.challenge.product.model.ProductResponse;
import com.challenge.product.model.ProductWithStockResponse;
import com.challenge.product.repository.ProductRepository;
import com.challenge.product.service.ProductService;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementacion de {@link ProductService} con adaptacion reactiva sobre repositorio JPA.
 */
@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
  private static final String MSG_PRODUCT_NAME_REQUIRED = "Product name is required";
  private static final String MSG_PRODUCT_PRICE_REQUIRED = "Product price must be greater than 0";
  private static final String MSG_PRODUCT_REQUEST_NULL = "Product request cannot be null";
  private static final String MSG_UNEXPECTED_PRODUCT_FLOW_ERROR =
      "Unexpected error while processing product flow";

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;
  private final InventoryClient inventoryClient;

  /**
   * Valida y crea un producto nuevo.
   *
   * @param request datos del producto a crear
   * @return producto creado
   */
  @Override
  public Mono<ProductResponse> createProduct(ProductRequest request) {
    LOGGER.info("Creating new product: {}", request != null ? request.getName() : "null");

    Consumer<ProductRequest> validateRequest = req -> Optional.ofNullable(req)
        .ifPresentOrElse(product -> {
          Optional.ofNullable(product.getName())
              .filter(name -> !name.isBlank())
              .orElseThrow(() -> new IllegalArgumentException(MSG_PRODUCT_NAME_REQUIRED));

          Optional.ofNullable(product.getPrice())
              .filter(price -> price > 0)
              .orElseThrow(
                  () -> new IllegalArgumentException(MSG_PRODUCT_PRICE_REQUIRED));
        }, () -> {
          throw new IllegalArgumentException(MSG_PRODUCT_REQUEST_NULL);
        });

    Supplier<ProductEntity> entitySupplier = () -> productMapper.toEntity(request);

    return Mono.fromRunnable(() -> validateRequest.accept(request))
        .then(Mono.fromSupplier(entitySupplier))
        .map(productRepository::save)
        .map(productMapper::toResponse)
        .doOnSuccess(product -> LOGGER.info("Product created successfully with id: {}", product.getId()))
        .doOnError(error -> LOGGER.error("Error creating product", error))
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(this::mapUnexpectedError);
  }

  /**
   * Obtiene todos los productos y los transforma al modelo de salida.
   *
   * @return flujo de productos
   */
  @Override
  public Flux<ProductResponse> getAllProducts() {
    LOGGER.debug("Fetching all products");

    return Mono.fromSupplier(productRepository::findAll)
        .map(products -> products.stream()
            .map(productMapper::toResponse)
            .toList())
        .doOnSuccess(products -> LOGGER.info("Retrieved {} products", products.size()))
        .flatMapMany(Flux::fromIterable)
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(this::mapUnexpectedError);
  }

  /**
   * Filtra productos por precio minimo.
   *
   * @param minPrice umbral de precio
   * @return flujo de productos que cumplen el filtro
   */
  @Override
  public Flux<ProductResponse> getExpensiveProducts(Double minPrice) {
    LOGGER.debug("Filtering products with minPrice: {}", minPrice);

    double priceThreshold = Optional.ofNullable(minPrice).orElse(0.0D);
    Predicate<ProductEntity> expensiveProduct = product -> Optional.ofNullable(product.getPrice())
        .map(price -> price > priceThreshold)
        .orElse(false);

    return Mono.fromSupplier(productRepository::findAll)
        .map(products -> products.stream()
            .filter(expensiveProduct)
            .map(productMapper::toResponse)
            .toList())
        .doOnSuccess(products -> LOGGER.info("Found {} products with price > {}", products.size(), priceThreshold))
        .flatMapMany(Flux::fromIterable)
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorMap(this::mapUnexpectedError);
  }

  /**
   * Busca un producto por id y agrega stock por defecto.
   *
   * @param id identificador del producto
   * @return producto con stock
   */
  @Override
  public Mono<ProductWithStockResponse> getProductById(Long id) {
    LOGGER.debug("Fetching product by id: {}", id);

    Supplier<ProductNotFoundException> notFoundSupplier = () -> new ProductNotFoundException(id);

    return Mono.fromSupplier(() -> productRepository.findById(id)
            .orElseThrow(notFoundSupplier))
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(product -> inventoryClient.getInventoryByProductId(product.getId())
            .map(inventory -> productMapper.toProductWithStock(product, inventory.stock())))
        .doOnSuccess(product -> LOGGER.info("Product retrieved successfully: id={}, stock={}",
            product.getId(), product.getStock()))
        .onErrorMap(this::mapUnexpectedError);
  }

  /**
   * Conserva excepciones de dominio y encapsula fallos inesperados.
   *
   * @param error error emitido por el flujo reactivo
   * @return error original o error encapsulado
   */
  private Throwable mapUnexpectedError(Throwable error) {
    if (error instanceof IllegalArgumentException
        || error instanceof ProductNotFoundException
        || error instanceof InventoryServiceException) {
      return error;
    }
    LOGGER.error("Unexpected error in product flow", error);
    return new IllegalStateException(MSG_UNEXPECTED_PRODUCT_FLOW_ERROR, error);
  }
}
