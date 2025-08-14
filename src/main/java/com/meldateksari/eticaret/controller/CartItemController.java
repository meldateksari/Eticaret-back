package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.CartItemDto;
import com.meldateksari.eticaret.model.CartItem;
import com.meldateksari.eticaret.service.CartItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemDto>> getAllItems() {
        List<CartItemDto> items = cartItemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<CartItemDto> addOrUpdateCartItem(@RequestParam Long cartId,
                                                           @RequestParam Long productId,
                                                           @RequestParam(defaultValue = "1") int quantity) {
        CartItem item = cartItemService.addOrUpdateCartItem(cartId, productId, quantity);
        return ResponseEntity.ok(convertToCartItemDto(item));
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartItemDto>> getItemsByCart(@PathVariable Long cartId) {
        List<CartItem> items = cartItemService.getCartItems(cartId);
        List<CartItemDto> dtoList = items.stream()
                .map(this::convertToCartItemDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        try {
            cartItemService.removeItem(itemId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata: " + e.getMessage());
        }
    }


    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
        return ResponseEntity.noContent().build();
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
