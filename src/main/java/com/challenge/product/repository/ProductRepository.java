package com.challenge.product.repository;

import com.challenge.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para {@link ProductEntity}.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
