package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.CreateWishlistItemRequest;
import com.meldateksari.eticaret.dto.WishlistDto;
import com.meldateksari.eticaret.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WishlistDto> getWishlistByUserId(@PathVariable Long userId) {
        WishlistDto wishlistDto = wishlistService.getOrCreateUserWishlist(userId);
        return ResponseEntity.ok(wishlistDto);
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistDto> addProductToWishlist(@RequestBody CreateWishlistItemRequest request) {
        WishlistDto wishlistDto = wishlistService.addProductToWishlist(request.getUserId(), request.getProductId());
        return ResponseEntity.ok(wishlistDto);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeProductFromWishlist(@RequestBody CreateWishlistItemRequest request) {
        wishlistService.removeProductFromWishlist(request.getUserId(), request.getProductId());
        return ResponseEntity.noContent().build();
    }
}