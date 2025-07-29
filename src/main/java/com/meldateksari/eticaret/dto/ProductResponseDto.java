package com.meldateksari.eticaret.dto;

import lombok.Builder; // Builder'ı import et
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String brand;
    private String imageUrl;
    private Boolean isActive;
    private BigDecimal weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryDto category;
    private List<CategoryDto> genderCategories;
    private List<String> images;
}