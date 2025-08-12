package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.repository.ProductRepository;
import com.meldateksari.eticaret.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                        .map(s -> s.replaceAll("\"", ""))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {

                System.err.println("Geçersiz genderCategoryIds parametresi: " + genderCategoryIds);
                return ResponseEntity.badRequest().build();
            }
        }

        List<ProductResponseDto> products = productService.getFilteredProducts(genderIds, categoryId, isActive);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }


    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody Product product) {
        ProductResponseDto createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductResponseDto productDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "removeImage", required = false) Boolean removeImage
    ) {
        ProductResponseDto updated = productService.updateProduct(id, productDto, image, Boolean.TRUE.equals(removeImage));
        return ResponseEntity.ok(updated);
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