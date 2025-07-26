package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.model.ProductFeature;
import com.meldateksari.eticaret.service.ProductFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/product-features")
@RequiredArgsConstructor
public class ProductFeatureController {

    private final ProductFeatureService service;

    @PostMapping
    public ResponseEntity<ProductFeature> save(@RequestBody ProductFeature feature) {
        return ResponseEntity.ok(service.save(feature));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Optional<ProductFeature>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(service.findByProductId(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
