package com.meldateksari.eticaret.dto;

import lombok.Data;

@Data
public class CreateWishlistItemRequest {
    private Long userId;
    private Long productId;
}