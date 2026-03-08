package com.challenge.product.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.challenge.product.client.InventoryClient;
import com.challenge.product.client.dto.InventoryResponseDto;
import com.challenge.product.entity.ProductEntity;
import com.challenge.product.exception.ProductNotFoundException;
import com.challenge.product.mapper.ProductMapper;
import com.challenge.product.model.ProductRequest;
import com.challenge.product.model.ProductResponse;
import com.challenge.product.model.ProductWithStockResponse;
import com.challenge.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Pruebas unitarias para {@link ProductServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductMapper productMapper;

  @Mock
  private InventoryClient inventoryClient;

  @InjectMocks
  private ProductServiceImpl productService;

  private ProductEntity productEntity;
  private ProductResponse productResponse;
  private ProductRequest productRequest;

  @BeforeEach
  void setUp() {
    productEntity = ProductEntity.builder()
        .id(1L)
        .name("Laptop")
        .price(1500.0)
        .build();

    productResponse = new ProductResponse()
        .id(1L)
        .name("Laptop")
        .price(1500.0);

    productRequest = new ProductRequest()
        .name("Laptop")
        .price(1500.0);
  }

  /**
   * Verifica que se crea un producto correctamente.
   */
  @Test
  void shouldCreateProductSuccessfully() {
    when(productMapper.toEntity(any(ProductRequest.class))).thenReturn(productEntity);
    when(productRepository.save(any(ProductEntity.class))).thenReturn(productEntity);
    when(productMapper.toResponse(any(ProductEntity.class))).thenReturn(productResponse);

    StepVerifier.create(productService.createProduct(productRequest))
        .assertNext(response -> {
          assertNotNull(response);
          assertEquals("Laptop", response.getName());
          assertEquals(1500.0, response.getPrice());
        })
        .verifyComplete();

    verify(productRepository, times(1)).save(any(ProductEntity.class));
  }

  /**
   * Verifica que se lanza excepcion cuando el nombre es null.
   */
  @Test
  void shouldThrowExceptionWhenProductNameIsNull() {
    ProductRequest invalidRequest = new ProductRequest().price(100.0);

    StepVerifier.create(productService.createProduct(invalidRequest))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  /**
   * Verifica que se lanza excepcion cuando el precio es 0 o negativo.
   */
  @Test
  void shouldThrowExceptionWhenProductPriceIsInvalid() {
    ProductRequest invalidRequest = new ProductRequest().name("Test").price(0.0);

    StepVerifier.create(productService.createProduct(invalidRequest))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  /**
   * Verifica que se lanza excepcion cuando la solicitud es null.
   */
  @Test
  void shouldThrowExceptionWhenProductRequestIsNull() {
    StepVerifier.create(productService.createProduct(null))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  /**
   * Verifica que se obtienen todos los productos correctamente.
   */
  @Test
  void shouldGetAllProductsSuccessfully() {
    List<ProductEntity> entities = List.of(productEntity);
    when(productRepository.findAll()).thenReturn(entities);
    when(productMapper.toResponse(any(ProductEntity.class))).thenReturn(productResponse);

    StepVerifier.create(productService.getAllProducts())
        .assertNext(response -> {
          assertNotNull(response);
          assertEquals("Laptop", response.getName());
        })
        .verifyComplete();

    verify(productRepository, times(1)).findAll();
  }

  /**
   * Verifica que se filtran productos costosos correctamente.
   */
  @Test
  void shouldFilterExpensiveProductsSuccessfully() {
    ProductEntity cheapProduct = ProductEntity.builder()
        .id(2L)
        .name("Mouse")
        .price(50.0)
        .build();

    List<ProductEntity> entities = List.of(productEntity, cheapProduct);
    when(productRepository.findAll()).thenReturn(entities);
    when(productMapper.toResponse(productEntity)).thenReturn(productResponse);

    StepVerifier.create(productService.getExpensiveProducts(1000.0))
        .assertNext(response -> {
          assertNotNull(response);
          assertEquals("Laptop", response.getName());
        })
        .verifyComplete();

    verify(productRepository, times(1)).findAll();
  }

  /**
   * Verifica que se obtiene un producto por ID con stock.
   */
  @Test
  void shouldGetProductByIdWithStockSuccessfully() {
    InventoryResponseDto inventoryDto = new InventoryResponseDto(1L, 1L, 10);
    ProductWithStockResponse withStock = new ProductWithStockResponse()
        .id(1L)
        .name("Laptop")
        .price(1500.0)
        .stock(10);

    when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
    when(inventoryClient.getInventoryByProductId(1L)).thenReturn(Mono.just(inventoryDto));
    when(productMapper.toProductWithStock(productEntity, 10)).thenReturn(withStock);

    StepVerifier.create(productService.getProductById(1L))
        .assertNext(response -> {
          assertNotNull(response);
          assertEquals(1L, response.getId());
          assertEquals(10, response.getStock());
        })
        .verifyComplete();

    verify(productRepository, times(1)).findById(1L);
    verify(inventoryClient, times(1)).getInventoryByProductId(1L);
  }

  /**
   * Verifica que se lanza excepcion cuando el producto no existe.
   */
  @Test
  void shouldThrowExceptionWhenProductNotFound() {
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

    StepVerifier.create(productService.getProductById(999L))
        .expectError(ProductNotFoundException.class)
        .verify();

    verify(productRepository, times(1)).findById(999L);
  }
}

