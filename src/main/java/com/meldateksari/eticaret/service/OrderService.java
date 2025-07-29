package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.CreateOrderRequest;
import com.meldateksari.eticaret.dto.OrderDto;
import com.meldateksari.eticaret.dto.OrderMapper;
import com.meldateksari.eticaret.model.Address;
import com.meldateksari.eticaret.model.Order;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.AddressRepository;
import com.meldateksari.eticaret.repository.OrderRepository;
import com.meldateksari.eticaret.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public OrderDto createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address shippingAddress = request.getShippingAddressId() != null ?
                addressRepository.findById(request.getShippingAddressId())
                        .orElseThrow(() -> new RuntimeException("Shipping address not found")) : null;

        Address billingAddress = request.getBillingAddressId() != null ?
                addressRepository.findById(request.getBillingAddressId())
                        .orElseThrow(() -> new RuntimeException("Billing address not found")) : null;

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .totalAmount(request.getTotalAmount())
                .status(request.getStatus())
                .paymentStatus(request.getPaymentStatus())
                .trackingNumber(request.getTrackingNumber())
                .build();

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toOrderDto(savedOrder);
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderMapper.toOrderDto(order);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
