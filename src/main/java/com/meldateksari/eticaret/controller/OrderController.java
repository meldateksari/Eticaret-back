package com.meldateksari.eticaret.controller;

import com.meldateksari.eticaret.dto.CreateOrderRequest;
import com.meldateksari.eticaret.dto.OrderDto;
import com.meldateksari.eticaret.enums.OrderStatus;
import com.meldateksari.eticaret.enums.PaymentStatus;
import com.meldateksari.eticaret.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/updatePayment/{orderId}/{paymentStatus}")
    public ResponseEntity<OrderDto> updatePayment(@PathVariable Long orderId,
                                                  @PathVariable String paymentStatus) {
        PaymentStatus ps = PaymentStatus.from(paymentStatus); // aşağıdaki helper
        return ResponseEntity.ok(orderService.updatePayment(orderId, ps));
    }

    @PostMapping("/updateStatus/{orderId}/{status}")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long orderId, @PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }
}
