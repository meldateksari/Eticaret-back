package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.ProductFeature;
import com.meldateksari.eticaret.repository.ProductFeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductFeatureService {

    private final ProductFeatureRepository repository;

    public ProductFeature save(ProductFeature feature) {
        return repository.save(feature);
    }

    public Optional<ProductFeature> findByProductId(Long productId) {
        return repository.findByProductId(productId);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
