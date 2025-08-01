package com.meldateksari.eticaret.dto;

import lombok.Data;
import java.util.List;

@Data
public class WishlistResponse {
    private Long id;
    private Long userId;
    private List<WishlistItemDto> items;
}