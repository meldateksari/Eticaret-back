package com.meldateksari.eticaret.repository;

import com.meldateksari.eticaret.model.ProductFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Long> {
    Optional<ProductFeature> findByProductId(Long productId);
}
