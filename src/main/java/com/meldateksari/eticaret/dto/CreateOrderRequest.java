package com.meldateksari.eticaret.dto;

import com.meldateksari.eticaret.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderRequest {

    private Long userId;
    private Long shippingAddressId;
    private Long billingAddressId;
    private BigDecimal totalAmount;
    private String status;
    private PaymentStatus paymentStatus;
    private String trackingNumber;
}
