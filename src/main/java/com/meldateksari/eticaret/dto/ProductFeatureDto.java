package com.meldateksari.eticaret.dto;



import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFeatureDto {
    private Long id;
    private Long productId;
    private String material;
    private String color;
    private String size;
    private String technicalSpecs;
}
