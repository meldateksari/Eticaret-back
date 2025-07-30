package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.CartDto;
import com.meldateksari.eticaret.dto.CartItemDto;
import com.meldateksari.eticaret.model.Cart;
import com.meldateksari.eticaret.model.CartItem;
import com.meldateksari.eticaret.service.CartItemService;
import com.meldateksari.eticaret.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;

    public CartController(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<CartDto> createCart(@RequestParam Long userId) {
        Cart cart = cartService.createCart(userId);
        return ResponseEntity.ok(convertToCartDto(cart));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDto> getCartByUser(@PathVariable Long userId) {
        Optional<Cart> cart = cartService.getCartByUserId(userId);
        return cart.map(value -> ResponseEntity.ok(convertToCartDto(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/add-product")
    public ResponseEntity<CartItemDto> addProductToUserCart(@RequestParam Long userId,
                                                            @RequestParam Long productId,
                                                            @RequestParam(defaultValue = "1") int quantity) {
        Cart cart = cartService.getCartByUserId(userId)
                .orElseGet(() -> cartService.createCart(userId));

        CartItem cartItem = cartItemService.addOrUpdateCartItem(cart.getId(), productId, quantity);
        return ResponseEntity.ok(convertToCartItemDto(cartItem));
    }

    // ======================
    // DTO DÖNÜŞÜMLERİ AŞAĞIDA
    // ======================

    private CartDto convertToCartDto(Cart cart) {
        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .items(
                        cart.getItems().stream()
                                .map(this::convertToCartItemDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private CartItemDto convertToCartItemDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .imageUrl(item.getProduct().getImageUrl())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}
