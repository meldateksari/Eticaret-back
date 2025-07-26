package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.model.Order;
import com.meldateksari.eticaret.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long userId,
                                             @RequestParam(required = false) Long shippingAddressId,
                                             @RequestParam(required = false) Long billingAddressId,
                                             @RequestParam BigDecimal totalAmount,
                                             @RequestParam String status,
                                             @RequestParam String paymentStatus,
                                             @RequestParam(required = false) String trackingNumber) {

        return ResponseEntity.ok(orderService.createOrder(
                userId,
                shippingAddressId,
                billingAddressId,
                totalAmount,
                status,
                paymentStatus,
                trackingNumber
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
