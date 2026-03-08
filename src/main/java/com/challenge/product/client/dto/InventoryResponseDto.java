package com.challenge.product.client.dto;

/**
 * Respuesta del servicio de inventario con informacion de stock.
 *
 * @param id identificador del registro de inventario
 * @param productId identificador del producto asociado
 * @param stock cantidad disponible en inventario
 */
public record InventoryResponseDto(
    Long id,
    Long productId,
    Integer stock
) {
}