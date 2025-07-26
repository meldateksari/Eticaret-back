package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.model.CartItem;
import com.meldateksari.eticaret.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<CartItem> addOrUpdateCartItem(@RequestParam Long cartId,
                                                        @RequestParam Long productId,
                                                        @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartItemService.addOrUpdateCartItem(cartId, productId, quantity));
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartItem>> getItemsByCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartItemService.getCartItems(cartId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        cartItemService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
