package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.ProductResponseDto;
import com.meldateksari.eticaret.enums.InteractionType;
import com.meldateksari.eticaret.model.Product;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.model.UserProductInteraction;
import com.meldateksari.eticaret.repository.ProductRepository;
import com.meldateksari.eticaret.repository.UserRepository;
import com.meldateksari.eticaret.security.CustomUserDetails;
import com.meldateksari.eticaret.service.UserProductInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class UserProductInteractionController  {

    private final UserProductInteractionService interactionService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDto> getProductDetail(@PathVariable Long productId) {

        Long currentUserId = getCurrentUserId();
        Optional<Product> viewedProductOptional = productRepository.findById(productId);

        if (viewedProductOptional.isPresent()) {


            if (currentUserId != null) {
                Optional<User> currentUserOptional = userRepository.findById(currentUserId);
                currentUserOptional.ifPresent(user -> {
                    UserProductInteraction interaction = UserProductInteraction.builder()
                            .user(user)
                            .product(viewedProductOptional.get())
                            .interactionType(InteractionType.VIEW.name())
                            .timestamp(LocalDateTime.now())
                            .build();
                    interactionService.logInteraction(interaction);
                });
            }


            Product product = viewedProductOptional.get();
            ProductResponseDto productDto = ProductResponseDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .slug(product.getSlug())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stockQuantity(product.getStockQuantity())
                    .brand(product.getBrand())
                    .imageUrl(product.getImageUrl())
                    .isActive(product.getIsActive())
                    .weight(product.getWeight())
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    // Kategori ve diğer listeleri burada doldurmanız gerekir.
                    // Örneğin: .category(new CategoryDto(...))
                    .build();

            return ResponseEntity.ok(productDto);
        }

        // Ürün bulunamazsa 404 Not Found dön
        return ResponseEntity.notFound().build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails) {
            Long id = ((CustomUserDetails) authentication.getPrincipal()).getId();
            System.out.println("🔍 Aktif kullanıcı ID: " + id);
            return id;
        }
        System.out.println("❌ Kullanıcı bulunamadı veya login değil.");
        return null;
    }

}