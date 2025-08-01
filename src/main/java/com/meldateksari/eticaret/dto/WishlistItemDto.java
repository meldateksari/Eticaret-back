package com.meldateksari.eticaret.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WishlistItemDto {
    private Long id;
    private Long productId;
    private LocalDateTime addedAt;
}