package com.meldateksari.eticaret.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardDto {
    private Long id;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private Long userId;
}

