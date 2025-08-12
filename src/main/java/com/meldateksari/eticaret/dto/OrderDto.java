package com.meldateksari.eticaret.dto;

import com.meldateksari.eticaret.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private String orderDate;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Long shippingAddressId;
    private Long billingAddressId;
    private List<OrderItemDto> orderItems;

}
