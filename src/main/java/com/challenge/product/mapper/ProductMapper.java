package com.challenge.product.mapper;

import com.challenge.product.entity.ProductEntity;
import com.challenge.product.model.ProductRequest;
import com.challenge.product.model.ProductResponse;
import com.challenge.product.model.ProductWithStockResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convierte entre entidades de persistencia y modelos de API.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

  /**
   * Convierte una solicitud de creacion en entidad persistente.
   *
   * @param request solicitud de producto
   * @return entidad de producto
   */
  @Mapping(target = "id", ignore = true)
  ProductEntity toEntity(ProductRequest request);

  /**
   * Convierte una entidad en respuesta de producto.
   *
   * @param entity entidad de producto
   * @return respuesta de producto
   */
  ProductResponse toResponse(ProductEntity entity);

  /**
   * Convierte una entidad en respuesta enriquecida con stock.
   *
   * @param entity entidad de producto
   * @param stock cantidad de stock disponible
   * @return respuesta con stock
   */
  @Mapping(target = "stock", source = "stock")
  ProductWithStockResponse toProductWithStock(ProductEntity entity, Integer stock);

}
