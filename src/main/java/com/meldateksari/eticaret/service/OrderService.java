package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.Address;
import com.meldateksari.eticaret.model.Order;
import com.meldateksari.eticaret.model.User;
import com.meldateksari.eticaret.repository.AddressRepository;
import com.meldateksari.eticaret.repository.OrderRepository;
import com.meldateksari.eticaret.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    public Order createOrder(Long userId,
                             Long shippingAddressId,
                             Long billingAddressId,
                             BigDecimal totalAmount,
                             String status,
                             String paymentStatus,
                             String trackingNumber) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address shippingAddress = shippingAddressId != null ?
                addressRepository.findById(shippingAddressId)
                        .orElseThrow(() -> new RuntimeException("Shipping address not found")) : null;

        Address billingAddress = billingAddressId != null ?
                addressRepository.findById(billingAddressId)
                        .orElseThrow(() -> new RuntimeException("Billing address not found")) : null;

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .totalAmount(totalAmount)
                .status(status)
                .paymentStatus(paymentStatus)
                .trackingNumber(trackingNumber)
                .build();

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
