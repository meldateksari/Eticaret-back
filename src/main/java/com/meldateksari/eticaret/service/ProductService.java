package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.CategoryDto;
import com.meldateksari.eticaret.dto.ProductImageDto;
import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.model.Category;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.ProductImage;
import com.meldateksari.eticaret.repository.CategoryRepository;
import com.meldateksari.eticaret.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    private String toSlug(String name) {
        if (name == null) return null;
        String s = name.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        return s;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getFilteredProducts(List<Long> genderCategoryIds, Long categoryId, Boolean isActive) {
        List<Long> finalGenderIds = (genderCategoryIds != null && !genderCategoryIds.isEmpty()) ? genderCategoryIds : null;
        return productRepository.findWithFilters(finalGenderIds, categoryId, isActive)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return convertToDto(product);
    }

    @Transactional
    public ProductResponseDto createProduct(Product product) {
        // 1) slug boşsa isimden üret
        if (product.getSlug() == null || product.getSlug().isBlank()) {
            product.setSlug(toSlug(product.getName()));
        } else {
            product.setSlug(toSlug(product.getSlug()));
        }

        // 2) benzersiz yap
        String base = product.getSlug();
        String candidate = base;
        int i = 1;
        while (productRepository.existsBySlug(candidate)) {
            candidate = base + "-" + i++;
        }
        product.setSlug(candidate);

        // kategori ve genderCategories mevcut kodun
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

        Product saved = productRepository.save(product);
        return convertToDto(saved);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductResponseDto dto, MultipartFile image, boolean removeImage) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        product.setName(dto.getName());
        product.setSlug(dto.getSlug());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setBrand(dto.getBrand());
        product.setIsActive(Boolean.TRUE.equals(dto.getIsActive()));
        product.setWeight(dto.getWeight());

        // kategori
        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            Category newCategory = categoryRepository.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategory().getId()));
            product.setCategory(newCategory);
        } else {
            product.setCategory(null);
        }

        // gender categories
        if (dto.getGenderCategories() != null) {
            Set<Category> updatedGenderCategories = dto.getGenderCategories().stream()
                    .map(gc -> categoryRepository.findById(gc.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Gender category not found with id: " + gc.getId())))
                    .collect(Collectors.toSet());
            product.setGenderCategories(updatedGenderCategories);
        } else {
            product.setGenderCategories(null);
        }

        // Görsel işlemleri — sadece URL ile
        if (removeImage) {
            product.setImageUrl(null);
        } else if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            product.setImageUrl(dto.getImageUrl());
        }
        // image MultipartFile parametresi kullanmıyoruz

        Product saved = productRepository.save(product);
        return convertToDto(saved);
    }



    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getLatestProducts() {
        return productRepository.findTop3ByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ---- mapper YOK: manuel dönüşüm burada ----
    private ProductResponseDto convertToDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setBrand(product.getBrand());
        dto.setImageUrl(product.getImageUrl());
        dto.setWeight(product.getWeight());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // category -> CategoryDto
        if (product.getCategory() != null) {
            CategoryDto c = new CategoryDto();
            c.setId(product.getCategory().getId());
            c.setName(product.getCategory().getName());
            dto.setCategory(c);
        }

        // genderCategories -> List<CategoryDto>
        if (product.getGenderCategories() != null && !product.getGenderCategories().isEmpty()) {
            dto.setGenderCategories(
                    product.getGenderCategories().stream().map(cat -> {
                        CategoryDto cd = new CategoryDto();
                        cd.setId(cat.getId());
                        cd.setName(cat.getName());
                        return cd;
                    }).collect(Collectors.toList())
            );
        }

        // images -> List<ProductImageDto>
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setImages(
                    product.getImages().stream().map(this::toImageDto).collect(Collectors.toList())
            );
        }

        return dto;
    }

    private ProductImageDto toImageDto(ProductImage img) {
        ProductImageDto d = new ProductImageDto();
        d.setId(img.getId());
        d.setImageUrl(img.getImageUrl());
        d.setIsThumbnail(img.getIsThumbnail());
        d.setSortOrder(img.getSortOrder());
        return d;
    }
}
