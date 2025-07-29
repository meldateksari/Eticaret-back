package com.meldateksari.eticaret.dto;

import com.meldateksari.eticaret.model.Order;
import com.meldateksari.eticaret.model.OrderItem;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setStatus(order.getStatus());
        if (order.getShippingAddress() != null) {
            orderDto.setShippingAddressId(order.getShippingAddress().getId());
        }
        if (order.getBillingAddress() != null) {
            orderDto.setBillingAddressId(order.getBillingAddress().getId());
        }
        if (order.getOrderItems() != null) {
            orderDto.setOrderItems(order.getOrderItems().stream()
                    .map(OrderMapper::toOrderItemDto)
                    .collect(Collectors.toList()));
        }
        return orderDto;
    }

    public static OrderItemDto toOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setProductId(orderItem.getProduct().getId());
        orderItemDto.setProductName(orderItem.getProduct().getName());
        orderItemDto.setQuantity(orderItem.getQuantity());
        orderItemDto.setPrice(orderItem.getPriceAtPurchase());
        return orderItemDto;
    }
}
