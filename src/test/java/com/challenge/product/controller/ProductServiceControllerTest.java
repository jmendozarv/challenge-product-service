package com.challenge.product.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.challenge.product.model.ProductRequest;
import com.challenge.product.model.ProductResponse;
import com.challenge.product.model.ProductWithStockResponse;
import com.challenge.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Pruebas de integracion para {@link ProductServiceController}.
 */
@WebFluxTest(ProductServiceController.class)
class ProductServiceControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private ProductService productService;

  private ProductResponse productResponse;
  private ProductRequest productRequest;

  @BeforeEach
  void setUp() {
    productResponse = new ProductResponse()
        .id(1L)
        .name("Laptop")
        .price(1500.0);

    productRequest = new ProductRequest()
        .name("Laptop")
        .price(1500.0);
  }

  /**
   * Verifica que POST /products retorna 201 al crear producto.
   */
  @Test
  void shouldCreateProductAndReturn201() {
    when(productService.createProduct(any(ProductRequest.class)))
        .thenReturn(Mono.just(productResponse));

    webTestClient.post()
        .uri("/products")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(productRequest)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(ProductResponse.class)
        .value(response -> {
          assertEquals(1L, response.getId());
          assertEquals("Laptop", response.getName());
        });
  }

  /**
   * Verifica que GET /products retorna 200 con lista de productos.
   */
  @Test
  void shouldGetAllProductsAndReturn200() {
    when(productService.getAllProducts())
        .thenReturn(Flux.just(productResponse));

    webTestClient.get()
        .uri("/products")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ProductResponse.class)
        .hasSize(1);
  }

  /**
   * Verifica que GET /products/filter/expensive filtra correctamente.
   */
  @Test
  void shouldGetExpensiveProductsAndReturn200() {
    when(productService.getExpensiveProducts(anyDouble()))
        .thenReturn(Flux.just(productResponse));

    webTestClient.get()
        .uri("/products/filter/expensive?minPrice=1000")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(ProductResponse.class)
        .hasSize(1);
  }

  /**
   * Verifica que GET /products/{id} retorna 200 con el producto.
   */
  @Test
  void shouldGetProductByIdAndReturn200() {
    ProductWithStockResponse withStock = new ProductWithStockResponse()
        .id(1L)
        .name("Laptop")
        .price(1500.0)
        .stock(10);

    when(productService.getProductById(anyLong()))
        .thenReturn(Mono.just(withStock));

    webTestClient.get()
        .uri("/products/1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(ProductWithStockResponse.class)
        .value(response -> {
          assertEquals(1L, response.getId());
          assertEquals(10, response.getStock());
        });
  }

  /**
   * Verifica que GET /products/{id} retorna 404 cuando no existe.
   */
  @Test
  void shouldReturn404WhenProductNotFound() {
    when(productService.getProductById(anyLong()))
        .thenReturn(Mono.error(new com.challenge.product.exception.ProductNotFoundException(999L)));

    webTestClient.get()
        .uri("/products/999")
        .exchange()
        .expectStatus().isNotFound();
  }
}

