package com.meldateksari.eticaret.service;
import com.meldateksari.eticaret.dto.WishlistDto;
import com.meldateksari.eticaret.dto.WishlistItemDto;
import com.meldateksari.eticaret.model.Wishlist;
import com.meldateksari.eticaret.model.WishlistItem;
import com.meldateksari.eticaret.repository.WishlistItemRepository;
import com.meldateksari.eticaret.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public WishlistService(WishlistRepository wishlistRepository, WishlistItemRepository wishlistItemRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
    }

    // Kullanıcının istek listesini getirir veya yoksa oluşturur
    public WishlistDto getOrCreateUserWishlist(Long userId) {
        Optional<Wishlist> wishlistOptional = wishlistRepository.findByUserId(userId);
        Wishlist wishlist;

        if (wishlistOptional.isPresent()) {
            wishlist = wishlistOptional.get();
        } else {
            wishlist = new Wishlist();
            wishlist.setUserId(userId);
            wishlist.setCreatedAt(LocalDateTime.now());
            wishlist = wishlistRepository.save(wishlist);
        }

        return convertToDto(wishlist);
    }

    // İstek listesine ürün ekler
    @Transactional
    public WishlistDto addProductToWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUserId(userId);
                    newWishlist.setCreatedAt(LocalDateTime.now());
                    return wishlistRepository.save(newWishlist);
                });

        Optional<WishlistItem> existingItem = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId);
        if (existingItem.isEmpty()) {
            WishlistItem newItem = new WishlistItem();
            newItem.setWishlist(wishlist);
            newItem.setProductId(productId);
            newItem.setAddedAt(LocalDateTime.now());
            wishlistItemRepository.save(newItem);
            wishlist.getItems().add(newItem);
        }

        return convertToDto(wishlist);
    }

    // İstek listesinden ürün kaldırır
    @Transactional
    public void removeProductFromWishlist(Long userId, Long productId) {
        Optional<Wishlist> wishlistOptional = wishlistRepository.findByUserId(userId);

        if (wishlistOptional.isPresent()) {
            Wishlist wishlist = wishlistOptional.get();
            Optional<WishlistItem> wishlistItemOptional = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId);

            wishlistItemOptional.ifPresent(wishlistItemRepository::delete);
        }
    }

    // Entity'den DTO'ya dönüştürme metodu
    private WishlistDto convertToDto(Wishlist wishlist) {
        WishlistDto dto = new WishlistDto();
        dto.setId(wishlist.getId());
        dto.setUserId(wishlist.getUserId());
        dto.setCreatedAt(wishlist.getCreatedAt());

        if (wishlist.getItems() != null) {
            List<WishlistItemDto> itemDtos = wishlist.getItems().stream()
                    .map(this::convertItemToDto)
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        }
        return dto;
    }

    private WishlistItemDto convertItemToDto(WishlistItem item) {
        WishlistItemDto dto = new WishlistItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setAddedAt(item.getAddedAt());
        return dto;
    }
}