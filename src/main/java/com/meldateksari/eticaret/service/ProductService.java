package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.CategoryMapper;
import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.model.Category;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.ProductImage;
import com.meldateksari.eticaret.repository.CategoryRepository;
import com.meldateksari.eticaret.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getFilteredProducts(List<Long> genderCategoryIds, Long categoryId, Boolean isActive) {
        List<Long> finalGenderIds = (genderCategoryIds != null && !genderCategoryIds.isEmpty()) ? genderCategoryIds : null;

        return productRepository.findWithFilters(finalGenderIds, categoryId, isActive).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getProductById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        Product product = optionalProduct.get();
        return convertToDto(product);
    }

    @Transactional
    public ProductResponseDto createProduct(Product product) {
        // İlişkili nesneleri yönet
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + product.getCategory().getId()));
            product.setCategory(category);
        }

        if (product.getGenderCategories() != null && !product.getGenderCategories().isEmpty()) {
            Set<Category> genderCategories = product.getGenderCategories().stream()
                    .map(gc -> categoryRepository.findById(gc.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Gender category not found with id: " + gc.getId())))
                    .collect(Collectors.toSet());
            product.setGenderCategories(genderCategories);
        }

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setSlug(updatedProduct.getSlug());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    product.setStockQuantity(updatedProduct.getStockQuantity());
                    product.setBrand(updatedProduct.getBrand());
                    product.setImageUrl(updatedProduct.getImageUrl());
                    product.setWeight(updatedProduct.getWeight());
                    product.setIsActive(updatedProduct.getIsActive());

                    if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
                        Category newCategory = categoryRepository.findById(updatedProduct.getCategory().getId())
                                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + updatedProduct.getCategory().getId()));
                        product.setCategory(newCategory);
                    } else {
                        product.setCategory(null);
                    }

                    if (updatedProduct.getGenderCategories() != null) {
                        Set<Category> updatedGenderCategories = updatedProduct.getGenderCategories().stream()
                                .map(gc -> categoryRepository.findById(gc.getId())
                                        .orElseThrow(() -> new EntityNotFoundException("Gender category not found with id: " + gc.getId())))
                                .collect(Collectors.toSet());
                        product.setGenderCategories(updatedGenderCategories);
                    } else {
                        product.setGenderCategories(null);
                    }

                    return convertToDto(productRepository.save(product));
                })
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductResponseDto convertToDto(Product product) {
        ProductResponseDto.ProductResponseDtoBuilder builder = ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .weight(product.getWeight())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt());

        if (product.getImages() != null) {
            builder.images(product.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        } else {
            builder.images(List.of());
        }

        if (product.getCategory() != null) {
            builder.category(CategoryMapper.toCategoryDto(product.getCategory()));
        }

        if (product.getGenderCategories() != null && !product.getGenderCategories().isEmpty()) {
            builder.genderCategories(product.getGenderCategories().stream()
                    .map(CategoryMapper::toCategoryDto)
                    .collect(Collectors.toList()));
        } else {
            builder.genderCategories(List.of());
        }

        return builder.build();
    }

    public List<ProductResponseDto> getLatestProducts() {
        return productRepository.findTop3ByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

}
