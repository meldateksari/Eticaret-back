package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.repository.ProductRepository;
import com.meldateksari.eticaret.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getFilteredProducts(
            @RequestParam(required = false) String genderCategoryIds,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive
    ) {
        List<Long> genderIds = null;

        if (genderCategoryIds != null && !genderCategoryIds.trim().isEmpty()) {
            try {
                genderIds = Arrays.stream(genderCategoryIds.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(s -> s.replaceAll("\"", "")) // tırnak varsa temizle
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Logla ve frontend'e düzgün hata mesajı ver
                System.err.println("Geçersiz genderCategoryIds parametresi: " + genderCategoryIds);
                return ResponseEntity.badRequest().build(); // 400 Bad Request dön
            }
        }

        List<ProductResponseDto> products = productService.getFilteredProducts(genderIds, categoryId, isActive);
        return ResponseEntity.ok(products);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productService.convertToDto(optionalProduct.get()));
    }


    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody Product product) {
        ProductResponseDto createdProductDto = productService.createProduct(product);
        return ResponseEntity.ok(createdProductDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        ProductResponseDto updatedProductDto = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProductDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ProductResponseDto>> getLatestProducts() {
        List<ProductResponseDto> latestProducts = productService.getLatestProducts();
        return ResponseEntity.ok(latestProducts);
    }

}