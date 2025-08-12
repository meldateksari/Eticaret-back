package com.meldateksari.eticaret.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private Boolean isThumbnail;
    private Integer sortOrder;
    private ProductResponseDto product;
}
