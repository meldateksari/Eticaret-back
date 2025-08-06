package com.meldateksari.eticaret.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long productId;
    private Long userId;
    private String username;
    private Byte rating;
    private String comment;
    private LocalDateTime createdAt;
}
