package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.ProductImageDto;
import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.ProductImage;
import com.meldateksari.eticaret.repository.ProductImageRepository;
import com.meldateksari.eticaret.repository.ProductRepository;
import com.meldateksari.eticaret.service.ProductImageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
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



    @Transactional
    public ProductImageDto add(ProductImageDto dto) {
        ProductImage img = new ProductImage();
        img.setImageUrl(dto.getImageUrl());
        img.setIsThumbnail(Boolean.TRUE.equals(dto.getIsThumbnail()));
        img.setSortOrder(dto.getSortOrder());

        Long productId = dto.getProduct().getId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        img.setProduct(product);

        ProductImage saved = productImageRepository.save(img);
        return convertToDto(saved);
    }

    private ProductImageDto convertToDto(ProductImage entity) {
        ProductImageDto dto = new ProductImageDto();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setIsThumbnail(entity.getIsThumbnail());
        dto.setSortOrder(entity.getSortOrder());

        // sadece id göndermek yeterli olabilir
        ProductResponseDto productDto = new ProductResponseDto();
        productDto.setId(entity.getProduct().getId());
        dto.setProduct(productDto);

        return dto;
    }
}
