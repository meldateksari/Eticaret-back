package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.ProductImage;
import com.meldateksari.eticaret.repository.ProductImageRepository;
import com.meldateksari.eticaret.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    @Override
    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public List<ProductImage> findByProductId(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    public void deleteById(Long id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public void deleteByProductId(Long productId) {
        productImageRepository.deleteByProductId(productId);
    }
}
