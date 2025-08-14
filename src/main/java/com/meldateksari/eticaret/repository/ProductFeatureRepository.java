package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.model.ProductFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Long> {
    Optional<ProductFeature> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    void deleteByProductId(Long productId);
}
