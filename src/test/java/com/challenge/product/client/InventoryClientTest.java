package com.challenge.product.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.challenge.product.exception.InventoryServiceException;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/**
 * Pruebas de integracion para {@link InventoryClient} usando MockWebServer.
 */
class InventoryClientTest {

  private MockWebServer mockWebServer;
  private InventoryClient inventoryClient;

  /**
   * Configura el servidor mock para WebClient.
   */
  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    inventoryClient = new InventoryClient(webClient);
  }

  /**
   * Cierra el servidor mock despues de cada test.
   */
  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  /**
   * Verifica que se obtiene inventario exitosamente.
   */
  @Test
  void shouldGetInventorySuccessfully() {
    mockWebServer.enqueue(new MockResponse()
        .setBody("{\"id\":1,\"productId\":100,\"stock\":50}")
        .addHeader("Content-Type", "application/json"));

    StepVerifier.create(inventoryClient.getInventoryByProductId(100L))
        .assertNext(response -> {
          assertNotNull(response);
          assertEquals(100L, response.productId());
          assertEquals(50, response.stock());
        })
        .verifyComplete();
  }

  /**
   * Verifica que se lanza excepcion cuando el inventario no existe (404).
   */
  @Test
  void shouldThrowExceptionWhenInventoryNotFound() {
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(404));

    StepVerifier.create(inventoryClient.getInventoryByProductId(100L))
        .expectError(InventoryServiceException.class)
        .verify();
  }

  /**
   * Verifica que se lanza excepcion cuando el servicio retorna error 500.
   */
  @Test
  void shouldThrowExceptionWhenInventoryServiceUnavailable() {
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(500));

    StepVerifier.create(inventoryClient.getInventoryByProductId(100L))
        .expectError(InventoryServiceException.class)
        .verify();
  }
}



