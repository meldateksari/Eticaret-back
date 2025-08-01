package com.meldateksari.eticaret.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WishlistDto {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private List<WishlistItemDto> items;
}