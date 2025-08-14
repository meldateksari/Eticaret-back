package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.ProductFeatureDto;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.ProductFeature;
import com.meldateksari.eticaret.repository.ProductFeatureRepository;
import com.meldateksari.eticaret.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductFeatureService {

    private final ProductFeatureRepository featureRepo;
    private final ProductRepository productRepo;

    @Transactional
    public ProductFeatureDto upsertByProductId(Long productId, ProductFeatureDto dto) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productId));

        ProductFeature feature = featureRepo.findByProductId(productId)
                .orElseGet(() -> ProductFeature.builder().product(product).build());

        feature.setMaterial(dto.getMaterial());
        feature.setColor(dto.getColor());
        feature.setSize(dto.getSize());
        feature.setTechnicalSpecs(dto.getTechnicalSpecs());

        ProductFeature saved = featureRepo.save(feature);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public ProductFeatureDto getByProductId(Long productId) {
        ProductFeature feature = featureRepo.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Feature not found for product: " + productId));
        return toDto(feature);
    }

    @Transactional
    public void deleteByProductId(Long productId) {
        featureRepo.deleteByProductId(productId);
    }

    private ProductFeatureDto toDto(ProductFeature pf) {
        return ProductFeatureDto.builder()
                .id(pf.getId())
                .productId(pf.getProduct().getId())
                .material(pf.getMaterial())
                .color(pf.getColor())
                .size(pf.getSize())
                .technicalSpecs(pf.getTechnicalSpecs())
                .build();
    }
}
