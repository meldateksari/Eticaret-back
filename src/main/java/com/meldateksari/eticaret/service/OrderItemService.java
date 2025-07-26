package com.meldateksari.eticaret.service;

import com.meldateksari.eticaret.model.OrderItem;
import com.meldateksari.eticaret.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItem save(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void delete(Long id) {
        orderItemRepository.deleteById(id);
    }
}
