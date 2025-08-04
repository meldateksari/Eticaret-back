package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.dto.CreateOrderRequest;
import com.meldateksari.eticaret.dto.OrderDto;
import com.meldateksari.eticaret.dto.OrderMapper;
import com.meldateksari.eticaret.enums.PaymentStatus;
import com.meldateksari.eticaret.model.*;
import com.meldateksari.eticaret.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartRepository cartRepository;


    public OrderService(OrderRepository orderRepository, UserRepository userRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }
    @Transactional
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
                .totalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO)
                .status(request.getStatus() != null ? request.getStatus() : "CREATED")
                .paymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PENDING)
                .trackingNumber(request.getTrackingNumber() != null ? request.getTrackingNumber() : UUID.randomUUID().toString())
                .build();


        Order savedOrder = orderRepository.save(order);
        //cart tablosundan ürünleri getir
        //Orderitems a kaydet
        //cart ürünlerini sil


        // 1. Kullanıcının sepetini bul
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2. Sepetteki ürünleri getir
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. OrderItems'a kaydet
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            orderItems.add(orderItem);
        }
        System.out.println("Kayıt ediliyor...");
        orderItemRepository.saveAll(orderItems);
        System.out.println("Kayıt başarılı");

        // 4. Sepeti temizle
        cartItemRepository.deleteByCartId(cart.getId());



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

    public OrderDto updatePayment(Long orderId, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
        return OrderMapper.toOrderDto(order);
    }
}
