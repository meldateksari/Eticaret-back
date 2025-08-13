package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.ProductImageDto;
import com.meldateksari.eticaret.model.ProductImage;
import com.meldateksari.eticaret.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    public ResponseEntity<ProductImageDto> addImage(@RequestBody ProductImageDto dto) {
        return ResponseEntity.ok(productImageService.add(dto));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageDto>> getImagesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.findByProductId(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        productImageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteImagesByProductId(@PathVariable Long productId) {
        productImageService.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
