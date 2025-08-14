package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.ProductFeatureDto;
import com.meldateksari.eticaret.service.ProductFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/features")
@RequiredArgsConstructor
public class ProductFeatureController {

    private final ProductFeatureService service;

    // Create or Update (UPSERT)
    @PutMapping
    public ResponseEntity<ProductFeatureDto> upsert(
            @PathVariable Long productId,
            @RequestBody ProductFeatureDto body) {
        body.setProductId(productId);
        return ResponseEntity.ok(service.upsertByProductId(productId, body));
    }

    @GetMapping
    public ResponseEntity<ProductFeatureDto> get(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getByProductId(productId));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        service.deleteByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
