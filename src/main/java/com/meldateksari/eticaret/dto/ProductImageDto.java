package com.meldateksari.eticaret.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private Boolean isThumbnail;
    private Integer sortOrder;
    private ProductResponseDto product;
    private byte[] image;

    public ProductImageDto(Long id, String imageUrl, Boolean isThumbnail, Integer sortOrder) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
        this.sortOrder = sortOrder;
    }
}
